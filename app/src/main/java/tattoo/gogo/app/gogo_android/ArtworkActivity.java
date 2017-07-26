package tattoo.gogo.app.gogo_android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tattoo.gogo.app.gogo_android.model.ArtWork;

public class ArtworkActivity extends GogoActivity
        implements ArtistArtworkFragment.OnArtistArtworkFragmentInteractionListener,
        ViewPager.OnPageChangeListener {
    private static final String TAG = "ArtworkListActivity";
    public static final String ARG_ARTWORKS = "artworks";
    public static final String ARG_POSITION = "pos";
    public static final String ARG_ARTWORK_TYPE = "type";

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
    private ArrayList<ArtWork> mArtworks;
    private int mPosition;

    @Override
    int getLayout() {
        return R.layout.activity_artworks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mArtworks = getIntent().getParcelableArrayListExtra(ARG_ARTWORKS);
        mPosition = getIntent().getIntExtra(ARG_POSITION, 0);
        mArtworkType = getIntent().getStringExtra(ARG_ARTWORK_TYPE);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        mViewPager.setCurrentItem(mPosition);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.hide();
        //setGogoTitle(getArtist() + "/" + mArtworkType);

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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mArtworkType == null) {
            return;
        }
        String title = getArtist() + "/" + mArtworkType.toLowerCase() + "/" + getPageTitle(position);
        setGogoTitle(title);
    }

    private String getPageTitle(int position) {
        return  mArtworks.get(position).getLink();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void navigateTo(String artworkName) {
        mViewPager.setCurrentItem(findArtworkPositionByName(artworkName), true);
    }

    @Override
    public void loadThumbnail(WeakReference<Fragment> fr, ArtworkRecyclerViewAdapter.ViewHolder holder) {

    }

    @Override
    public void onListFragmentInteraction(WeakReference<Fragment> tWeakReference, String mArtistName, List<ArtWork> mValues, int position) {

    }

    private int findArtworkPositionByName(String artworkName) {
        int count = 0;
        for (ArtWork a : mArtworks) {
            if (a.getShortName().equals(artworkName)) {
                return count;
            }
            count++;
        }
        return 0;
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
            return ArtistArtworkFragment.newInstance(getArtist(), mArtworks.get(position), mArtworkType);
        }

        @Override
        public int getCount() {
            return mArtworks.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ArtworkActivity.this.getPageTitle(position);
        }
    }

    protected String getArtist() {
        return ((GogoAndroid) getApplication()).getArtist();
    }
}
