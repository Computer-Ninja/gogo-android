package tattoo.gogo.app.gogo_android;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.utils.GlideImageGetter;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements
        ArtistArtworkListFragment.OnArtistArtworkFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "MainActivity";
    private boolean isFabOpen;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fl_new_tattoo)
    View newTattoo;
    @BindView(R.id.fl_new_design)
    View newDesign;
    @BindView(R.id.fl_new_dreadlocks)
    View newDreadlocks;
    @BindView(R.id.fl_new_piercing)
    View newPiercing;
    @BindView(R.id.fl_new_henna)
    View newHenna;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.appbar)
    AppBarLayout mAppbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
                //fabClicked(view);
            }
        });
        newTattoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                String tag = "gogo/tattoo/new";
                getSupportFragmentManager().beginTransaction()
                        .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                        .add(R.id.fragment_container, new NewTattooFragment(), tag)
                        .addToBackStack(tag)
                        .commit();
            }
        });

        newPiercing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                String tag = "gogo/piercing/new";
                getSupportFragmentManager().beginTransaction()
                        .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                        .add(R.id.fragment_container, new NewPiercingFragment(), tag)
                        .addToBackStack(tag)
                        .commit();
            }
        });

        newDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                String tag = "gogo/design/new";
                getSupportFragmentManager().beginTransaction()
                        .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                        .add(R.id.fragment_container, new NewDesignFragment(), tag)
                        .addToBackStack(tag)
                        .commit();
            }
        });

        newHenna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                String tag = "gogo/henna/new";
                getSupportFragmentManager().beginTransaction()
                        .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                        .add(R.id.fragment_container, new NewHennaFragment(), tag)
                        .addToBackStack(tag)
                        .commit();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this);

    }

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            newTattoo.startAnimation(fab_close);
            newTattoo.setClickable(false);
            newHenna.startAnimation(fab_close);
            newHenna.setClickable(false);
            newDesign.startAnimation(fab_close);
            newDesign.setClickable(false);
            newPiercing.startAnimation(fab_close);
            newPiercing.setClickable(false);
            newDreadlocks.startAnimation(fab_close);
            newDreadlocks.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            newTattoo.startAnimation(fab_open);
            newTattoo.setClickable(true);
            newHenna.startAnimation(fab_open);
            newHenna.setClickable(true);
            newDesign.startAnimation(fab_open);
            newDesign.setClickable(true);
            newPiercing.startAnimation(fab_open);
            newPiercing.setClickable(true);
            newDreadlocks.startAnimation(fab_open);
            newDreadlocks.setClickable(true);
            isFabOpen = true;

        }
    }

    public FloatingActionButton getFloatingActionButton() {
        return (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isFabOpen) {
            animateFAB();
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateFAB();
                }
            });

            super.onBackPressed();
        }
    }

    @Override
    public void onListFragmentInteraction(Fragment fr, String artistName, ArtWork artWork) {
        String tag = artistName + "/" + artWork.getType() + "/"+ artWork.getLink();
        getSupportFragmentManager().beginTransaction()
                .hide(fr)
                .add(R.id.fragment_container,  ArtistArtworkFragment.newInstance(artistName, artWork), tag)
                .addToBackStack(tag)
                .commit();
    }

    public void onBackStackChanged() {
        FragmentManager manager = getSupportFragmentManager();
        //mAppbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        mAppbar.setExpanded(true, true);
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        if (manager != null) {
            if (manager.getBackStackEntryCount() == 0) {
                setActionBarTitle(getString(R.string.app_name_short));
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                FragmentManager.BackStackEntry bse =
                        manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1);
                String title = getString(R.string.app_name_short) + "/" + bse.getName();
                if (title.length() > 24) {
                    title = "/" + bse.getName();
                }

                setActionBarTitle(title);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }


    @Override
    public void loadThumbnail(final Fragment fr, ArtworkRecyclerViewAdapter.ViewHolder holder) {
        Log.d(TAG, "loadThumbnail: " + holder.mItem.getTitle());
        final String url = GogoConst.IPFS_GATEWAY_URL + holder.mItem.getImageIpfs();
        holder.ivThumbnail.setVisibility(View.VISIBLE);
        Display display = getWindowManager().getDefaultDisplay();
        final DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        Glide.with(getApplicationContext())
                .load(url)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.doge)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(outMetrics.widthPixels, outMetrics.heightPixels)
                .into(holder.ivThumbnail);
//
//        iv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//                Glide.with(fr)
//                        .load(url)
//                        .placeholder(R.drawable.progress_animation)
//                        .error(R.drawable.doge)
//                        .override(outMetrics.widthPixels, outMetrics.heightPixels)
//                        .into(iv);
//                return true;
//            }
//        });

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
