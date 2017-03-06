package tattoo.gogo.app.gogo_android.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import tattoo.gogo.app.gogo_android.model.Design;
import tattoo.gogo.app.gogo_android.model.Henna;
import tattoo.gogo.app.gogo_android.model.Piercing;
import tattoo.gogo.app.gogo_android.model.Tattoo;

public interface GogoService {
    @GET("tattoo/{id}")
    Call<Tattoo> tattoo(@Path("id") long id);

    @GET("tattoo/{name}")
    Call<List<Tattoo>> tattoo(@Path("name") String name);

    @POST("tattoo/{id}")
    Call<Tattoo> tattoo(@Path("id") int id, @Body Tattoo tat);

    @GET("henna/{name}")
    Call<List<Henna>> henna(@Path("name") String name);

    @POST("henna/{id}")
    Call<Henna> henna(@Path("id") int id, @Body Henna hen);

    @GET("design/{name}")
    Call<List<Design>> design(@Path("name") String name);

    @POST("design/{id}")
    Call<Design> design(@Path("id") int id, @Body Design des);

    @GET("piercing/{name}")
    Call<List<Piercing>> piercing(@Path("name") String name);

    @POST("piercing/{id}")
    Call<Piercing> piercing(@Path("id") int id, @Body Piercing piercing);
}



