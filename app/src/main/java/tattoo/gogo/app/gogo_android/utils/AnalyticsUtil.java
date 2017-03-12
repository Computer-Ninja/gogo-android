package tattoo.gogo.app.gogo_android.utils;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by delirium on 3/11/17.
 */

public class AnalyticsUtil {

    public static void sendScreenName(Tracker tracker, String title) {
        tracker.setScreenName(title);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void sendEvent(Tracker tracker, String category, String action, String label) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }
}
