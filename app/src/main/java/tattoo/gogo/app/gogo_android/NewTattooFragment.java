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
import tattoo.gogo.app.gogo_android.model.Tattoo;

import static android.view.View.GONE;

/**
 * Created by delirium on 2/22/17.
 */
public class NewTattooFragment extends NewWorkFragment {

    private Tattoo mTattoo;

    @Override
    protected int getLayout() {
        return R.layout.fragment_new_tattoo;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTattoo = new Tattoo();
        populateWithDelay(etAuthor, mTattooArtist, 600);
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
        etTitle.requestFocus();

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

    private void sendForApprovalToPublish() {
        if (!isAdded()) {
            return;
        }
        TomlWriter tomlWriter = new TomlWriter();
        String tomlString = tomlWriter.write(mTattoo);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mTattoo.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, tomlString);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_to)));

    }

    protected String makeLink(String mainUrl) {
        String tattooTitleLinkified = mTattoo.getTitle().toLowerCase().replace(" ", "_");
        return mainUrl + mTattooArtist.toLowerCase() + "/tattoo/" + tattooTitleLinkified;
    }

}
