package tattoo.gogo.app.gogo_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
import tattoo.gogo.app.gogo_android.utils.CircleTransform;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends ArtFragment {

    private int countFabTapped;
    @BindView(R.id.iv_doge) ImageView ivDoge;
    @BindView(R.id.iv_artist_gogo) ImageView ivArtistGogo;
    @BindView(R.id.iv_artist_aid) ImageView ivArtistAid;
    @BindView(R.id.iv_artist_xizi) ImageView ivArtistXizi;
    @BindView(R.id.tv_description) TextView tvDescription;

    private View flNewTattoo;
    private FloatingActionButton fab;
    private Animation myFadeInAnimation;
    private Animation myFadeOutAnimation;


    public MainActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myFadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        myFadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivDoge.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        myFadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
        myFadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivDoge.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ivDoge.startAnimation(myFadeOutAnimation);

        loadArtist(ivArtistGogo, "http://gogo.tattoo/gogo/images/strawberry.jpg", "gogo");
        loadArtist(ivArtistAid, "http://gogo.tattoo/aid/images/aid.png", "aid");
        loadArtist(ivArtistXizi, "http://gogo.tattoo/xizi/images/xizi.jpg", "xizi");

        //tvDescription.setText(Hello.greetings("This is a greeting from golang, WOW!"));

    }

    private void loadArtist(final ImageView iv, String link, final String artistName) {

        SimpleTarget target = new SimpleTarget<GlideBitmapDrawable>() {
            @Override
            public void onResourceReady(GlideBitmapDrawable bitmap, GlideAnimation glideAnimation) {
                // do something with the bitmap
                // for demonstration purposes, let's just set it to an ImageView
                iv.setImageBitmap(bitmap.getBitmap());
                iv.setOnClickListener(v -> {
                    String tag = artistName+"/"+ArtistArtworkListFragment.ARTWORK_TYPE_TATTOO;
                    getFragmentManager().beginTransaction()
                            .hide(MainActivityFragment.this)
                            .add(R.id.fragment_container, ArtistArtworkListFragment.newInstance(1,
                                    artistName, ArtistArtworkListFragment.ARTWORK_TYPE_TATTOO), tag)
                            .addToBackStack(tag)
                            .commit();
                });
                iv.setOnLongClickListener(view -> {
                    String tag = artistName+"/"+ArtistArtworkListFragment.ARTWORK_TYPE_DESIGN;
                    getFragmentManager().beginTransaction()
                            .hide(MainActivityFragment.this)
                            .add(R.id.fragment_container, ArtistArtworkListFragment.newInstance(1,
                                    artistName, ArtistArtworkListFragment.ARTWORK_TYPE_DESIGN), tag)
                            .addToBackStack(tag)
                            .commit();
                    return true;
                });
            }
        };
        Glide.with(getContext())
                .load(link)
                .bitmapTransform(new CircleTransform(getContext()))
                .into(target);
    }

    @Override
    public void onResume() {
        super.onResume();
        countFabTapped = 0;

    }


    private void fabClicked(final View view) {

        if (countFabTapped == 1) {
            getFragmentManager().beginTransaction()
                    .hide(MainActivityFragment.this)
                    .add(R.id.fragment_container, new NewTattooFragment())
                    .addToBackStack("xyz")
                    .commit();
            countFabTapped = 0;
            return;

        }
        ivDoge.startAnimation(myFadeInAnimation);
        ivDoge.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivDoge.startAnimation(myFadeOutAnimation);
            }
        }, 5000);

        String msg = "i explain let me";
        String action = "no more";
        if (countFabTapped == 0) {
            msg = "Will neww tattooo so cool yeah";
            action = "Hide so shy";
        } else if (countFabTapped == 1) {
            msg = "So cool indeed";
            action = "Show mo,re ye";
        } else if (countFabTapped == 2) {
            msg = "want to save portifolio longe time";
            action = "i envy";
        }
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction(action,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fabClicked(view);
                            }
                        }).show();
    }
}
