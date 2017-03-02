package tattoo.gogo.app.gogo_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.moandjiezana.toml.TomlWriter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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
                mTattoo.setDuration_min(Integer.valueOf(etTimeDuration.getText().toString()));
                mTattoo.setMade_at_shop(etMadeAt.getText().toString());
                //sendForApprovalToPublish();
                sendToApi();
            }
        });
    }
    public interface TattooService {
        //@GET("tattoo/{artist}")
        //Call<List<Tattoo>> tattoos(@Path("artist") String user);
        @POST("tattoo/{id}")
        Call<List<Tattoo>> tattoo(@Path("id") int id, @Body Tattoo tat);
    }
    private void sendToApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://tron.ink:12345/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TattooService ts = retrofit.create(TattooService.class);

        ts.tattoo(ThreadLocalRandom.current().nextInt(0, 10000), mTattoo).enqueue(new Callback<List<Tattoo>>() {
            @Override
            public void onResponse(Call<List<Tattoo>> call, Response<List<Tattoo>> response) {
                Snackbar.make(etAuthor, response.body().get(response.body().size()-1).getTitle(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<List<Tattoo>> call, Throwable t) {
                Snackbar.make(etAuthor, "Failed: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void sendForApprovalToPublish() {
        if (!isAdded()) {
            return;
        }
        String tomlString = new TomlWriter().write(mTattoo);

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
