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

    protected Bitmap makeQRcode(String link) throws OutOfMemoryError {
        return QRCode.from(link).withSize(1024, 1024).bitmap();
    }

    protected String getArtist() {
        return ((GogoAndroid) getActivity().getApplication()).getArtist();
    }

    protected void setArtist(String name) {
        ((GogoAndroid) getActivity().getApplication()).setArtist(name);
    }
}
