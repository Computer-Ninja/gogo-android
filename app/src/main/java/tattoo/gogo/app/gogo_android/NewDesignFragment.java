package tattoo.gogo.app.gogo_android;

import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Design;
import tattoo.gogo.app.gogo_android.model.Tattoo;

import static android.content.ContentValues.TAG;

/**
 * Created by delirium on 2/22/17.
 */
public class NewDesignFragment extends NewWorkFragment {

    private Design mDesign;

    public static NewDesignFragment newInstance(ArtWork artWork) {
        NewDesignFragment fr = new NewDesignFragment();
        fr.mDesign = (Design) artWork;
        return fr;
    }
    @Override
    protected int getLayout() {
        return R.layout.fragment_new_design;
    }


    @Override
    protected ArtWork newArtWork() {
        if (mDesign == null) {
            mDesign = new Design();
        }
        return mDesign;
    }


    protected void sendToApi() {
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
