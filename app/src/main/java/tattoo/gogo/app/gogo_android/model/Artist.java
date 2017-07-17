package tattoo.gogo.app.gogo_android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by delirium on 2/26/17.
 */

public class Artist implements Parcelable {
    protected Artist(Parcel in) {
        link = in.readString();
        name = in.readString();
        services = in.createStringArrayList();
        joinDate = in.readString();
        birthDate = in.readString();
        experience = in.readString();
        about = in.readString();
        origin = in.readString();
        locationNow = in.readString();
        avatarIpfs = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeString(name);
        dest.writeStringList(services);
        dest.writeString(joinDate);
        dest.writeString(birthDate);
        dest.writeString(experience);
        dest.writeString(about);
        dest.writeString(origin);
        dest.writeString(locationNow);
        dest.writeString(avatarIpfs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getLocationNow() {
        return locationNow;
    }

    public void setLocationNow(String locationNow) {
        this.locationNow = locationNow;
    }

    public String getAvatarIpfs() {
        return avatarIpfs;
    }

    public void setAvatarIpfs(String avatarIpfs) {
        this.avatarIpfs = avatarIpfs;
    }

    String name;
    List<String> services;
    @SerializedName("join_date")
    String joinDate;
    @SerializedName("birth_date")
    String birthDate;
    String experience;
    String about;
    String origin;
    @SerializedName("location_now")
    String locationNow;
    @SerializedName("avatar_ipfs")
    String avatarIpfs;
    String link;

}
