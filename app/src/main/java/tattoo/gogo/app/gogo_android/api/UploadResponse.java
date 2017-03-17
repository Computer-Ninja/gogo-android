package tattoo.gogo.app.gogo_android.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by delirium on 3/17/17.
 */

public class UploadResponse {
    @SerializedName("URL")
    String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @SerializedName("Hash")
    String hash;
}
