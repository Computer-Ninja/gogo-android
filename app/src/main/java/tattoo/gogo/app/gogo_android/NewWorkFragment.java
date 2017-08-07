package tattoo.gogo.app.gogo_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.moandjiezana.toml.TomlWriter;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Artist;
import tattoo.gogo.app.gogo_android.utils.IntentUtils;

import static android.os.Environment.getExternalStorageDirectory;
import static android.view.View.GONE;
import static tattoo.gogo.app.gogo_android.GogoActivity.GALLERY_VIDEO;
import static tattoo.gogo.app.gogo_android.GogoActivity.PERMISSION_REQUEST_CAMERA;
import static tattoo.gogo.app.gogo_android.GogoActivity.PERMISSION_REQUEST_STORAGE;
import static tattoo.gogo.app.gogo_android.MainActivity.GALLERY_PICTURE;

/**
 * Created by delirium on 2/26/17.
 */
public abstract class NewWorkFragment extends ArtFragment {
    private static final String TAG = "NewWorkFragment";
    protected static final String IS_FINAL = "is_final";
    protected OkHttpClient client;
    private boolean isFinalPhotoUloaded = false;
    protected Artist mArtist;

    @BindView(R.id.input_title)
    EditText etTitle;
    @BindView(R.id.input_made_by)
    EditText etAuthor;
    @BindView(R.id.input_made_at)
    EditText etMadeAt;
    @BindView(R.id.input_made_city)
    EditText etMadeCity;
    @BindView(R.id.input_made_country)
    EditText etMadeCountry;
    @BindView(R.id.tv_future_link)
    TextView tvLink;
    @BindView(R.id.tv_future_link_availability)
    TextView tvTitleAvailability;
    @BindView(R.id.input_made_date)
    EditText etMadeDate;
    @BindView(R.id.input_time_elapsed)
    EditText etTimeDuration;
    @BindView(R.id.iv_qr_gogotattoo)
    ImageView ivQRgogo;
    @BindView(R.id.iv_qr_gogogithub)
    ImageView ivQRgithub;
    @BindView(R.id.tv_gogo_link)
    TextView tvGogoLink;
    @BindView(R.id.tv_github_link)
    TextView tvGithubLink;
    @BindView(R.id.tet_body_parts)
    ImprovedTagsEditText tetBodyParts;
    @BindView(R.id.tet_tags)
    ImprovedTagsEditText tetTags;
    @Nullable
    @BindView(R.id.btn_female)
    Button btnFemale;
    @Nullable
    @BindView(R.id.btn_male)
    Button btnMale;
    @Nullable
    @BindView(R.id.ll_gender_selection)
    LinearLayout llGenderSelection;
    @BindView(R.id.ll_process_images)
    LinearLayout llProcessImages;
    @BindView(R.id.ll_videos)
    LinearLayout llVideos;
    @BindView(R.id.ll_final_image)
    LinearLayout llFinalImage;
    @BindView(R.id.btn_upload_process)
    Button btnUploadProcess;
    @BindView(R.id.btn_upload_final)
    Button btnUploadFinal;
    @BindView(R.id.btn_upload_video)
    Button btnUploadVideo;

    Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
    protected Runnable workRunnable;
    private ArtWork mArtWork;
    protected FloatingActionButton fab;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        mArtWork = newArtWork();
        mArtist = ((GogoAndroid) getActivity().getApplication()).getArtist();
        populateWithDelay(etAuthor, mArtist.getName(), 600);
        populateWithDelay(etTitle, mArtWork.getTitle(), 200);
        populateWithDelay(etMadeAt, mArtist.getCurrentStudio(), 1000);
        String date = null;
        try {
            date = GogoConst.watermarkDateFormat.format(GogoConst.sdf.parse(mArtWork.getMadeDate()));
        } catch (ParseException p) {
            date = GogoConst.watermarkDateFormat.format(new Date());
        }
        try {
            String locationCity = mArtist.getLocationNow().split(",")[0];
            String locationCountry = mArtist.getLocationNow().split(",")[1];

            populateWithDelay(etMadeCity, locationCity, 200);
            populateWithDelay(etMadeCountry, locationCountry, 700);
        }catch (Exception x) {
            x.printStackTrace();
            populateWithDelay(etMadeCity, mArtWork.getMadeAtCity(), 200);
            populateWithDelay(etMadeCountry, mArtWork.getMadeAtCountry(), 700);

        }
        populateWithDelay(etMadeDate, date, 1400);
        populateWithDelay(etTimeDuration, String.valueOf(mArtWork.getDurationMin()), 400);

