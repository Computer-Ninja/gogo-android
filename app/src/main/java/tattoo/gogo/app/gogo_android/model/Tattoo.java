package tattoo.gogo.app.gogo_android.model;

import android.os.Parcel;

/**
 * Created by delirium on 2/24/17.
 */

public class Tattoo extends ArtWork {

    protected Tattoo(Parcel in) {
        super(in);
    }

    public Tattoo() {
        link = "gogo/tattoo";
    }

}
