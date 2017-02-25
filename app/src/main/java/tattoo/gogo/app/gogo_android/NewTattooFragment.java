package tattoo.gogo.app.gogo_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moandjiezana.toml.TomlWriter;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.view.View.GONE;

/**
 * Created by delirium on 2/22/17.
 */
public class NewTattooFragment extends Fragment {

    private static final String MAIN_URL = "http://gogo.tattoo/";
    private static final String GITHUB_URL = "https://gogotattoo.github.io/";
    static final long ONE_MINUTE_IN_MILLIS = 60000; //millisecs

    @BindView(R.id.input_tattoo_title) EditText etTattooTitle;
    @BindView(R.id.input_tattoo_made_by) EditText etTattooAuthor;
    @BindView(R.id.input_tattoo_made_at) EditText etMadeAt;
    @BindView(R.id.input_tattoo_made_city) EditText etMadeCity;
    @BindView(R.id.input_tattoo_made_country) EditText etMadeCountry;
    @BindView(R.id.tv_future_link) TextView tvLink;
    @BindView(R.id.tv_future_link_availability) TextView tvTitleAvailability;
    @BindView(R.id.input_tattoo_made_date) EditText etMadeDate;
    @BindView(R.id.input_tattoo_time_elapsed) EditText etTimeDuration;
    @BindView(R.id.iv_qr_gogotattoo) ImageView ivQRgogo;
    @BindView(R.id.iv_qr_gogogithub) ImageView ivQRgithub;
    @BindView(R.id.tv_gogo_link) TextView tvGogoLink;
    @BindView(R.id.tv_github_link) TextView tvGithubLink;
    @BindView(R.id.tet_body_parts) ImprovedTagsEditText tetBodyParts;
    @BindView(R.id.tet_tags) ImprovedTagsEditText tetTags;
    @BindView(R.id.btn_female) Button btnFemale;
    @BindView(R.id.btn_male) Button btnMale;

    Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
    private Runnable workRunnable;
    private Tattoo mTattoo;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
    SimpleDateFormat watermarkDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private OkHttpClient client;
    private String mTattooArtist = "gogo";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_tattoo, container, false);

        ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTattoo = new Tattoo();
        populateWithDelay(etTattooAuthor, mTattooArtist, 600);
        populateWithDelay(etMadeAt, mTattoo.getMade_at_shop(), 1000);
        String dateToday = watermarkDateFormat.format(new Date());
        populateWithDelay(etMadeDate, dateToday, 1400);
        populateWithDelay(etTimeDuration, String.valueOf(mTattoo.getDuration_min()), 400);
        populateWithDelay(etMadeCity, String.valueOf(mTattoo.getLocation_city()), 200);
        populateWithDelay(etMadeCountry, String.valueOf(mTattoo.getLocation_country()), 700);

        tetTags.setTags(mTattoo.getTags());
        tetBodyParts.setTags(mTattoo.getBodypart());

        setListeners();

        btnFemale.performClick();
        etTattooTitle.requestFocus();

        client = new OkHttpClient();
    }

    private void setListeners() {

        btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFemale.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                btnMale.setTextColor(Color.GRAY);
                mTattoo.setGender("female");
            }
        });

        btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMale.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                btnFemale.setTextColor(Color.GRAY);
                mTattoo.setGender("male");
            }
        });

        etTattooAuthor.addTextChangedListener(new TextWatcher() {
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
        etTattooTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable tattooTitle) {
                mTattoo.setTitle(tattooTitle.toString().trim());
                updateLink();


                handler.removeCallbacks(workRunnable);
                workRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mTattoo.getTitle().length() < 4 || mTattooArtist.isEmpty()) {
                            ivQRgogo.setVisibility(GONE);
                            ivQRgithub.setVisibility(GONE);
                            tvGogoLink.setVisibility(GONE);
                            tvGithubLink.setVisibility(GONE);
                            return;
                        }
                        updateQRcodes();
                        testLink();
                    }
                };
                handler.postDelayed(workRunnable, 1500 /*delay*/);

            }
        });

        ((MainActivity) getActivity()).getFloatingActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTattoo.setTattoodate(sdf.format(new Date()));
                long t = Calendar.getInstance().getTimeInMillis();
                mTattoo.setDate(sdf.format(new Date(t + (mTattoo.getDuration_min() * ONE_MINUTE_IN_MILLIS))));
                mTattoo.setBodypart(tetBodyParts.getTags().toArray(new String[0]));
                mTattoo.setTags(tetTags.getTags().toArray(new String[0]));
                mTattoo.setLink(makeLink(MAIN_URL));
                sendForApprovalToPublish();
            }
        });
    }

    private void testLink() {
        Request request = new Request.Builder()
                .url(makeLink(MAIN_URL))
                .head()
                .build();

        tvTitleAvailability.setText(R.string.test_link_wait);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int strRes = R.string.test_link_available;
                        if (response.isSuccessful()) {
                            strRes = R.string.test_link_taken;
                            tvTitleAvailability.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                        } else {
                            tvTitleAvailability.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                        }
                        tvTitleAvailability.setText(strRes);
                    }
                });
            }
        });

    }

    private void sendForApprovalToPublish() {
        TomlWriter tomlWriter = new TomlWriter();
        String tomlString = tomlWriter.write(mTattoo);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mTattoo.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, tomlString);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_to)));

    }

    private void populateWithDelay(final EditText view, final String value, int delay) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setText(value);
            }
        }, delay);
    }

    private void updateQRcodes() {

        ivQRgogo.setVisibility(View.VISIBLE);
        ivQRgithub.setVisibility(View.VISIBLE);
        tvGogoLink.setVisibility(View.VISIBLE);
        tvGithubLink.setVisibility(View.VISIBLE);
        final String gogoTattooLink = makeLink(MAIN_URL);
        final String gogoGithubLink = makeLink(GITHUB_URL);
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

        ivQRgithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("images/png");

                Uri uri = FileProvider.getUriForFile(getContext(), "tattoo.gogo.app", makeQRcodeFile(gogoGithubLink));
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_to)));
            }
        });

    }

    private Bitmap makeQRcode(String link) {
        return QRCode.from(link).withSize(2056, 2056).bitmap();
    }

    private File makeQRcodeFile(String link) {
        return QRCode.from(link).to(ImageType.PNG).withSize(2056, 2056).file();
    }


    private String makeLink(String mainUrl) {
        String tattooTitleLinkified = mTattoo.getTitle().toLowerCase().replace(" ", "_");
        return mainUrl + mTattooArtist.toLowerCase() + "/tattoo/" + tattooTitleLinkified;
    }

    private void updateLink() {
        tvLink.setText(makeLink(MAIN_URL));


    }
}
