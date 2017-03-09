package tattoo.gogo.app.gogo_android;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import io.fabric.sdk.android.Fabric;
public class GogoAndroid extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());

//        Picasso.Builder builder = new Picasso.Builder(this);
//        //builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
//        Picasso built = builder.build();
//        built.setIndicatorsEnabled(true);
//        built.setLoggingEnabled(true);
//        Picasso.setSingletonInstance(built);

    }
}