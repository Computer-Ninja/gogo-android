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

    @GET("henna/{id}")
    Call<Henna> henna(@Path("id") long id);

    @POST("henna/{id}")
    Call<Henna> henna(@Path("id") int id, @Body Henna hen);

    @GET("design/{id}")
    Call<Design> design(@Path("id") long id);

    @POST("design/{id}")
    Call<Design> design(@Path("id") int id, @Body Design des);

    @GET("piercing/{id}")
    Call<Piercing> piercing(@Path("id") long id);

    @POST("piercing/{id}")
    Call<Piercing> piercing(@Path("id") int id, @Body Piercing piercing);
}



