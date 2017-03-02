package tattoo.gogo.app.gogo_android.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import tattoo.gogo.app.gogo_android.model.Tattoo;

public interface TattooService {
        @GET("tattoo/{id}")
        Call<List<Tattoo>> tattoos(@Path("id") long id);
        @POST("tattoo/{id}")
        Call<List<Tattoo>> tattoo(@Path("id") int id, @Body Tattoo tat);
    }



