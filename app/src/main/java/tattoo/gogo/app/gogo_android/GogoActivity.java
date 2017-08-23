package tattoo.gogo.app.gogo_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.analytics.Tracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import tattoo.gogo.app.gogo_android.utils.AnalyticsUtil;
import tattoo.gogo.app.gogo_android.utils.IntentUtils;

import static android.R.attr.name;

/**
 * Created by delirium on 3/21/17.
 */

abstract class GogoActivity extends AppCompatActivity implements
        FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "GogoActivity";
    protected static final int PERMISSION_REQUEST_STORAGE = 2;
    protected static final int PERMISSION_REQUEST_CAMERA = 1;
    public static final int GALLERY_PICTURE = 3;
    public static final int GALLERY_VIDEO = 5;
    public static final int CAMERA_REQUEST = 4;
    boolean isFabOpen;
    Animation fab_open;
    Animation fab_close;
    Animation rotate_forward;
    Animation rotate_backward;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.fl_loading)
    FrameLayout flLoading;

    private AlphaAnimation fadeOut;
    private AlphaAnimation fadeIn;
    Tracker mTracker;
    int mSavedOrientation;
    protected String mArtworkType = "tattoo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        createAnimations();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.actionbar_space_between_icon_and_title);

        View logoView = getToolbarLogoIcon(mToolbar);
        if (logoView != null) {
            logoView.setOnClickListener(v -> startActivity(new Intent(GogoActivity.this, TattooQrScannerActivity.class)));
        }
        mTracker = ((GogoAndroid) getApplication()).getTracker();

        AnalyticsUtil.sendScreenName(mTracker, getString(R.string.app_name));

        hideLoading();
    }

    public static View getToolbarLogoIcon(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }

    abstract int getLayout();

    private void createAnimations() {
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(400);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                flLoading.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(1000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                flLoading.setVisibility(View.VISIBLE);
            }
        });
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

    }

    public void hideLoading() {
        if (fadeOut == null) {
            createAnimations();
        }
        flLoading.post(() -> flLoading.startAnimation(fadeOut));
    }

    public void showLoading() {
        if (fadeIn == null) {
            createAnimations();
        }

        flLoading.post(() -> flLoading.startAnimation(fadeIn));
    }

    public void onBackStackChanged() {
        FragmentManager manager = getSupportFragmentManager();
        //mAppbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        mAppbar.setExpanded(true, true);
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        if (manager != null) {
            if (manager.getBackStackEntryCount() == 0) {
                hideLoading();
                setActionBarTitle(getString(R.string.app_name_short));
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                getSupportActionBar().setIcon(R.drawable.actionbar_space_between_icon_and_title);
                AnalyticsUtil.sendScreenName(mTracker, getString(R.string.app_name_short));
            } else {
                FragmentManager.BackStackEntry bse =
                        manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1);
                setGogoTitle(bse.getName());

                getSupportActionBar().setDisplayUseLogoEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setIcon(0);
                Log.i(TAG, "Setting screen name: " + name);
            }
        }

    }

    protected void setGogoTitle(String title) {
        AnalyticsUtil.sendScreenName(mTracker, title);
        String t = getString(R.string.app_name_short) + "/" + title;
        if (t.length() > 25 /* || And portrait? */) {
            setActionBarTitle("/" + title);
        } else {
            setActionBarTitle(t);
        }
    }

    protected void setGogoTitle() {
        String title = ((GogoAndroid) getApplication()).getArtist().getLink() + "/" + mArtworkType.toLowerCase();
        setGogoTitle(title);
    }

    public void showContextMenu(final ImageView iv, final String hash, final ArtistArtworkFragment.OnImageRefreshListener refresh) {
        ArrayList<String> items = new ArrayList<>();
        items.add(getString(R.string.save_to_phone));
        items.add(getString(R.string.share_to));
        items.add(getString(R.string.share_original_to));
        items.add(getString(R.string.refresh_image));
        new AlertDialog.Builder(this)
                .setAdapter(new ArrayAdapter<>(this,
                                R.layout.selectable_list_item, items),
                        (dialogInterface, position) -> {
                            showLoading();
                            if (position == 0) {
                                savePhoto(hash);
                                AnalyticsUtil.sendEvent(mTracker, "context_menu", "save_photo", hash);
                            } else if (position == 1) {
                                sharePhoto(iv, "");
                                AnalyticsUtil.sendEvent(mTracker, "context_menu", "share_photo", hash);
                            } else if (position == 2) {
                                shareOriginalPhoto(hash);
                                AnalyticsUtil.sendEvent(mTracker, "context_menu", "share_original_photo", hash);
                            } else {
                                refresh.onImageRefresh(hash, iv);
                                hideLoading();
                                AnalyticsUtil.sendEvent(mTracker, "context_menu", "refresh_photo", hash);
                            }
                        })
                .setOnCancelListener(dialog -> {
                    hideLoading();
                })
                .show();
    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // This is because the dialog was cancelled when we recreated the activity.
        if (permissions.length == 0 || grantResults.length == 0)
            return;

        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Snackbar.make(mToolbar, "STORAGE PERMISSION GRANTED", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mToolbar, "No permission to store files", Snackbar.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    protected boolean haveStoragePermission() {
        return Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    protected boolean haveCameraPermission() {
        return Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }



    private class PhotoSave extends AsyncTask<String, Void, String> {

        private final boolean mShare;
        public Bitmap bitmap;

        public PhotoSave(boolean share) {
            mShare = share;
        }

        @Override
        protected String doInBackground(String... imageIpfs) {
            String filePath;
            try {
                filePath = saveImageToFile(imageIpfs[0]);
                if (filePath != null) IntentUtils.broadcastImageUpdate(GogoActivity.this, filePath);
            } catch (Exception x) {
                x.printStackTrace();
                return null;
            }
            return filePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
            if (mShare) {
                Snackbar.make(mToolbar, R.string.photo_download_for_sharing, Snackbar.LENGTH_LONG).show();

            } else {
                Snackbar.make(mToolbar, R.string.photo_download_started, Snackbar.LENGTH_LONG).show();

            }
        }

        @Override
        protected void onPostExecute(String filePath) {
            super.onPostExecute(filePath);
            hideLoading();
            if (filePath != null) {
                Snackbar.make(mToolbar, R.string.photo_download_succeess, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mToolbar, R.string.photo_download_fail, Snackbar.LENGTH_SHORT).show();
            }
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),
                    bitmap, "", null);
            Uri bitmapUri = Uri.parse(bitmapPath);

            if (mShare) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                startActivity(Intent.createChooser(intent, "Sharing ..."));
            }
        }


        private String saveImageToFile(String imageIpfs) throws Exception {
            if (!haveStoragePermission()) {
                requestPermission(PERMISSION_REQUEST_STORAGE);
                throw new Exception("No permission");
            }

            URL imageurl = new URL(GogoConst.IPFS_GATEWAY_URL + imageIpfs);
            bitmap = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + File.separator + imageIpfs + ".jpg";
            File f = new File(filePath);
            f.createNewFile();
            //write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            // remember close de FileOutput
            fo.close();
            return filePath;
        }
    }

    protected void requestPermission(int perm) {
        if (perm == PERMISSION_REQUEST_STORAGE) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, perm);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, perm);
        }
    }

    public void savePhoto(final String imageIpfs) {
        if (!haveStoragePermission()) {
            requestPermission(PERMISSION_REQUEST_STORAGE);
            return;
        }
        new PhotoSave(false).execute(imageIpfs);
    }

    public void shareOriginalPhoto(String hash) {
        if (!haveStoragePermission()) {
            requestPermission(PERMISSION_REQUEST_STORAGE);
            return;
        }
        new PhotoSave(true).execute(hash);
    }

    public void sharePhoto(View view, String text) {
        if (!haveStoragePermission()) {
            requestPermission(PERMISSION_REQUEST_STORAGE);
            return;
        }
        showLoading();
        broadcastBitmapToApps(Collections.singletonList(view), text);
        hideLoading();

    }

    public void sharePhotos(List<View> views, String text) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (haveStoragePermission()) {
                    showLoading();
                } else {
                    requestPermission(PERMISSION_REQUEST_STORAGE);
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (haveStoragePermission()) {
                    broadcastBitmapToApps(views, text);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hideLoading();
            }
        }.execute();
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        if (path != null)
            return Uri.parse(path);
        else
            return null;
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        if (view.getWidth() == 0 || view.getHeight() == 0) {
            return null;
        }
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public void broadcastBitmapToApps(List<View> views, String text) {
        boolean multipleFiles = true;
        if (views.size() == 1) {
            multipleFiles = false;
        }

        Intent i = new Intent(multipleFiles ? Intent.ACTION_SEND_MULTIPLE : Intent.ACTION_SEND);
        i.setType("image/*");

        ArrayList<Uri> files = new ArrayList<>();

        System.gc();
        for (int j = 0; j < views.size(); j++) {
            View view = views.get(j);
            try {
                Bitmap bitmap = getBitmapFromView(view);
                if (bitmap == null) {
                    continue;
                }
                Uri uri = getImageUri(this, bitmap);
                if (uri != null) {
                    files.add(uri);
                }
            } catch (OutOfMemoryError x) {
                System.gc();
            }
        }

        if (multipleFiles) {
            i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        } else {
            i.putExtra(Intent.EXTRA_STREAM, files.get(0));
        }
        i.putExtra(Intent.EXTRA_TEXT, text);

        try {
            startActivity(Intent.createChooser(i, getString(R.string.share_to)));
        } catch (android.content.ActivityNotFoundException ex) {

            Snackbar.make(mToolbar, R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
        }


    }

    protected void showError(Exception e) {
        e.printStackTrace();
        hideLoading();
        setRequestedOrientation(mSavedOrientation);
        Snackbar.make(mToolbar, e.getMessage(), Snackbar.LENGTH_LONG).show();
    }


    @NonNull
    Resources getLocalizedResources(Context context, Locale desiredLocale) {
        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(desiredLocale);
        Context localizedContext = context.createConfigurationContext(conf);
        return localizedContext.getResources();
    }

    public FloatingActionButton getFloatingActionButton() {
        return (FloatingActionButton) findViewById(R.id.fab);
    }
}
