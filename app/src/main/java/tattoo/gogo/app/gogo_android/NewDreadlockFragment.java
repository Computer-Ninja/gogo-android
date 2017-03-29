package tattoo.gogo.app.gogo_android;

import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Dreadlocks;
import tattoo.gogo.app.gogo_android.model.Piercing;

import static android.content.ContentValues.TAG;

/**
 * Created by delirium on 2/22/17.
 */
public class NewDreadlockFragment extends NewWorkFragment {

    private Dreadlocks mDreads;

    public static NewDreadlockFragment newInstance(ArtWork artWork) {
        NewDreadlockFragment fr = new NewDreadlockFragment();
        fr.mDreads = (Dreadlocks) artWork;
        return fr;
    }

    @Override
    protected ArtWork newArtWork() {
        if (mDreads == null) {
            mDreads = new Dreadlocks();
        }
        return mDreads;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_new_dreadlocks;
    }


    protected void setListeners() {
        super.setListeners();
        fab.setOnClickListener(view -> {
            updateArtwork();
            sendToApi();
        });
    }

    protected void sendToApi() {
        GogoApi.getApi().dreadlocks(ThreadLocalRandom.current().nextInt(0, 10000), mDreads)
                .enqueue(new Callback<Dreadlocks>() {
                    @Override
                    public void onResponse(Call<Dreadlocks> call, Response<Dreadlocks> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(etAuthor, R.string.submit_success, Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(etAuthor, R.string.submit_fail, Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Dreadlocks> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        Snackbar.make(etAuthor, "Failed: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}
