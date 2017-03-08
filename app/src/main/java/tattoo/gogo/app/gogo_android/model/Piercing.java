package tattoo.gogo.app.gogo_android.model;

import android.os.Parcel;

/**
 * Created by delirium on 2/24/17.
 */

public class Piercing extends ArtWork {

    protected Piercing(Parcel in) {
        super(in);
    }

    public  Piercing() {
        link = "gogo/piercing/";
    }

    public String getType() { return "piercing"; }

}
