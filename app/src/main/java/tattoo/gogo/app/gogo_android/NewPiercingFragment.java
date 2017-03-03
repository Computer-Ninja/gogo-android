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
import tattoo.gogo.app.gogo_android.model.Henna;
import tattoo.gogo.app.gogo_android.model.Piercing;

import static android.view.View.GONE;

/**
 * Created by delirium on 2/22/17.
 */
public class NewPiercingFragment extends NewWorkFragment {

    private Piercing mPiercing;

    @Override
    protected ArtWork newArtWork() {
        mPiercing = new Piercing();
        return mPiercing;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_new_piercing;
    }
}
