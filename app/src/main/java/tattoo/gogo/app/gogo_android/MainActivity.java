package tattoo.gogo.app.gogo_android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private boolean isFabOpen;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.fl_new_tattoo) View newTattoo;
    @BindView(R.id.fl_new_design) View newDesign;
    @BindView(R.id.fl_new_dreadlocks) View newDreadlocks;
    @BindView(R.id.fl_new_piercing) View newPiercing;
    @BindView(R.id.fl_new_henna) View newHenna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack("xyz")
                        .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                        .add(R.id.fragment_container, new NewTattooFragment(), "new tattoo")
                        .commit();
            }
        });

        newPiercing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack("xyz")
                        .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                        .add(R.id.fragment_container, new NewPiercingFragment(), "new piercing")
                        .commit();
            }
        });

        newDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack("xyz")
                        .hide(getSupportFragmentManager().findFragmentByTag("welcome"))
                        .add(R.id.fragment_container, new NewDesignFragment(), "new design")
                        .commit();
            }
        });

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
        }

        return super.onOptionsItemSelected(item);
    }
}
