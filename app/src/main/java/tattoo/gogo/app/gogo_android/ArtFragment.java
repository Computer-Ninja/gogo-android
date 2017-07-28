package tattoo.gogo.app.gogo_android;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import net.glxn.qrgen.android.QRCode;

import butterknife.ButterKnife;

/**
 * Created by delirium on 17-3-9.
 */

abstract public class ArtFragment extends Fragment {

    public static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_ARTIST_NAME = "artist-name";
    public static final String ARG_ARTWORK_TYPE = "artwork-type";

    public static final String ARTWORK_TYPE_TATTOO = "tattoo";
    public static final String ARTWORK_TYPE_DESIGN = "design";
    public static final String ARTWORK_TYPE_HENNA = "henna";
    public static final String ARTWORK_TYPE_PIERCING = "piercing";
    public static final String ARTWORK_TYPE_DREADLOCKS= "dreadlocks";
    public static final String PARAM_WORKS = "works";
    public static final String PARAM_BUNDLE = "bundle";
    //    @BindView(R.id.appbar) AppBarLayout mAppbar;
    Toolbar mToolbar;
    FloatingActionButton mFabButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    protected abstract int getLayout();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        mFabButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
    }

    protected void hideViews() {
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        hideFab();
    }

    protected void hideFab() {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mFabButton.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        mFabButton.animate().translationY(mFabButton.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();

    }

    protected void showViews() {
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    protected String getArtist() {
        if (isAdded()) {
            return ((GogoAndroid) getActivity().getApplication()).getArtist();
        }
        return "";
    }

    protected void setArtist(String name) {
        if (isAdded()) {
            ((GogoAndroid) getActivity().getApplication()).setArtist(name);
        }
    }

    public void handleShakeEvent(int count) {

    }
}
