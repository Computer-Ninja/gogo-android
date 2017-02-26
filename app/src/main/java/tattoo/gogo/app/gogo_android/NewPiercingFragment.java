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
public class NewPiercingFragment extends NewWorkFragment {

    private Piercing mPiercing;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPiercing = new Piercing();
        populateWithDelay(etAuthor, mTattooArtist, 600);
        populateWithDelay(etMadeAt, mPiercing.getMade_at_shop(), 1000);
        String dateToday = watermarkDateFormat.format(new Date());
        populateWithDelay(etMadeDate, dateToday, 1400);
        populateWithDelay(etTimeDuration, String.valueOf(mPiercing.getDuration_min()), 400);
        populateWithDelay(etMadeCity, String.valueOf(mPiercing.getLocation_city()), 200);
        populateWithDelay(etMadeCountry, String.valueOf(mPiercing.getLocation_country()), 700);

        tetTags.setTags(mPiercing.getTags());
        tetBodyParts.setTags(mPiercing.getBodypart());

        setListeners();

        btnFemale.performClick();
        etTitle.requestFocus();

        client = new OkHttpClient();
    }

    private void setListeners() {

        btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFemale.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                btnMale.setTextColor(Color.GRAY);
                mPiercing.setGender("female");
            }
        });

        btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMale.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                btnFemale.setTextColor(Color.GRAY);
                mPiercing.setGender("male");
            }
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
                mPiercing.setTitle(tattooTitle.toString().trim());
                updateLink();


                handler.removeCallbacks(workRunnable);
                workRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mPiercing.getTitle().length() < 4 || mTattooArtist.isEmpty()) {
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
                mPiercing.setTattoodate(sdf.format(new Date()));
                long t = Calendar.getInstance().getTimeInMillis();
                mPiercing.setDate(sdf.format(new Date(t + (mPiercing.getDuration_min() * ONE_MINUTE_IN_MILLIS))));
                mPiercing.setBodypart(tetBodyParts.getTags().toArray(new String[0]));
                mPiercing.setTags(tetTags.getTags().toArray(new String[0]));
                mPiercing.setLink(makeLink(MAIN_URL));
                sendForApprovalToPublish();
            }
        });
    }


    private void sendForApprovalToPublish() {
        if (!isAdded()) {
            return;
        }
        TomlWriter tomlWriter = new TomlWriter();
        String tomlString = tomlWriter.write(mPiercing);

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mPiercing.getTitle());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, tomlString);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_to)));

    }



    protected String makeLink(String mainUrl) {
        String tattooTitleLinkified = mPiercing.getTitle().toLowerCase().replace(" ", "_");
        return mainUrl + mTattooArtist.toLowerCase() + "/piercing/" + tattooTitleLinkified;
    }
    @Override
    protected int getLayout() {
        return R.layout.fragment_new_piercing;
    }
}
