package tattoo.gogo.app.gogo_android;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Henna;

import static android.content.ContentValues.TAG;

/**
 * Created by delirium on 2/22/17.
 */
public class NewHennaFragment extends NewWorkFragment {

    private Henna mHenna;


    @Override
    protected ArtWork newArtWork() {
        mHenna = new Henna();
        return mHenna;
    }

    protected void sendToApi() {
        GogoApi.getApi().henna(ThreadLocalRandom.current().nextInt(0, 10000), mHenna)
                .enqueue(new Callback<Henna>() {
                    @Override
                    public void onResponse(Call<Henna> call, Response<Henna> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(etAuthor, R.string.submit_success, Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(etAuthor, R.string.submit_fail, Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Henna> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        Snackbar.make(etAuthor, "Failed: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    protected int getLayout() {
        return R.layout.fragment_new_henna;
    }
}
