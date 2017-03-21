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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.api.UploadResponse;

public class MainActivity extends GogoActivity {
    private static final String TAG = "MainActivity";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            String tag = ((GogoAndroid) getApplication()).getArtist() + "/henna/new";
            getSupportFragmentManager().beginTransaction()
                    .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                    .add(R.id.fragment_container, new NewHennaFragment(), tag)
                    .addToBackStack(tag)
                    .commit();
        });

        newDreadlocks.setOnClickListener(v -> {
            animateFAB();
            String tag = ((GogoAndroid) getApplication()).getArtist() + "/dreadlocks/new";
            getSupportFragmentManager().beginTransaction()
                    .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                    .add(R.id.fragment_container, new NewDreadlockFragment(), tag)
                    .addToBackStack(tag)
                    .commit();
        });

    }

    @Override
    int getLayout() {
        return R.layout.activity_main;
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
            onCameraPhotoResult(isFinal);

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
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, false);

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
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4,
                    bitmap.getHeight() / 4, true);

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
        GogoApi.getApi().upload(((GogoAndroid) getApplication()).getArtist(), "chushangfeng", GogoConst.watermarkDateFormat.format(new Date()), multipartBody).enqueue(new Callback<UploadResponse>() {
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

        NewWorkFragment fr = (NewWorkFragment) getSupportFragmentManager()
                .findFragmentByTag(((GogoAndroid) getApplication()).getArtist() + "/tattoo/new");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
        fr = (NewWorkFragment) getSupportFragmentManager()
                .findFragmentByTag(((GogoAndroid) getApplication()).getArtist() + "/design/new");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
        fr = (NewWorkFragment) getSupportFragmentManager()
                .findFragmentByTag(((GogoAndroid) getApplication()).getArtist() + "/henna/new");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
        fr = (NewWorkFragment) getSupportFragmentManager()
                .findFragmentByTag(((GogoAndroid) getApplication()).getArtist() + "/piercing/new");
        if (fr != null) {
            fr.addImage(hash, bitmap, isFinal);
        }
        fr = (NewWorkFragment) getSupportFragmentManager()
                .findFragmentByTag(((GogoAndroid) getApplication()).getArtist() + "/dreadlocks/new");
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

