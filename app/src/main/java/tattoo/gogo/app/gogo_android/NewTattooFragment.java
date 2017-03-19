package tattoo.gogo.app.gogo_android;

import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Tattoo;

import static android.content.ContentValues.TAG;
import static tattoo.gogo.app.gogo_android.GogoConst.ONE_MINUTE_IN_MILLIS;
import static tattoo.gogo.app.gogo_android.GogoConst.sdf;

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
    protected void setListeners() {
        super.setListeners();
        fab.setOnClickListener(view -> {
            updateArtwork();
            //sendForApprovalToPublish();
            sendToApi();
        });
    }

    private void sendToApi() {
        GogoApi.getApi().tattoo(ThreadLocalRandom.current().nextInt(0, 10000), mTattoo)
                .enqueue(new Callback<Tattoo>() {
                    @Override
                    public void onResponse(Call<Tattoo> call, Response<Tattoo> response) {
                        Tattoo tattoo = response.body();
                        if (response.isSuccessful()) {
                            Snackbar.make(etAuthor, R.string.submit_success, Snackbar.LENGTH_LONG)
                                    .setText(tattoo.getTitle())
                                    .show();
                        } else {
                            Snackbar.make(etAuthor, R.string.submit_fail, Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Tattoo> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        Snackbar.make(etAuthor, "Failed: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected ArtWork newArtWork() {
        mTattoo = new Tattoo();
        return mTattoo;
    }

    @Override
    protected void updateArtwork() {
        super.updateArtwork();
        try {
            Date tattooDate = GogoConst.watermarkDateFormat.parse(etMadeDate.getText().toString());

            mTattoo.setMadeDate(sdf.format(tattooDate));

            mTattoo.setDate(sdf.format(new Date(tattooDate.getTime() +
                    (mTattoo.getDurationMin() * ONE_MINUTE_IN_MILLIS))));
        } catch (Exception x) {
            long t = Calendar.getInstance().getTimeInMillis();
            mTattoo.setDate(sdf.format(new Date(t + (mTattoo.getDurationMin() * ONE_MINUTE_IN_MILLIS))));
        }
    }

}
