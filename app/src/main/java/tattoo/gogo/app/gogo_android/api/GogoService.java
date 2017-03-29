package tattoo.gogo.app.gogo_android.api;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tattoo.gogo.app.gogo_android.model.Design;
import tattoo.gogo.app.gogo_android.model.Dreadlocks;
import tattoo.gogo.app.gogo_android.model.Henna;
import tattoo.gogo.app.gogo_android.model.Piercing;
import tattoo.gogo.app.gogo_android.model.Tattoo;

public interface GogoService {
    @GET("tattoo/{id}")
    Call<Tattoo> tattoo(@Path("id") long id);

    @GET("tattoo/{name}")
    Call<List<Tattoo>> tattoo(@Path("name") String name);

    @GET("tattoo")
    Call<List<Tattoo>> newTattoo();

    @POST("tattoo/{id}")
    Call<Tattoo> tattoo(@Path("id") int id, @Body Tattoo tat);

    @DELETE("tattoo/{id}")
    Call<List<Tattoo>> deleteTattoo(@Path("id") int id);

    @GET("henna/{name}")
    Call<List<Henna>> henna(@Path("name") String name);

    @GET("henna")
    Call<List<Henna>> newHenna();

    @POST("henna/{id}")
    Call<Henna> henna(@Path("id") int id, @Body Henna hen);

    @GET("design/{name}")
    Call<List<Design>> design(@Path("name") String name);

    @POST("design/{id}")
    Call<Design> design(@Path("id") int id, @Body Design des);

    @GET("design")
    Call<List<Design>> newDesign();

    @GET("piercing/{name}")
    Call<List<Piercing>> piercing(@Path("name") String name);

    @GET("piercing")
    Call<List<Piercing>> newPiercing();

    @POST("piercing/{id}")
    Call<Piercing> piercing(@Path("id") int id, @Body Piercing piercing);

    @GET("dreadlocks/{id}")
    Call<Dreadlocks> dreadlocks(@Path("id") long id);

    @GET("dreadlocks")
    Call<List<Dreadlocks>> newDreadlocks();

    @GET("dreadlocks/{name}")
    Call<List<Dreadlocks>> dreadlocks(@Path("name") String name);

    @POST("dreadlocks/{id}")
    Call<Dreadlocks> dreadlocks(@Path("id") int id, @Body Dreadlocks tat);

    @Multipart
    @POST("upload")
    Call<UploadResponse> upload(
            @Query("artist_name") String artistName,
            @Query("made_at") String madeAt,
            @Query("made_date") String madeDate,
            @Part MultipartBody.Part file
    );

}



