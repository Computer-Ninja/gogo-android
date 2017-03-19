package tattoo.gogo.app.gogo_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.Tracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.api.UploadResponse;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.utils.AnalyticsUtil;
import tattoo.gogo.app.gogo_android.utils.IntentUtils;

import static android.R.attr.name;

public class MainActivity extends AppCompatActivity implements
        ArtistArtworkListFragment.OnArtistArtworkFragmentInteractionListener,
        ArtistArtworkFragment.OnArtistArtworkFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "MainActivity";
    protected static final int PERMISSION_REQUEST_STORAGE = 2;
    public static final int GALLERY_PICTURE = 3;
    public static final int CAMERA_REQUEST = 4;
    private boolean isFabOpen;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fl_new_tattoo)
    View newTattoo;
    @BindView(R.id.fl_new_design)
    View newDesign;
    @BindView(R.id.fl_new_dreadlocks)
    View newDreadlocks;
    @BindView(R.id.fl_new_piercing)
    View newPiercing;
    @BindView(R.id.fl_new_henna)
    View newHenna;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.fl_loading) FrameLayout flLoading;
    private AlphaAnimation fadeOut;
    private AlphaAnimation fadeIn;
    private Tracker mTracker;
    private int mSavedOrientation;

    public static View getToolbarLogoIcon(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        createAnimations();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.actionbar_space_between_icon_and_title);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(view -> {
            animateFAB();
            //fabClicked(view);
        });
        newTattoo.setOnClickListener(v -> {
            animateFAB();
            String tag = ((GogoAndroid) getApplication()).getArtist() + "/tattoo/new";
            getSupportFragmentManager().beginTransaction()
                    .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                    .add(R.id.fragment_container, new NewTattooFragment(), tag)
                    .addToBackStack(tag)
                    .commit();
        });

        newPiercing.setOnClickListener(v -> {
            animateFAB();
            String tag = ((GogoAndroid) getApplication()).getArtist() + "/piercing/new";
            getSupportFragmentManager().beginTransaction()
                    .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                    .add(R.id.fragment_container, new NewPiercingFragment(), tag)
                    .addToBackStack(tag)
                    .commit();
        });

        newDesign.setOnClickListener(v -> {
            animateFAB();
            String tag = ((GogoAndroid) getApplication()).getArtist() + "/design/new";
            getSupportFragmentManager().beginTransaction()
                    .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                    .add(R.id.fragment_container, new NewDesignFragment(), tag)
                    .addToBackStack(tag)
                    .commit();
        });

        newHenna.setOnClickListener(v -> {
            animateFAB();
            String tag = ((GogoAndroid) getApplication()).getArtist() +"/henna/new";
            getSupportFragmentManager().beginTransaction()
                    .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                    .add(R.id.fragment_container, new NewHennaFragment(), tag)
                    .addToBackStack(tag)
                    .commit();
        });

        newDreadlocks.setOnClickListener(v -> {
            animateFAB();
            String tag = ((GogoAndroid) getApplication()).getArtist() +"/dreadlocks/new";
            getSupportFragmentManager().beginTransaction()
                    .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                    .add(R.id.fragment_container, new NewDreadlockFragment(), tag)
                    .addToBackStack(tag)
                    .commit();
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        View logoView = getToolbarLogoIcon(mToolbar);
        logoView.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TattooQrScannerActivity.class)));

        mTracker = ((GogoAndroid) getApplication()).getTracker();

        AnalyticsUtil.sendScreenName(mTracker, getString(R.string.app_name));

        hideLoading();
    }

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
    }

    @Override
    public void hideLoading() {
        if (fadeOut == null) {
            createAnimations();
        }
        flLoading.post(() -> flLoading.startAnimation(fadeOut));
    }

    @Override
    public void showLoading() {
        if (fadeIn == null) {
            createAnimations();
        }

        flLoading.post(() -> flLoading.startAnimation(fadeIn));
    }

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            newTattoo.startAnimation(fab_close);
            newTattoo.setClickable(false);
            newHenna.startAnimation(fab_close);
            newHenna.setClickable(false);
            newDesign.startAnimation(fab_close);
            newDesign.setClickable(false);
            newPiercing.startAnimation(fab_close);
            newPiercing.setClickable(false);
            newDreadlocks.startAnimation(fab_close);
            newDreadlocks.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            newTattoo.startAnimation(fab_open);
            newTattoo.setClickable(true);
            newHenna.startAnimation(fab_open);
            newHenna.setClickable(true);
            newDesign.startAnimation(fab_open);
            newDesign.setClickable(true);
            newPiercing.startAnimation(fab_open);
            newPiercing.setClickable(true);
            newDreadlocks.startAnimation(fab_open);
            newDreadlocks.setClickable(true);
            isFabOpen = true;

        }
    }

    public FloatingActionButton getFloatingActionButton() {
        return (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                startActivity(new Intent(this, TattooQrScannerActivity.class));
            } else {
                onBackPressed();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isFabOpen) {
            animateFAB();
        } else {
            fab.setOnClickListener(v -> animateFAB());

            super.onBackPressed();
        }
    }

    @Override
    public void onListFragmentInteraction(WeakReference<Fragment> fr, String artistName, ArtWork artWork) {
        if (fr.get() == null) {
            Log.d(TAG, "loadThumbnail: Fragment is null");
            return;
        }
        String tag = artistName + "/" + artWork.getType() + "/" + artWork.getLink();
        getSupportFragmentManager().beginTransaction()
                .hide(fr.get())
                .add(R.id.fragment_container, ArtistArtworkFragment.newInstance(artistName, artWork), tag)
                .addToBackStack(tag)
                .commit();
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
                String title = getString(R.string.app_name_short) + "/" + bse.getName();
                if (title.length() > 24 /* || And portrait? */) {
                    setActionBarTitle("/" + bse.getName());
                } else {
                    setActionBarTitle(title);
                }
                getSupportActionBar().setDisplayUseLogoEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setIcon(0);
                Log.i(TAG, "Setting screen name: " + name);
                AnalyticsUtil.sendScreenName(mTracker, title);
            }
        }

    }

    @Override
    public void loadThumbnail(final WeakReference<Fragment> fr, final ArtworkRecyclerViewAdapter.ViewHolder holder) {
        Log.d(TAG, "loadThumbnail: " + holder.mItem.getTitle());
        if (fr.get() == null) {
            Log.d(TAG, "loadThumbnail: Fragment is null");
            return;
        }
        final String url = GogoConst.IPFS_GATEWAY_URL + holder.mItem.getImageIpfs();
        holder.ivThumbnail.setVisibility(View.VISIBLE);
        Display display = getWindowManager().getDefaultDisplay();
        final DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        Glide.with(fr.get())
                .load(url)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.doge)
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                //.override(outMetrics.widthPixels, outMetrics.heightPixels)
                .into(holder.ivThumbnail);

        holder.mView.setOnLongClickListener(view -> {
            showContextMenu(holder.ivThumbnail, holder.mItem.getImageIpfs(),
                    (hash, iv) -> loadThumbnail(fr, holder));
            return true;
        });
    }

    @Override
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
                                sharePhoto(iv);
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
                if (filePath != null) IntentUtils.broadcastImageUpdate(MainActivity.this, filePath);
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
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.PERMISSION_REQUEST_STORAGE);
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

    @Override
    public void savePhoto(final String imageIpfs) {
        new PhotoSave(false).execute(imageIpfs);
    }

    @Override
    public void shareOriginalPhoto(String hash) {
        new PhotoSave(true).execute(hash);
    }

    @Override
    public void sharePhoto(View view) {
        showLoading();
        broadcastBitmapToApps(Collections.singletonList(view));
        hideLoading();

    }

    @Override
    public void sharePhotos(List<View> views) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                broadcastBitmapToApps(views);
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
        return Uri.parse(path);
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
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

    public void broadcastBitmapToApps(List<View> views) {
        boolean multipleFiles = true;
        if (views.size() == 1) {
            multipleFiles = false;
        }

        Intent i = new Intent(multipleFiles ? Intent.ACTION_SEND_MULTIPLE : Intent.ACTION_SEND);
        i.setType("image/*");

        ArrayList<Uri> files = new ArrayList<>();

        for (View view : views) {
            files.add(getImageUri(this, getBitmapFromView(view)));
        }

        if (multipleFiles) {
            i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        } else {
            i.putExtra(Intent.EXTRA_STREAM, files.get(0));
        }

        try {
            startActivity(Intent.createChooser(i, getString(R.string.share_to)));
        } catch (android.content.ActivityNotFoundException ex) {

            Snackbar.make(mToolbar, R.string.something_went_wrong, Snackbar.LENGTH_SHORT).show();
        }


    }

    Bitmap bitmap;

    String selectedImagePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSavedOrientation = getRequestedOrientation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        showLoading();

        bitmap = null;
        boolean isFinal = false;
        selectedImagePath = null;
        if (requestCode > 1000) {
            requestCode -= 1000;
            isFinal = true;
        }

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            onCameraPhotoResult(data, isFinal);

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            onGalleryPhotoResult(data, isFinal);
        }
    }

    private void onGalleryPhotoResult(Intent data, boolean isFinal) {
        if (data != null) {

            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath,
                    null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            selectedImagePath = c.getString(columnIndex);
            c.close();

            bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
            // preview image
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/4, bitmap.getHeight()/4, false);

            uploadFile(bitmap, isFinal);

        } else {
            Snackbar.make(mToolbar, R.string.gallery_cancelled, Toast.LENGTH_SHORT).show();
        }
    }

    private void onCameraPhotoResult(Intent data, boolean isFinal) {

        File f = new File(Environment.getExternalStorageDirectory().toString());
        for (File temp : f.listFiles()) {
            if (temp.getName().equals("temp.jpg")) {
                f = temp;
                break;
            }
        }

        if (!f.exists()) {
            Snackbar.make(mToolbar, R.string.error_camera_capture, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/4,
                    bitmap.getHeight()/4, true);

            int rotate = 0;
            ExifInterface exif = new ExifInterface(f.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);

            //img_logo.setImageBitmap(bitmap);
            storeImageToSDCard(bitmap);
            uploadFile(bitmap, isFinal);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void showError(Exception e) {
        e.printStackTrace();
        hideLoading();
        setRequestedOrientation(mSavedOrientation);
        Snackbar.make(mToolbar, e.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    private void uploadFile(Bitmap bitmap, boolean isFinal) {

        File file = new File(selectedImagePath);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("uploadfile", file.getName(), requestFile);

        //MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file",file2.getName(),requestFile);
        GogoApi.getApi().upload("gogo", "chushangfeng", GogoConst.watermarkDateFormat.format(new Date()), multipartBody).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                onFileUploadSuccess(response, bitmap, isFinal);
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.d("failure", "message = " + t.getMessage());
                Log.d("failure", "cause = " + t.getCause());
                Snackbar.make(mToolbar, "Failure: " + t, Snackbar.LENGTH_LONG).show();
                hideLoading();
                setRequestedOrientation(mSavedOrientation);
            }
        });
    }

    private void onFileUploadSuccess(Response<UploadResponse> response, Bitmap bitmap, boolean isFinal) {
        Log.d("Success", "Code: " + response.code());
        Log.d("Success", "Message: " + response.message());
        hideLoading();
        setRequestedOrientation(mSavedOrientation);
        String hash = response.body().getHash();
        Log.d("Success", "Hash: " + hash);
        Snackbar.make(mToolbar, "Success: " + hash, Snackbar.LENGTH_LONG).show();
        //IntentUtils.opentUrl(MainActivity.this, GogoConst.IPFS_GATEWAY_URL + hash);

        NewWorkFragment fr = (NewWorkFragment) getSupportFragmentManager().findFragmentByTag("gogo/tattoo/new");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
         fr = (NewWorkFragment) getSupportFragmentManager().findFragmentByTag("gogo/design/new");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
         fr = (NewWorkFragment) getSupportFragmentManager().findFragmentByTag("gogo/henna/new");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
         fr = (NewWorkFragment) getSupportFragmentManager().findFragmentByTag("gogo/piercing/new");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
    }

    private void storeImageToSDCard(Bitmap processedBitmap) throws IOException {
        OutputStream output;
        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();
        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath() + "/gogo.tattoo/");
        dir.mkdirs();

        String imageName = "gogo.tattoo_" + System.currentTimeMillis() + ".jpg";
        // Create a name for the saved image
        File file = new File(dir, imageName);
        if (file.exists()) {
            file.delete();
            file.createNewFile();
        } else {
            file.createNewFile();

        }

        output = new FileOutputStream(file);

        // Compress into png format image from 0% - 100%
        processedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        output.flush();
        output.close();

        int size = Integer.parseInt(String.valueOf(file.length() / 1024));
        System.out.println("size ===>>> " + size);
        System.out.println("file.length() ===>>> " + file.length());

        selectedImagePath = file.getAbsolutePath();
    }

}

