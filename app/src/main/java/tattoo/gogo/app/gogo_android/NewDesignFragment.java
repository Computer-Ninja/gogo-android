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
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Design;

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
    }


}
