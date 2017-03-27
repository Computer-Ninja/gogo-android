package tattoo.gogo.app.gogo_android.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by delirium on 2/24/17.
 */

public class Tattoo extends ArtWork implements Parcelable{

    protected Tattoo(Parcel in) {
        super(in);
    }

    public Tattoo() {
        type = "tattoo";
        link = "tattoo/";
    }

}