        tetTags.setTags(mArtWork.getTags());
        tetBodyParts.setTags(mArtWork.getBodypart());

        tetTags.post(() -> {
            if (!isAdded()) {
                return;
            }
            for (String hash : mArtWork.getImagesIpfs()) {
                ImageView iv = createImageView(hash, false);
                addImageView(iv, false);
                Glide.with(getContext())
                        .load(GogoConst.IPFS_GATEWAY_URL + hash)
                        .placeholder(R.drawable.progress_animation)
                        .into(iv);
            }
            if (!mArtWork.getImageIpfs().isEmpty()) {
                ImageView iv = createImageView(mArtWork.getImageIpfs(), true);
                addImageView(iv, true);
                Glide.with(getContext())
                        .load(GogoConst.IPFS_GATEWAY_URL + mArtWork.getImageIpfs())
                        .placeholder(R.drawable.progress_animation)
                        .into(iv);
            }

            if (!mArtWork.getVideosIpfs().isEmpty()) {
                llVideos.setVisibility(View.VISIBLE);
            }
            for (String hash : mArtWork.getVideosIpfs()) {

                VideoView vv = createVideoView(hash);
                llVideos.addView(vv);
                vv.setVideoURI(Uri.parse(GogoConst.IPFS_GATEWAY_URL + hash));
                vv.start();
            }
        });

        fab = ((GogoActivity) getActivity()).getFloatingActionButton();
        setListeners();

        if (btnFemale != null) {

            if (mArtWork.getGender().equals("male")) {
                btnMale.performClick();
            } else {
                btnFemale.performClick();
            }
        }
        etTitle.requestFocus();

