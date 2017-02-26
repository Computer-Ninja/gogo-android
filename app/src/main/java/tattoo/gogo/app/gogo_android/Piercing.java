package tattoo.gogo.app.gogo_android;

import java.util.ArrayList;

/**
 * Created by delirium on 2/24/17.
 */

public class Piercing {

    String link = "gogo/piercing/";
    String title = "";
    String tattoodate = "2017-02-25T20:01:06+03:00";
    String date = "2017-02-25T20:01:06+03:00";
    String []tags = {"sharp"};
    String [] bodypart = {"nose", "lip", "ear"};
    String image_ipfs = "";
    ArrayList<String> images_ipfs = new ArrayList<>();
    String location_country = "China";
    String location_city = "Shanghai";
    String made_at_shop = "chushangfeng";
    int duration_min = 30;
    String gender = "female";
    String extra = "";

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

    public String getTattoodate() {
        return tattoodate;
    }

    public void setTattoodate(String tattoodate) {
        this.tattoodate = tattoodate;
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

    public String getImage_ipfs() {
        return image_ipfs;
    }

    public void setImage_ipfs(String image_ipfs) {
        this.image_ipfs = image_ipfs;
    }

    public ArrayList<String> getImages_ipfs() {
        return images_ipfs;
    }

    public void setImages_ipfs(ArrayList<String> images_ipfs) {
        this.images_ipfs = images_ipfs;
    }

    public String getLocation_country() {
        return location_country;
    }

    public void setLocation_country(String location_country) {
        this.location_country = location_country;
    }

    public String getLocation_city() {
        return location_city;
    }

    public void setLocation_city(String location_city) {
        this.location_city = location_city;
    }

    public String getMade_at_shop() {
        return made_at_shop;
    }

    public void setMade_at_shop(String made_at_shop) {
        this.made_at_shop = made_at_shop;
    }

    public int getDuration_min() {
        return duration_min;
    }

    public void setDuration_min(int duration_min) {
        this.duration_min = duration_min;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

}
