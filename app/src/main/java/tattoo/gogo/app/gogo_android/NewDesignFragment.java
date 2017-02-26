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

import static android.view.View.GONE;

/**
 * Created by delirium on 2/22/17.
 */
public class NewDesignFragment extends NewWorkFragment {

    private Design mDesign;

    @Override
    protected int getLayout() {
        return R.layout.fragment_new_tattoo;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDesign = new Design();
        populateWithDelay(etAuthor, mTattooArtist, 600);
        populateWithDelay(etMadeAt, mDesign.getMade_at_shop(), 1000);
        String dateToday = watermarkDateFormat.format(new Date());
        populateWithDelay(etMadeDate, dateToday, 1400);
        populateWithDelay(etTimeDuration, String.valueOf(mDesign.getDuration_min()), 400);
        populateWithDelay(etMadeCity, String.valueOf(mDesign.getLocation_city()), 200);
        populateWithDelay(etMadeCountry, String.valueOf(mDesign.getLocation_country()), 700);

        tetTags.setTags(mDesign.getTags());
        tetBodyParts.setTags(mDesign.getBodypart());

        llGenderSelection.setVisibility(GONE);
        setListeners();

        btnFemale.performClick();
        etTitle.requestFocus();

        client = new OkHttpClient();
    }

    private void setListeners() {

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
                mDesign.setTitle(tattooTitle.toString().trim());
                updateLink();


                handler.removeCallbacks(workRunnable);
                workRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mDesign.getTitle().length() < 4 || mTattooArtist.isEmpty()) {
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
                mDesign.setTattoodate(sdf.format(new Date()));
                long t = Calendar.getInstance().getTimeInMillis();
                mDesign.setDate(sdf.format(new Date(t + (mDesign.getDuration_min() * ONE_MINUTE_IN_MILLIS))));
                mDesign.setBodypart(tetBodyParts.getTags().toArray(new String[0]));
                mDesign.setTags(tetTags.getTags().toArray(new String[0]));
                mDesign.setLink(makeLink(MAIN_URL));
                sendForApprovalToPublish();
            }
        });
    }

    private void sendForApprovalToPublish() {
        if (!isAdded()) {
            return;
        }
        TomlWriter tomlWriter = new TomlWriter();
        String tomlString = tomlWriter.write(mDesign);

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mDesign.getTitle());
        sharingIntent.putExtra(Intent.EXTRA_TEXT, tomlString);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_to)));

    }

    protected String makeLink(String mainUrl) {
        String tattooTitleLinkified = mDesign.getTitle().toLowerCase().replace(" ", "_");
        return mainUrl + mTattooArtist.toLowerCase() + "/design/" + tattooTitleLinkified;
    }

}
