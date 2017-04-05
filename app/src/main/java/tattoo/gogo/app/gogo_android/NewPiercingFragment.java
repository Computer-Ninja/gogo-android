package tattoo.gogo.app.gogo_android;

import android.support.design.widget.Snackbar;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Piercing;

import static android.content.ContentValues.TAG;

/**
 * Created by delirium on 2/22/17.
 */
public class NewPiercingFragment extends NewWorkFragment {

    private Piercing mPiercing;

    public static NewPiercingFragment newInstance(ArtWork artWork) {
        NewPiercingFragment fr = new NewPiercingFragment();
        fr.mPiercing = (Piercing) artWork;
        return fr;
    }

    @Override
    protected ArtWork newArtWork() {
        if (mPiercing == null) {
            mPiercing = new Piercing();
        }
        return mPiercing;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_new_piercing;
    }

    protected void sendToApi() {
        GogoApi.getApi().piercing(getArtist(), mPiercing.getShortName(), mPiercing)
                .enqueue(new Callback<Piercing>() {
                    @Override
                    public void onResponse(Call<Piercing> call, Response<Piercing> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(etAuthor, R.string.submit_success, Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(etAuthor, R.string.submit_fail, Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Piercing> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t);
                        Snackbar.make(etAuthor, "Failed: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}
