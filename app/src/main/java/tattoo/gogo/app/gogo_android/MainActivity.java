package tattoo.gogo.app.gogo_android;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import tattoo.gogo.app.gogo_android.model.Artist;
import tattoo.gogo.app.gogo_android.utils.ShakeDetector;

public class MainActivity extends GogoActivity {
    private static final String TAG = "MainActivity";
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

    private Artist mArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab.setOnClickListener(view -> {
            animateFAB();
            //fabClicked(view);
        });
        setNewWorkFAB(newTattoo, ArtFragment.ARTWORK_TYPE_TATTOO);
        setNewWorkFAB(newPiercing, ArtFragment.ARTWORK_TYPE_PIERCING);
        setNewWorkFAB(newHenna, ArtFragment.ARTWORK_TYPE_HENNA);
        setNewWorkFAB(newDesign, ArtFragment.ARTWORK_TYPE_DESIGN);
        setNewWorkFAB(newDreadlocks, ArtFragment.ARTWORK_TYPE_DREADLOCKS);

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        /*
         * The following method, "handleShakeEvent(count):" is a stub //
         * method you would use to setup whatever you want done once the
         * device has been shook.
         */
        mShakeDetector.setOnShakeListener(this::handleShakeEvent);
    }

    private void setNewWorkFAB(View view, String type) {
        view.setOnClickListener(v -> {
            if (mArtist == null) {

                Snackbar.make(fab, R.string.error_choose_artist_first, Snackbar.LENGTH_SHORT).show();
                return;
            }

            animateFAB();
            Intent i = new Intent(MainActivity.this, NewArtworkActivity.class);
            i.putExtra(NewArtworkActivity.ARG_ARTIST, mArtist);
            i.putExtra(NewArtworkActivity.ARG_ARTWORK_TYPE, type);
            startActivity(i);
        });
    }

    private void handleShakeEvent(int count) {
        ((ArtFragment) getSupportFragmentManager().findFragmentByTag("welcome")).handleShakeEvent(count);
    }

    @Override
    int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (isFabOpen) {
            animateFAB();
        } else {
            fab.setOnClickListener(v -> animateFAB());

            super.onBackPressed();
        }
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
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                startActivity(new Intent(this, TattooQrScannerActivity.class));
            } else {
                onBackPressed();
            }
        }

        return super.onOptionsItemSelected(item);
    }



    /*
	 * The gForce that is necessary to register as shake.
	 * Must be greater than 1G (one earth gravity unit).
	 * You can install "G-Force", by Blake La Pierre
	 * from the Google Play Store and run it to see how
	 *  many G's it takes to register a shake
	 */
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public void setArtist(Artist mArtist) {
        this.mArtist = mArtist;
    }

    public interface OnShakeListener {
        public void onShake(int count);
    }

}

