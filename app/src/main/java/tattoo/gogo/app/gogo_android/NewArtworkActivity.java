package tattoo.gogo.app.gogo_android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.api.UploadResponse;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Design;
import tattoo.gogo.app.gogo_android.model.Dreadlocks;
import tattoo.gogo.app.gogo_android.model.Henna;
import tattoo.gogo.app.gogo_android.model.Piercing;
import tattoo.gogo.app.gogo_android.model.Tattoo;
import tattoo.gogo.app.gogo_android.utils.AnalyticsUtil;


/**
 * Created by delirium on 3/28/17.
 */

public class NewArtworkActivity extends GogoActivity
        implements NewWorkListFragment.OnNewWorkFragmentInteractionListener {
    private static final String TAG = "NewArtworkActivity";
    public static final String ARG_ARTIST = "artist";
    public static final String ARG_ARTWORK_TYPE = "type";
    private String mArtistName = "gogo";

    @Override
    int getLayout() {
        return R.layout.activity_new_artwork;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtworkType = getIntent().getStringExtra(ARG_ARTWORK_TYPE);
//
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, new NewTattooFragment(), null)
//                .commit();
        mArtistName = ((GogoAndroid) getApplication()).getArtist();
        NewWorkListFragment listFr = NewWorkListFragment.newInstance(1,
                mArtistName,
                mArtworkType);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, listFr, null)
                .commit();

        setGogoTitle();
    }


    public void showContextMenu(final ImageView iv, final String hash, final ArtistArtworkFragment.OnImageRefreshListener refresh, ArtWork artWork) {
        ArrayList<String> items = new ArrayList<>();
        items.add(getString(R.string.save_to_phone));
        items.add(getString(R.string.share_to));
        items.add(getString(R.string.share_original_to));
        items.add(getString(R.string.refresh_image));
        items.add(getString(R.string.delete));

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
                            } else if (position == 3) {
                                refresh.onImageRefresh(hash, iv);
                                hideLoading();
                                AnalyticsUtil.sendEvent(mTracker, "context_menu", "refresh_photo", hash);
                            } else {
                                deleteArtwork(artWork.getShortName());
                            }
                        })
                .setOnCancelListener(dialog -> {
                    hideLoading();
                })
                .show();
    }

    @Override
    public void loadThumbnail(WeakReference<Fragment> fr, NewWorkRecyclerViewAdapter.ViewHolder holder) {
        Log.d(TAG, "loadThumbnail: " + holder.mItem.getTitle());
        if (fr.get() == null) {
            Log.d(TAG, "loadThumbnail: Fragment is null");
            return;
        }
        String hash = holder.mItem.getImageIpfs();
        if ((hash == null || hash.isEmpty()) && !holder.mItem.getImagesIpfs().isEmpty()) {
            hash = holder.mItem.getImagesIpfs().get(0);
        }

        holder.mView.setOnLongClickListener(view -> {
            showContextMenu(holder.ivThumbnail, holder.mItem.getImageIpfs(),
                    (h, iv) -> loadThumbnail(fr, holder), holder.mItem);
            return true;
        });

        if (hash == null || hash.isEmpty()) {
            holder.ivThumbnail.setImageResource(R.drawable.doge);
            return;
        }
        final String url = GogoConst.IPFS_GATEWAY_URL + hash;
        Display display = getWindowManager().getDefaultDisplay();
        final DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        Glide.with(fr.get())
                .load(url)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.doge)
                .into(holder.ivThumbnail);
    }

    @Override
    public void onListFragmentInteraction(WeakReference<Fragment> tWeakReference, String mArtistName, ArtWork artWork) {

        NewWorkFragment fr = null;
        if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_TATTOO)) {
            fr = NewTattooFragment.newInstance(artWork);
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_DESIGN)) {
            fr = NewDesignFragment.newInstance(artWork);
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_HENNA)) {
            fr = NewHennaFragment.newInstance(artWork);
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_PIERCING)) {
            fr = NewPiercingFragment.newInstance(artWork);
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_DREADLOCKS)) {
            fr = NewDreadlockFragment.newInstance(artWork);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fr, ((GogoAndroid) getApplication()).getArtist() + "/" + mArtworkType)
                .addToBackStack(null)
                .commit();
    }

    public void deleteArtwork(String artworkName) {
        showLoading();
        Callback<List<ArtWork>> callback = new Callback<List<ArtWork>>() {
            @Override
            public void onResponse(Call<List<ArtWork>> call, Response<List<ArtWork>> response) {
                finish();
                Intent i = new Intent(NewArtworkActivity.this, NewArtworkActivity.class);
                i.putExtra(NewArtworkActivity.ARG_ARTIST, ((GogoAndroid) getApplication()).getArtist());
                i.putExtra(NewArtworkActivity.ARG_ARTWORK_TYPE, mArtworkType);
                startActivity(i);
            }

            @Override
            public void onFailure(Call<List<ArtWork>> call, Throwable t) {
                hideLoading();
                Snackbar.make(mToolbar, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
            }
        };
        if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_TATTOO)) {
            GogoApi.getApi().deleteTattoo(mArtistName, artworkName).enqueue(callback);
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_DESIGN)) {
            GogoApi.getApi().deleteDesign(mArtistName, artworkName).enqueue(callback);
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_HENNA)) {
            GogoApi.getApi().deleteHenna(mArtistName, artworkName).enqueue(callback);
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_PIERCING)) {
            GogoApi.getApi().deletePiercing(mArtistName, artworkName).enqueue(callback);
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_DREADLOCKS)) {
            GogoApi.getApi().deleteDreadlocks(mArtistName, artworkName).enqueue(callback);
        }
    }

    public void startNewWork(String mArtworkType, boolean addToBackStack) {

        NewWorkFragment fr = null;
        if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_TATTOO)) {
            fr = NewTattooFragment.newInstance(new Tattoo());
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_DESIGN)) {
            fr = NewDesignFragment.newInstance(new Design());
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_HENNA)) {
            fr = NewHennaFragment.newInstance(new Henna());
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_PIERCING)) {
            fr = NewPiercingFragment.newInstance(new Piercing());
        } else if (mArtworkType.equals(ArtFragment.ARTWORK_TYPE_DREADLOCKS)) {
            fr = NewDreadlockFragment.newInstance(new Dreadlocks());
        }
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.fragment_container, fr, ((GogoAndroid) getApplication()).getArtist() + "/" + mArtworkType);
        if (addToBackStack) {
            tr.addToBackStack(null);
        }
        tr.commit();
    }


    public void setLatestLabel(Label latestLabel) {
        this.latestLabel = latestLabel;
    }

    Label latestLabel;

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

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                onCameraPhotoResult(isFinal);

            } else if (requestCode == GALLERY_PICTURE) {
                onGalleryPhotoResult(data, isFinal);
            } else {
                Uri selectedVideo = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedVideo, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();
                uploadVideo();
            }
        }
    }

    private void onVideoChosen(Intent data) {
        Log.d(TAG, "onVideoChosen() called with: data = [" + data + "]");
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
            if (bitmap.getWidth() > 2048 || bitmap.getHeight() > 2048) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);

                try {
                    storeImageToSDCard(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadFile(bitmap, isFinal);
            }
            uploadFile(bitmap, isFinal);

        } else {
            Snackbar.make(mToolbar, R.string.gallery_cancelled, Toast.LENGTH_SHORT).show();
        }
    }

    private void onCameraPhotoResult(boolean isFinal) {

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
            if (bitmap.getWidth() > 2048 || bitmap.getHeight() > 2048) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4,
                        bitmap.getHeight() / 4, true);
            }

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
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    matrix, true);

            storeImageToSDCard(bitmap);
            uploadFile(bitmap, isFinal);
        } catch (Exception e) {
            showError(e);
        }
    }

    private void uploadFile(Bitmap bitmap, boolean isFinal) {

        File file = new File(selectedImagePath);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("uploadfile", file.getName(), requestFile);

        //MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file",file2.getName(),requestFile);
        GogoApi.getApi().upload(((GogoAndroid) getApplication()).getArtist(), latestLabel.getMadeAt(), GogoConst.watermarkDateFormat.format(new Date()), multipartBody).enqueue(new Callback<UploadResponse>() {
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

    private void uploadVideo() {

        File file = new File(selectedImagePath);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("uploadfile", file.getName(), requestFile);

        //MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file",file2.getName(),requestFile);
        GogoApi.getApi().upload(((GogoAndroid) getApplication()).getArtist(), latestLabel.getMadeAt(), GogoConst.watermarkDateFormat.format(new Date()), multipartBody).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                onVideoUploadSuccess(response);
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

    private void onVideoUploadSuccess(Response<UploadResponse> response) {
        Log.d("Success", "Code: " + response.code());
        Log.d("Success", "Message: " + response.message());
        hideLoading();
        setRequestedOrientation(mSavedOrientation);
        String hash = response.body().getHash();
        Log.d("Success", "Hash: " + hash);
        Snackbar.make(mToolbar, "Success: " + hash, Snackbar.LENGTH_LONG).show();
        //IntentUtils.opentUrl(MainActivity.this, GogoConst.IPFS_GATEWAY_URL + hash);

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

        NewWorkFragment fr =
                (NewWorkFragment) getSupportFragmentManager()
                        .findFragmentByTag(((GogoAndroid) getApplication()).getArtist() + "/tattoo");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
        fr = (NewWorkFragment) getSupportFragmentManager()
                .findFragmentByTag(((GogoAndroid) getApplication()).getArtist() + "/design");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
        fr = (NewWorkFragment) getSupportFragmentManager()
                .findFragmentByTag(((GogoAndroid) getApplication()).getArtist() + "/henna");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
        fr = (NewWorkFragment) getSupportFragmentManager()
                .findFragmentByTag(((GogoAndroid) getApplication()).getArtist() + "/piercing");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
        fr = (NewWorkFragment) getSupportFragmentManager()
                .findFragmentByTag(((GogoAndroid) getApplication()).getArtist() + "/dreadlocks");
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