        client = new OkHttpClient();
        showViews();
    }

    protected abstract ArtWork newArtWork();


    protected void setListeners() {
        if (btnFemale != null)
            btnFemale.setOnClickListener(v -> {
                btnFemale.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                btnMale.setTextColor(Color.GRAY);
                mArtWork.setGender("female");
            });

        if (btnMale != null)
            btnMale.setOnClickListener(v -> {
                btnMale.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                btnFemale.setTextColor(Color.GRAY);
                mArtWork.setGender("male");
            });

        etAuthor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable authorName) {
                mArtist.setName(authorName.toString().trim());
                updateLink();
            }
        });
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable tattooTitle) {
                mArtWork.setTitle(tattooTitle.toString().trim());
                updateLink();

                handler.removeCallbacks(workRunnable);
                workRunnable = () -> {
                    if (!isAdded()) {
                        return;
                    }
                    if (mArtWork.getTitle().length() < 4) {
                        ivQRgogo.setVisibility(GONE);
                        ivQRgithub.setVisibility(GONE);
                        tvGogoLink.setVisibility(GONE);
                        tvGithubLink.setVisibility(GONE);
                        return;
                    }
                    updateQRcodes();
                    testLink();
                };
                handler.postDelayed(workRunnable, 1500 /*delay*/);

            }
        });
        fab.setOnClickListener(view -> {
            if (etTitle.getText().toString().trim().isEmpty()) {
                Snackbar.make(mToolbar, R.string.error_please_give_title, Snackbar.LENGTH_LONG).show();
                return;
            }
            updateArtwork();
            sendToApi();
        });

        fab.setOnLongClickListener(view -> {
            if (etTitle.getText().toString().trim().isEmpty()) {
                Snackbar.make(mToolbar, R.string.error_please_give_title, Snackbar.LENGTH_LONG).show();
                return true;
            }
            updateArtwork();
            sendForApprovalToPublish();
            return true;
        });

        btnUploadProcess.setOnClickListener(v -> startDialog(false));
        btnUploadFinal.setOnClickListener(v -> startDialog(true));
        btnUploadVideo.setOnClickListener(v -> {
            final GogoActivity ga = ((GogoActivity) getActivity());
            if (!ga.haveStoragePermission()) {
                ga.requestPermission(PERMISSION_REQUEST_STORAGE);
                return;
            }
            hideFab();
            updateArtwork();
            Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            ga.startActivityForResult(pictureActionIntent, GALLERY_VIDEO);
        });
    }

    protected abstract void sendToApi();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        menu.add(R.string.upload_process_photo).setOnMenuItemClickListener(item -> {
            startDialog(false);
            return true;
        });
        if (!isFinalPhotoUloaded) {
            menu.add(R.string.upload_final_photo).setOnMenuItemClickListener(item -> {
                startDialog(true);
                return true;
            });
        }
    }

    private void startDialog(boolean isFinal) {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                getActivity());
        myAlertDialog.setTitle(R.string.upload_photo);
        final GogoActivity ga = ((GogoActivity) getActivity());
        myAlertDialog.setPositiveButton(R.string.from_gallery,
                (arg0, arg1) -> {
                    if (!ga.haveStoragePermission()) {
                        ga.requestPermission(PERMISSION_REQUEST_STORAGE);
                        return;
                    }
                    hideFab();
                    updateArtwork();
                    Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    ga.startActivityForResult(pictureActionIntent, GALLERY_PICTURE + (isFinal ? 1000 : 0));

                });

        myAlertDialog.setNegativeButton(R.string.from_camera,
                (arg0, arg1) -> {
                    if (!ga.haveStoragePermission()) {
                        ga.requestPermission(PERMISSION_REQUEST_STORAGE);
                        return;
                    }
                    if (!ga.haveCameraPermission()) {
                        ga.requestPermission(PERMISSION_REQUEST_CAMERA);
                        return;
                    }
                    updateArtwork();
                    hideFab();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    ga.startActivityForResult(intent, MainActivity.CAMERA_REQUEST
                            + (isFinal ? 1000 : 0));

                });
        myAlertDialog.setOnCancelListener(dialog -> {
            showViews();
            ((GogoActivity) getActivity()).hideLoading();
        });
        myAlertDialog.show();
    }


    private void sendForApprovalToPublish() {
        if (!isAdded()) {
            return;
        }
        String tomlString = new TomlWriter().write(mArtWork);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mArtWork.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, tomlString);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_to)));

    }

    protected void testLink() {
        Request request = new Request.Builder()
                .url(makeLink(GogoConst.MAIN_URL))
                .head()
                .build();

        tvTitleAvailability.setText(R.string.test_link_wait);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                handler.post(() -> {
                    int strRes = R.string.test_link_available;
                    int colorRes = R.color.colorAccent;
                    if (response.isSuccessful()) {
                        strRes = R.string.test_link_taken;
                        colorRes = R.color.colorPrimaryDark;
                    } else {
                    }
                    if (isAdded()) {
                        tvTitleAvailability.setText(strRes);
                        tvTitleAvailability.setTextColor(ContextCompat.getColor(getContext(), colorRes));
                    }
                });
            }
        });

    }

    protected void updateArtwork() {
        mArtWork.setMadeDate(GogoConst.sdf.format(new Date()));
        mArtWork.setDate(GogoConst.sdf.format(new Date(new Date().getTime() + (mArtWork.getDurationMin() * GogoConst.ONE_MINUTE_IN_MILLIS))));
        mArtWork.setBodypart(tetBodyParts.getTags().toArray(new String[0]));
        mArtWork.setTags(tetTags.getTags().toArray(new String[0]));
        mArtWork.setLink(makeLink(GogoConst.MAIN_URL));
        mArtWork.setDurationMin(Integer.valueOf(etTimeDuration.getText().toString()));
        mArtWork.setMadeAtShop(etMadeAt.getText().toString());
        mArtWork.setMadeAtCity(etMadeCity.getText().toString());
        mArtWork.setMadeAtCountry(etMadeCountry.getText().toString());
        try {
            Date tattooDate = GogoConst.watermarkDateFormat.parse(etMadeDate.getText().toString());

            mArtWork.setMadeDate(GogoConst.sdf.format(tattooDate));

            mArtWork.setDate(GogoConst.sdf.format(new Date(tattooDate.getTime() +
                    (mArtWork.getDurationMin() * GogoConst.ONE_MINUTE_IN_MILLIS))));
        } catch (Exception x) {

        }
        Label label = new Label();
        label.setMadeAt(mArtWork.getMadeAtShop());
        label.setMadeDate(mArtWork.getMadeDate());
        ((NewArtworkActivity) getActivity()).setLatestLabel(label);
    }

    protected void updateQRcodes() {
        ivQRgogo.setVisibility(View.VISIBLE);
        ivQRgithub.setVisibility(View.VISIBLE);
        tvGogoLink.setVisibility(View.VISIBLE);
        tvGithubLink.setVisibility(View.VISIBLE);
        final String gogoTattooLink = makeLink(GogoConst.MAIN_URL);
        final String gogoGithubLink = makeLink(GogoConst.GITHUB_URL);
        tvGogoLink.setText(gogoTattooLink);
        tvGithubLink.setText(gogoGithubLink);

        new AsyncTask<Void, Void, Void>() {
            Bitmap qrGithubBitmap;
            Bitmap qrGogoBitmap;

            @Override
            protected Void doInBackground(Void... params) {
                qrGogoBitmap = makeQRcode(gogoTattooLink);
                qrGithubBitmap = makeQRcode(gogoGithubLink);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ivQRgogo.setImageBitmap(qrGogoBitmap);
                ivQRgithub.setImageBitmap(qrGithubBitmap);
            }
        }.execute();

        ivQRgithub.setOnClickListener(v -> {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("partial_images/png");

            Uri uri = FileProvider.getUriForFile(getContext(), "tattoo.gogo.app.gogo_android", makeQRcodeFile(gogoGithubLink));
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_to)));
        });

    }

    protected Bitmap makeQRcode(String link) {
        return QRCode.from(link).withSize(2056, 2056).bitmap();
    }

    protected File makeQRcodeFile(String link) {
        return QRCode.from(link).to(ImageType.PNG).withSize(2056, 2056).file();
    }


    protected void updateLink() {
        tvLink.setText(makeLink(GogoConst.MAIN_URL));

    }

    protected void populateWithDelay(final EditText view, final String value, int delay) {
        view.postDelayed(() -> view.setText(value), delay);
    }


    protected String makeLink(String mainUrl) {
        String tattooTitleLinkified = mArtWork.getTitle().toLowerCase()
                .replace(" ", "_")
                .replace("'", "");
        return mainUrl + mArtist.getLink() + "/" + mArtWork.getType() + "/" + tattooTitleLinkified;
    }


    public void addImage(String hash, Bitmap bitmap, boolean isFinal) {
        if (!isAdded()) {
            return;
        }
        showViews();
        final ImageView iv = createImageView(hash, isFinal);
        iv.setImageBitmap(bitmap);
        if (isFinal) {
            mArtWork.setImageIpfs(hash);
        } else {
            mArtWork.getImagesIpfs().add(hash);
        }
        addImageView(iv, isFinal);

        Glide.with(getContext())
                .load(GogoConst.IPFS_GATEWAY_URL + hash)
                .placeholder(new BitmapDrawable(getResources(), bitmap))
                .into(iv);
    }

    private void addImageView(ImageView iv, boolean isFinal) {
        if (isFinal) {
            llFinalImage.removeAllViews();
            llFinalImage.addView(iv);
            llFinalImage.setVisibility(View.VISIBLE);
        } else {
            llProcessImages.addView(iv);
            llProcessImages.setVisibility(View.VISIBLE);
        }
    }

    private ImageView createImageView(String hash, final boolean isFinal) {
        ImageView iv = new ImageView(getContext());
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setPadding(8, 8, 8, 8);
        iv.setOnClickListener(v -> IntentUtils.opentUrl(getContext(), GogoConst.IPFS_GATEWAY_URL + hash));
        iv.setOnLongClickListener(v -> {
            if (isFinal) {
                llFinalImage.removeAllViews();
                mArtWork.setImageIpfs(null);
            } else {
                llProcessImages.removeView(iv);
                mArtWork.getImagesIpfs().remove(hash);
            }
            return true;
        });

        return iv;
    }

    public void addVideo(String hash) {
        if (!isAdded()) {
            return;
        }
        showViews();
        mArtWork.getVideosIpfs().add(hash);
        VideoView vv = createVideoView(hash);
        llVideos.addView(vv);
        vv.setVideoURI(Uri.parse(GogoConst.IPFS_GATEWAY_URL + hash));
        vv.start();
        llVideos.setVisibility(View.VISIBLE);
    }

    private float mDownX;
    private float mDownY;
    private final float SCROLL_THRESHOLD = 10;
    private boolean isOnClick;

    protected VideoView createVideoView(String hash) {
        VideoView vv = new VideoView(getContext());
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(520, 880);
        ll.setMargins(8, 8, 8, 8);
        vv.setLayoutParams(ll);
        vv.setOnTouchListener((v, event) -> {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mDownY = event.getY();
                    isOnClick = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isOnClick) {
                        try {
                            llVideos.removeView(vv);
                            mArtWork.getVideosIpfs().remove(hash);
                        } catch (Exception x){

                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isOnClick && (Math.abs(mDownX - event.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - event.getY()) > SCROLL_THRESHOLD)) {
                        isOnClick = false;
                    }
                    break;
                default:
                    break;
            }
            return true;
        });
        vv.setOnPreparedListener(mp -> mp.setLooping(true));
        return vv;
    }

    public void setArtist(Artist artist) {
        this.mArtist = artist;
    }
}
