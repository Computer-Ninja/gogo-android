package tattoo.gogo.app.gogo_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.moandjiezana.toml.TomlWriter;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Design;
import tattoo.gogo.app.gogo_android.model.Henna;

import static android.content.ContentValues.TAG;
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
    protected ArtWork newArtWork() {
        mDesign = new Design();
        return mDesign;
    }

    protected void setListeners() {
        super.setListeners();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateArtwork();
                sendToApi();
            }
        });
    }

    private void sendToApi() {
        GogoApi.getApi().design(ThreadLocalRandom.current().nextInt(0, 10000), mDesign)
                .enqueue(new Callback<Design>() {
                    @Override
                    public void onResponse(Call<Design> call, Response<Design> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(etAuthor, R.string.submit_success, Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(etAuthor, R.string.submit_fail, Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Design> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        Snackbar.make(etAuthor, "Failed: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}
