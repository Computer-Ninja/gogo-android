package tattoo.gogo.app.gogo_android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by delirium on 3/11/17.
 */

public class IntentUtils {
    public static void opentUrl(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
}
