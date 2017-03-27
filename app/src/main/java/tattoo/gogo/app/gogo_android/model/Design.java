package tattoo.gogo.app.gogo_android.model;

import android.os.Parcel;

/**
 * Created by delirium on 2/24/17.
 */

public class Design extends ArtWork {


    protected Design(Parcel in) {
        super(in);
    }

    public Design() {
        type = "design";
        link = "design/";
    }
}
