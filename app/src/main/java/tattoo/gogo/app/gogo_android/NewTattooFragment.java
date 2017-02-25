package tattoo.gogo.app.gogo_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mabbas007.tagsedittext.TagsEditText;

import static android.view.View.GONE;

/**
 * Created by delirium on 2/22/17.
 */
public class NewTattooFragment extends Fragment {

    private static final String MAIN_URL = "http://gogo.tattoo/";
    private static final String GITHUB_URL = "https://gogotattoo.github.io/";

    @BindView(R.id.input_tattoo_title) EditText etTattooTitle;
    @BindView(R.id.input_tattoo_made_by) EditText etTattooAuthor;
    @BindView(R.id.input_tattoo_made_at) EditText etMadeAt;
    @BindView(R.id.input_tattoo_made_city) EditText etMadeCity;
    @BindView(R.id.input_tattoo_made_country) EditText etMadeCountry;
    @BindView(R.id.tv_future_link) TextView tvLink;
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
        populateWithDelay(etTattooAuthor, mTattoo.getArtist(), 600);
        populateWithDelay(etMadeAt, mTattoo.getMade_at_shop(), 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String dateToday = format.format(new Date());
        populateWithDelay(etMadeDate, dateToday, 1400);
        populateWithDelay(etTimeDuration, String.valueOf(mTattoo.getDuration_min()), 400);
        populateWithDelay(etMadeCity, String.valueOf(mTattoo.getLocation_city()), 200);
        populateWithDelay(etMadeCountry, String.valueOf(mTattoo.getLocation_country()), 700);

        tetTags.setTags(mTattoo.getTags());
        tetBodyParts.setTags(mTattoo.getBodypart());

        setListeners();

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
                mTattoo.setArtist(authorName.toString().trim());
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
                        updateQRcodes();
                    }
                };
                handler.postDelayed(workRunnable, 1500 /*delay*/);

            }
        });

        tetTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTattoo.setTags((String[]) tetTags.getTags().toArray());

            }
        });
        tetBodyParts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTattoo.setBodypart((String[]) tetBodyParts.getTags().toArray());

            }
        });
        ((MainActivity) getActivity()).getFloatingActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendForApprovalToPublish();
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
        if (mTattoo.getTitle().length() < 4 || mTattoo.getArtist().isEmpty()) {
            ivQRgogo.setVisibility(GONE);
            ivQRgithub.setVisibility(GONE);
            tvGogoLink.setVisibility(GONE);
            tvGithubLink.setVisibility(GONE);
            return;
        }
        ivQRgogo.setVisibility(View.VISIBLE);
        ivQRgithub.setVisibility(View.VISIBLE);
        tvGogoLink.setVisibility(View.VISIBLE);
        tvGithubLink.setVisibility(View.VISIBLE);
        String gogoTattooLink = makeLink(MAIN_URL);
        final String gogoGithubLink = makeLink(GITHUB_URL);
        tvGogoLink.setText(gogoTattooLink);
        tvGithubLink.setText(gogoGithubLink);
        ivQRgogo.setImageBitmap(makeQRcode(gogoTattooLink));
        ivQRgithub.setImageBitmap(makeQRcode(gogoGithubLink));

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
        return mainUrl + mTattoo.getArtist().toLowerCase() + "/tattoo/" + tattooTitleLinkified;
    }

    private void updateLink() {
        tvLink.setText(makeLink(MAIN_URL));
    }
}
