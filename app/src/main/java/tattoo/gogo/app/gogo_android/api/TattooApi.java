package tattoo.gogo.app.gogo_android.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by delirium on 17-3-2.
 */

public class TattooApi {

    public static TattooService getApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://tron.ink:12345/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(TattooService.class);
    }
}
