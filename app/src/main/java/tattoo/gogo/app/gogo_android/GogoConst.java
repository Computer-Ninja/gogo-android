package tattoo.gogo.app.gogo_android;

import java.text.SimpleDateFormat;

/**
 * Created by delirium on 17-3-7.
 */

class GogoConst {
    public static final String IPFS_GATEWAY_URL = "https://ipfs.io/ipfs/";

    protected static final String MAIN_URL = "http://gogo.tattoo/";
    protected static final String GITHUB_URL = "https://gogotattoo.github.io/";
    static final long ONE_MINUTE_IN_MILLIS = 60000; //millisecs
    public static final String GOGO_TATTOO = "gogo.tattoo";

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
    public static SimpleDateFormat watermarkDateFormat = new SimpleDateFormat("yyyy/MM/dd");
}
