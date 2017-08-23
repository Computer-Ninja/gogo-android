package tattoo.gogo.app.gogo_android;

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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tattoo.gogo.app.gogo_android.model.ArtWork;

public class ShareArtworkActivity extends GogoActivity
        implements ShareArtworkFragment.OnArtistArtworkFragmentInteractionListener {
    private static final String TAG = "ArtworkListActivity";
    private String mArtistName;
    private ArtWork mArtwork;

    @Override
    int getLayout() {
        return R.layout.activity_share_artwork;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mArtwork = getIntent().getParcelableExtra(ShareArtworkFragment.ARG_ARTWORK);
        mArtworkType = getIntent().getStringExtra(ShareArtworkFragment.ARG_ARTWORK_TYPE);
        mArtistName = getIntent().getStringExtra(ShareArtworkFragment.ARG_ARTIST_NAME);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.hide();
        ShareArtworkFragment shareArtworkFragment = ShareArtworkFragment.newInstance(mArtistName,
                mArtwork,
                mArtworkType);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, shareArtworkFragment, null)
                .commit();
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
    public void navigateTo(String artworkName) {
    }

    @Override
    public void showContextMenu(ImageView iv, String hash, ShareArtworkFragment.OnImageRefreshListener l) {

    }

}
