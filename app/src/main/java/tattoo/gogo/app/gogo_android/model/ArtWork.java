package tattoo.gogo.app.gogo_android.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by delirium on 2/26/17.
 */

public class ArtWork {

    String link = "gogo/tattoo/";
    String title = "";
    @SerializedName("made_date")
    String made_date = "2017-02-25T20:01:06+03:00";
    String date = "2017-02-25T20:01:06+03:00";
    String[] tags = {"coloring", "watercolor", "cover", "scarcover", "color", "freehand", "black and gray", "bird", "animal", "dragon", "flower", "koi", "lotus", "stars", "butterfly", "skull", "cat",
            "colorful", "simplicity", "crazy", "couple"};
    String[] bodypart = {"feet", "shoulder", "back", "hand", "arm", "chest", "leg"};
    @SerializedName("image_ipfs")
    String image_ipfs = "";
    @SerializedName("images_ipfs")
    ArrayList<String> images_ipfs = new ArrayList<>();
    @SerializedName("made_at_country")
    String made_at_country = "China";
    @SerializedName("made_at_city")
    String made_at_city = "Shanghai";
    @SerializedName("made_at_shop")
    String made_at_shop = "chushangfeng";
    @SerializedName("duration_min")
    int duration_min = 120;
    @SerializedName("gender")
    String gender = "female";
    String extra = "";


    public String getMadeAtCity() {
        return made_at_city;
    }

    public void setMadeAtCity(String made_at_city) {
        this.made_at_city = made_at_city;
    }

    public String getMadeAtShop() {
        return made_at_shop;
    }

    public void setMadeAtShop(String made_at_shop) {
        this.made_at_shop = made_at_shop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMadeDate() {
        return made_date;
    }

    public void setMadeDate(String tattoodate) {
        this.made_date = tattoodate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getBodypart() {
        return bodypart;
    }

    public void setBodypart(String[] bodypart) {
        this.bodypart = bodypart;
    }

    public String getImageIpfs() {
        return image_ipfs;
    }

    public void setImageIpfs(String image_ipfs) {
        this.image_ipfs = image_ipfs;
    }

    public ArrayList<String> getImagesIpfs() {
        return images_ipfs;
    }

    public void setImagesIpfs(ArrayList<String> images_ipfs) {
        this.images_ipfs = images_ipfs;
    }

    public String getMadeAtCountry() {
        return made_at_country;
    }

    public void setMadeAtCountry(String made_at_country) {
        this.made_at_country = made_at_country;
    }

    public int getDurationMin() {
        return duration_min;
    }

    public void setDurationMin(int durationMin) {
        this.duration_min = durationMin;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String clientGender) {
        this.gender = clientGender;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getShortName() {
        return getClass().getSimpleName();
    }
}
