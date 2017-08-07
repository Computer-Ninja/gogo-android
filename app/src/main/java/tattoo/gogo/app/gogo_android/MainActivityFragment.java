package tattoo.gogo.app.gogo_android;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tattoo.gogo.app.gogo_android.api.GogoApi;
import tattoo.gogo.app.gogo_android.model.Artist;
import tattoo.gogo.app.gogo_android.utils.CircleTransform;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends ArtFragment {

    private int countFabTapped;
    @BindView(R.id.iv_doge) ImageView ivDoge;
    @BindView(R.id.tv_description) TextView tvDescription;
    @BindView(R.id.tv_version) TextView tvVersion;
    @BindView(R.id.tv_debug_info) TextView tvDebugInfo;
    @BindView(R.id.rv_artists) RecyclerView rvArtists;

    private Animation myFadeInAnimation;
    private Animation myFadeOutAnimation;
    private HorizontalPhotosAdapter horizontalAdapter;


    @Override
    protected int getLayout() {
        return R.layout.fragment_main;
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
        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            tvVersion.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!GogoApi.HOST_URL.contains("gogo.tattoo")) {
            tvDebugInfo.setText(GogoApi.HOST_URL);
        }



        GogoApi.getApi().artists().enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {

                horizontalAdapter = new HorizontalPhotosAdapter(response.body());
                LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false);
                rvArtists.setLayoutManager(horizontalLayoutManagaer);
                rvArtists.setAdapter(horizontalAdapter);
                horizontalAdapter.notifyDataSetChanged();
                rvArtists.postDelayed(() -> {
                    RecyclerView.ViewHolder view = rvArtists.findViewHolderForAdapterPosition(0);
                    if (view != null) {
                        view.itemView.performLongClick();
                    }
                }, 100);

            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {

            }
        });
    }

    public class HorizontalPhotosAdapter extends RecyclerView.Adapter<HorizontalPhotosAdapter.MyViewHolder> {

        private List<Artist> artists;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView riv;

            public MyViewHolder(View view) {
                super(view);

                riv = (ImageView) view;

            }
        }

        public HorizontalPhotosAdapter(List<Artist> artistList) {
            artists = artistList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_view, parent, false);

//
//            if (itemView.getLayoutParams ().width == RecyclerView.LayoutParams.MATCH_PARENT)
//                itemView.getLayoutParams ().width = RecyclerView.LayoutParams.WRAP_CONTENT;

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            Artist artist = artists.get(position);
            holder.riv.setImageResource(R.drawable.doge);

            loadArtist(holder.riv, GogoConst.IPFS_GATEWAY_URL + artist.getAvatarIpfs(), artist);

        }

        @Override
        public int getItemCount() {
            return artists.size();
        }
    }

    private void loadArtist(ImageView iv, String link, final Artist artist) {

        //hpArtists.addView(iv);

        SimpleTarget target = new SimpleTarget<GlideBitmapDrawable>() {
            @Override
            public void onResourceReady(GlideBitmapDrawable bitmap, GlideAnimation glideAnimation) {
                // do something with the bitmap
                // for demonstration purposes, let's just set it to an ImageView
                iv.setImageBitmap(bitmap.getBitmap());
                iv.setOnClickListener(v -> {
                    setArtistView(artist);
                    startActivity(new Intent(getContext(), ArtworkListActivity.class));
                });
                iv.setOnLongClickListener(view -> {
                    setArtistView(artist);
                    return true;
                });
            }
        };
        Glide.with(getContext())
                .load(link)
                .placeholder(R.drawable.doge)
                .bitmapTransform(new CircleTransform(getContext()))
                .into(target);
    }

    private void setArtistView(Artist artist) {
        ((MainActivity) getActivity()).setArtist(artist);
        ((GogoAndroid) getActivity().getApplication()).setArtist(artist);
        ((GogoActivity)getActivity()).setGogoTitle(artist.getLink());

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
        ivDoge.postDelayed(() -> ivDoge.startAnimation(myFadeOutAnimation), 5000);

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
                .setAction(action, v -> fabClicked(view)).show();
    }

    @Override
    public void handleShakeEvent(int count) {
        ivDoge.startAnimation(myFadeInAnimation);
        ivDoge.postDelayed(() -> {
            ivDoge.startAnimation(myFadeOutAnimation);
        }, 1000);
    }
}
