package tattoo.gogo.app.gogo_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by delirium on 2/26/17.
 */
public abstract class NewWorkFragment extends Fragment {
    protected static final String MAIN_URL = "http://gogo.tattoo/";
    protected static final String GITHUB_URL = "https://gogotattoo.github.io/";
    static final long ONE_MINUTE_IN_MILLIS = 60000; //millisecs

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
    SimpleDateFormat watermarkDateFormat = new SimpleDateFormat("yyyy/MM/dd");
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



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    protected abstract int getLayout();


    protected void testLink() {
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

    protected abstract String makeLink(String mainUrl);

    protected void updateQRcodes() {

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

                Uri uri = FileProvider.getUriForFile(getContext(), "tattoo.gogo.app.gogo_android", makeQRcodeFile(gogoGithubLink));
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_to)));
            }
        });

    }

    protected Bitmap makeQRcode(String link) {
        return QRCode.from(link).withSize(2056, 2056).bitmap();
    }

    protected File makeQRcodeFile(String link) {
        return QRCode.from(link).to(ImageType.PNG).withSize(2056, 2056).file();
    }


    protected void updateLink() {
        tvLink.setText(makeLink(MAIN_URL));

    }

    protected void populateWithDelay(final EditText view, final String value, int delay) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setText(value);
            }
        }, delay);
    }

}
