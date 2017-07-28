package tattoo.gogo.app.gogo_android.utils;

import android.content.Context;
import android.content.Intent;

import tattoo.gogo.app.gogo_android.ShareArtworkActivity;
import tattoo.gogo.app.gogo_android.ShareArtworkFragment;
import tattoo.gogo.app.gogo_android.model.ArtWork;

/**
 * Created by morphium on 17-7-28.
 */

public class NavUtil {


    public static void shareArtwork(Context context, String artistName, ArtWork artwork, String artworkType) {
        Intent intent = new Intent(context, ShareArtworkActivity.class);
        intent.putExtra(ShareArtworkFragment.ARG_ARTIST_NAME, artistName);
        intent.putExtra(ShareArtworkFragment.ARG_ARTWORK, artwork);
        intent.putExtra(ShareArtworkFragment.ARG_ARTWORK_TYPE, artworkType);
        context.startActivity(intent);
    }
}
