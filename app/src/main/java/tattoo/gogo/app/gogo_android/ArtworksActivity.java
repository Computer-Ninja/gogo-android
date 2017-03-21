package tattoo.gogo.app.gogo_android;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.Locale;

import tattoo.gogo.app.gogo_android.model.ArtWork;

public class ArtworksActivity extends GogoActivity
        implements ArtistArtworkFragment.OnArtistArtworkFragmentInteractionListener,
        ArtistArtworkListFragment.OnArtistArtworkFragmentInteractionListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "ArtworksActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    int getLayout() {
        return R.layout.activity_artworks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        setGogoTitle(getArtist() + "/" + getCurrentArtType(0).toString().toLowerCase());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artworks, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(WeakReference<Fragment> fr, String artistName, ArtWork artWork) {
        if (fr.get() == null) {
            Log.d(TAG, "loadThumbnail: Fragment is null");
            return;
        }
        String tag = artistName + "/" + artWork.getType() + "/" + artWork.getLink();
        getSupportFragmentManager().beginTransaction()
                .hide(fr.get())
                .add(R.id.fragment_container, ArtistArtworkFragment.newInstance(artistName, artWork), tag)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void loadThumbnail(WeakReference<Fragment> fr, ArtworkRecyclerViewAdapter.ViewHolder holder) {
        Log.d(TAG, "loadThumbnail: " + holder.mItem.getTitle());
        if (fr.get() == null) {
            Log.d(TAG, "loadThumbnail: Fragment is null");
            return;
        }
        final String url = GogoConst.IPFS_GATEWAY_URL + holder.mItem.getImageIpfs();
        holder.ivThumbnail.setVisibility(View.VISIBLE);
        Display display = getWindowManager().getDefaultDisplay();
        final DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        Glide.with(fr.get())
                .load(url)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.doge)
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                //.override(outMetrics.widthPixels, outMetrics.heightPixels)
                .into(holder.ivThumbnail);

        holder.mView.setOnLongClickListener(view -> {
            showContextMenu(holder.ivThumbnail, holder.mItem.getImageIpfs(),
                    (hash, iv) -> loadThumbnail(fr, holder));
            return true;
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setGogoTitle(getArtist() + "/" + getCurrentArtType(position).toString().toLowerCase());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            String type = ArtistArtworkListFragment.ARTWORK_TYPE_TATTOO;
            switch (position) {
                case 1:
                    type = ArtistArtworkListFragment.ARTWORK_TYPE_DESIGN;
                    break;
                case 2:
                    type = ArtistArtworkListFragment.ARTWORK_TYPE_HENNA;
                    break;
                case 3:
                    type = ArtistArtworkListFragment.ARTWORK_TYPE_PIERCING;
                    break;

            }
            return ArtistArtworkListFragment.newInstance(1, getArtist(), type);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getCurrentArtType(position);
        }
    }

    private CharSequence getCurrentArtType(int position) {
        Resources res = getLocalizedResources(this, Locale.US);
        switch (position) {
            case 0:
                return res.getString(R.string.tattoo);
            case 1:
                return res.getString(R.string.design);
            case 2:
                return res.getString(R.string.henna);
            case 3:
                return res.getString(R.string.piercing);
        }
        return "";
    }


    protected String getArtist() {
        return ((GogoAndroid) getApplication()).getArtist();
    }
}
