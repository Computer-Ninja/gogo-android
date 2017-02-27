package tattoo.gogo.app.gogo_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.moandjiezana.toml.TomlWriter;

import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;
import tattoo.gogo.app.gogo_android.model.Henna;

import static android.view.View.GONE;

/**
 * Created by delirium on 2/22/17.
 */
public class NewHennaFragment extends NewWorkFragment {

    private Henna mHenna;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHenna = new Henna();
        populateWithDelay(etAuthor, mTattooArtist, 600);
        populateWithDelay(etMadeAt, mHenna.getMade_at_shop(), 1000);
        String dateToday = watermarkDateFormat.format(new Date());
        populateWithDelay(etMadeDate, dateToday, 1400);
        populateWithDelay(etTimeDuration, String.valueOf(mHenna.getDuration_min()), 400);
        populateWithDelay(etMadeCity, String.valueOf(mHenna.getLocation_city()), 200);
        populateWithDelay(etMadeCountry, String.valueOf(mHenna.getLocation_country()), 700);

        tetTags.setTags(mHenna.getTags());
        tetBodyParts.setTags(mHenna.getBodypart());

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
                mHenna.setGender("female");
            }
        });

        btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMale.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                btnFemale.setTextColor(Color.GRAY);
                mHenna.setGender("male");
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
                mHenna.setTitle(tattooTitle.toString().trim());
                updateLink();


                handler.removeCallbacks(workRunnable);
                workRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mHenna.getTitle().length() < 4 || mTattooArtist.isEmpty()) {
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
                mHenna.setTattoodate(sdf.format(new Date()));
                long t = Calendar.getInstance().getTimeInMillis();
                mHenna.setDate(sdf.format(new Date(t + (mHenna.getDuration_min() * ONE_MINUTE_IN_MILLIS))));
                mHenna.setBodypart(tetBodyParts.getTags().toArray(new String[0]));
                mHenna.setTags(tetTags.getTags().toArray(new String[0]));
                mHenna.setLink(makeLink(MAIN_URL));
                sendForApprovalToPublish();
            }
        });
    }


    private void sendForApprovalToPublish() {
        if (!isAdded()) {
            return;
        }
        TomlWriter tomlWriter = new TomlWriter();
        String tomlString = tomlWriter.write(mHenna);

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mHenna.getTitle());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, tomlString);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_to)));

    }



    protected String makeLink(String mainUrl) {
        String tattooTitleLinkified = mHenna.getTitle().toLowerCase().replace(" ", "_");
        return mainUrl + mTattooArtist.toLowerCase() + "/henna/" + tattooTitleLinkified;
    }
    @Override
    protected int getLayout() {
        return R.layout.fragment_new_henna;
    }
}
