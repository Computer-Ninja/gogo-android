package tattoo.gogo.app.gogo_android.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by delirium on 2/24/17.
 */

public class Tattoo extends ArtWork {
    String tattoodate = "2017-02-25T20:01:06+03:00";

    public String getTattoodate() {
        return tattoodate;
    }

    public void setTattoodate(String tattoodate) {
        this.tattoodate = tattoodate;
    }

    public Tattoo() {
        tags = new String[]{"coloring", "watercolor", "cover", "scarcover", "color", "freehand", "black and gray", "bird", "animal", "dragon", "flower", "koi", "lotus", "stars", "butterfly", "skull", "cat",
                "colorful", "simplicity", "crazy", "couple"};
        bodypart = new String[]{"feet", "shoulder", "back", "hand", "arm", "chest", "leg"};
        link = "gogo/tattoo/";
    }


}
