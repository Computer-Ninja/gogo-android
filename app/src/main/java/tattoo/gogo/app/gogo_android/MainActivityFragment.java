package tattoo.gogo.app.gogo_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private int countFabTapped;
    @BindView(R.id.iv_doge) ImageView ivDoge;
    @BindView(R.id.iv_artist_gogo) ImageView ivArtistGogo;
    @BindView(R.id.iv_artist_aid) ImageView ivArtistAid;
    @BindView(R.id.iv_artist_xizi) ImageView ivArtistXizi;

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

        Picasso.with(getContext())
                .load("http://gogo.tattoo/gogo/images/strawberry.jpg")
                .transform(new CircleTransform())
                .into(ivArtistGogo, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivArtistGogo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getFragmentManager().beginTransaction()
                                        .addToBackStack("xyz")
                                        .hide(MainActivityFragment.this)
                                        .add(R.id.fragment_container, new ArtistTattooFragment())
                                        .commit();
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        countFabTapped = 0;
    }


    private void fabClicked(final View view) {

        if (countFabTapped == 1) {
            getFragmentManager().beginTransaction()
                    .addToBackStack("xyz")
                    .hide(MainActivityFragment.this)
                    .add(R.id.fragment_container, new NewTattooFragment())
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
