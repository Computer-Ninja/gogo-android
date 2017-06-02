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
import tattoo.gogo.app.gogo_android.model.ArtWork;
import tattoo.gogo.app.gogo_android.model.Artist;
import tattoo.gogo.app.gogo_android.model.Design;
import tattoo.gogo.app.gogo_android.model.Dreadlocks;
import tattoo.gogo.app.gogo_android.model.Henna;
import tattoo.gogo.app.gogo_android.model.Piercing;
import tattoo.gogo.app.gogo_android.model.Tattoo;

public interface GogoService {

    @GET("{artist}/tattoo")
    Call<List<Tattoo>> tattoo(@Path("artist") String name);

    @GET("{artist}/tattoo")
    Call<List<Tattoo>> tattoo(@Path("artist") String name, @Query("status") String status);

    @POST("{artist}/tattoo/{work_name}")
    Call<Tattoo> tattoo(@Path("artist") String name,  @Path("work_name") String workName, @Body Tattoo tat);

    @DELETE("{artist}/tattoo/{work_name}")
    Call<List<ArtWork>> deleteTattoo(@Path("artist") String name, @Path("work_name") String workName);

    @GET("{artist}/henna")
    Call<List<Henna>> henna(@Path("artist") String name);

    @GET("{artist}/henna")
    Call<List<Henna>> henna(@Path("artist") String name, @Query("status") String status);

    @POST("{artist}/henna/{work_name}")
    Call<Henna> henna(@Path("artist") String name,  @Path("work_name") String workName, @Body Henna tat);

    @DELETE("{artist}/henna/{work_name}")
    Call<List<ArtWork>> deleteHenna(@Path("artist") String name, @Path("work_name") String workName);


    @GET("{artist}/design")
    Call<List<Design>> design(@Path("artist") String name);

    @GET("{artist}/design")
    Call<List<Design>> design(@Path("artist") String name, @Query("status") String status);

    @POST("{artist}/design/{work_name}")
    Call<Design> design(@Path("artist") String name, @Path("work_name") String workName, @Body Design tat);

    @DELETE("{artist}/design/{work_name}")
    Call<List<ArtWork>> deleteDesign(@Path("artist") String name, @Path("work_name") String workName);


    @GET("{artist}/piercing")
    Call<List<Piercing>> piercing(@Path("artist") String name);

    @GET("{artist}/piercing")
    Call<List<Piercing>> piercing(@Path("artist") String name, @Query("status") String status);

    @POST("{artist}/piercing/{work_name}")
    Call<Piercing> piercing(@Path("artist") String name, @Path("work_name") String workName, @Body Piercing tat);

    @DELETE("{artist}/piercing/{work_name}")
    Call<List<ArtWork>> deletePiercing(@Path("artist") String name, @Path("work_name") String workName);


    @GET("{artist}/dreadlocks")
    Call<List<Dreadlocks>> dreadlocks(@Path("artist") String name);

    @GET("{artist}/dreadlocks")
    Call<List<Dreadlocks>> dreadlocks(@Path("artist") String name, @Query("status") String status);

    @POST("{artist}/dreadlocks/{work_name}")
    Call<Dreadlocks> dreadlocks(@Path("artist") String name, @Path("work_name") String workName, @Body Dreadlocks tat);

    @DELETE("{artist}/dreadlocks/{work_name}")
    Call<List<ArtWork>> deleteDreadlocks(@Path("artist") String name, @Path("work_name") String workName);

    @GET("/artists")
    Call<List<Artist>> artists();

    @Multipart
    @POST("upload")
    Call<UploadResponse> upload(
            @Query("artist_name") String artistName,
            @Query("made_at") String madeAt,
            @Query("made_date") String madeDate,
            @Part MultipartBody.Part file
    );

}



