package tattoo.gogo.app.gogo_android.model;

import android.os.Parcel;

/**
 * Created by delirium on 2/24/17.
 */

public class Dreadlocks extends ArtWork {

    protected Dreadlocks(Parcel in) {
        super(in);
    }

    public Dreadlocks() {
        type = "dreadlocks";
        link = "dreadlocks/";
    }


}
