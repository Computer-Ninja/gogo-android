package tattoo.gogo.app.gogo_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moandjiezana.toml.TomlWriter;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tattoo.gogo.app.gogo_android.model.ArtWork;

import static android.view.View.GONE;

/**
 * Created by delirium on 2/26/17.
 */
public abstract class NewWorkFragment extends ArtFragment {
    protected OkHttpClient client;
    protected String mTattooArtist = "gogo";


    @BindView(R.id.input_title) EditText etTitle;
    @BindView(R.id.input_made_by) EditText etAuthor;
    @BindView(R.id.input_made_at) EditText etMadeAt;
    @BindView(R.id.input_made_city) EditText etMadeCity;
    @BindView(R.id.input_made_country) EditText etMadeCountry;
    @BindView(R.id.tv_future_link) TextView tvLink;
    @BindView(R.id.tv_future_link_availability) TextView tvTitleAvailability;
    @BindView(R.id.input_made_date) EditText etMadeDate;
    @BindView(R.id.input_time_elapsed) EditText etTimeDuration;
    @BindView(R.id.iv_qr_gogotattoo) ImageView ivQRgogo;
    @BindView(R.id.iv_qr_gogogithub) ImageView ivQRgithub;
    @BindView(R.id.tv_gogo_link) TextView tvGogoLink;
    @BindView(R.id.tv_github_link) TextView tvGithubLink;
    @BindView(R.id.tet_body_parts) ImprovedTagsEditText tetBodyParts;
    @BindView(R.id.tet_tags) ImprovedTagsEditText tetTags;
    @BindView(R.id.btn_female) Button btnFemale;
    @BindView(R.id.btn_male) Button btnMale;
    @BindView(R.id.ll_gender_selection) LinearLayout llGenderSelection;


    Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
    protected Runnable workRunnable;
    private ArtWork mArtWork;
    protected FloatingActionButton fab;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        mArtWork = newArtWork();
        populateWithDelay(etAuthor, mTattooArtist, 600);
        populateWithDelay(etMadeAt, mArtWork.getMadeAtShop(), 1000);
        String dateToday = GogoConst.watermarkDateFormat.format(new Date());
        populateWithDelay(etMadeDate, dateToday, 1400);
        populateWithDelay(etTimeDuration, String.valueOf(mArtWork.getDurationMin()), 400);
        populateWithDelay(etMadeCity, String.valueOf(mArtWork.getMadeAtCity()), 200);
        populateWithDelay(etMadeCountry, String.valueOf(mArtWork.getMadeAtCountry()), 700);

        tetTags.setTags(mArtWork.getTags());
        tetBodyParts.setTags(mArtWork.getBodypart());

        fab = ((MainActivity) getActivity()).getFloatingActionButton();
        setListeners();

        btnFemale.performClick();
        etTitle.requestFocus();

        client = new OkHttpClient();
    }

    protected abstract ArtWork newArtWork();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    protected abstract int getLayout();


    protected void setListeners() {

        btnFemale.setOnClickListener(v -> {
            btnFemale.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            btnMale.setTextColor(Color.GRAY);
            mArtWork.setGender("female");
        });

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
                mTattooArtist = authorName.toString().trim();
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
                    if (mArtWork.getTitle().length() < 4 || mTattooArtist.isEmpty()) {
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
        fab.setOnLongClickListener(view -> {
            updateArtwork();
            sendForApprovalToPublish();
            return false;
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        menu.add(R.string.upload_photo).setOnMenuItemClickListener(item -> {
            startDialog();
            return true;
        });
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                getActivity());
        myAlertDialog.setTitle(R.string.upload_photo);

        myAlertDialog.setPositiveButton(R.string.from_gallery,
                (arg0, arg1) -> {
                    Intent pictureActionIntent = null;

                    pictureActionIntent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    getActivity().startActivityForResult(
                            pictureActionIntent,
                            MainActivity.GALLERY_PICTURE);

                });

        myAlertDialog.setNegativeButton(R.string.from_camera,
                (arg0, arg1) -> {

                    Intent intent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(f));

                    getActivity().startActivityForResult(intent,
                            MainActivity.CAMERA_REQUEST);

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
                        colorRes =  R.color.colorPrimaryDark;
                    } else {
                        }
                    tvTitleAvailability.setText(strRes);
                    tvTitleAvailability.setTextColor(ContextCompat.getColor(getContext(), colorRes));

                });
            }
        });

    }

    protected void updateArtwork() {
        mArtWork.setMadeDate(GogoConst.sdf.format(new Date()));
        long t = Calendar.getInstance().getTimeInMillis();
        mArtWork.setDate(GogoConst.sdf.format(new Date(t + (mArtWork.getDurationMin() * GogoConst.ONE_MINUTE_IN_MILLIS))));
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
            public Bitmap qrGithubBitmap;
            public Bitmap qrGogoBitmap;

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
            shareIntent.setType("images/png");

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
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setText(value);
            }
        }, delay);
    }


    protected String makeLink(String mainUrl) {
        String tattooTitleLinkified = mArtWork.getTitle().toLowerCase().replace(" ", "_");
        return mainUrl + mTattooArtist.toLowerCase() + "/" + mArtWork.getShortName().toLowerCase() +"/" + tattooTitleLinkified;
    }
}
