package tattoo.gogo.app.gogo_android.model;

import android.os.Parcel;

/**
 * Created by delirium on 2/24/17.
 */

public class Henna extends ArtWork {

    protected Henna(Parcel in) {
        super(in);
    }

    public Henna() {
        type = "henna";
        link = "henna/";
    }

}
