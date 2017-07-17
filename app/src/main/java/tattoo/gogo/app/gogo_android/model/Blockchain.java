package tattoo.gogo.app.gogo_android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Blockchain implements Parcelable {
        @SerializedName("steem")
        private String steem;
        @SerializedName("golos")
        private String golos;

        protected Blockchain(Parcel in) {
            steem = in.readString();
            golos = in.readString();
        }

        public static final Creator<Blockchain> CREATOR = new Creator<Blockchain>() {
            @Override
            public Blockchain createFromParcel(Parcel in) {
                return new Blockchain(in);
            }

            @Override
            public Blockchain[] newArray(int size) {
                return new Blockchain[size];
            }
        };

        public String getSteem() {
            return steem;
        }

        public void setSteem(String steem) {
            this.steem = steem;
        }

        public String getGolos() {
            return golos;
        }

        public void setGolos(String golos) {
            this.golos = golos;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(steem);
            dest.writeString(golos);
        }
    }