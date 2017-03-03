package tattoo.gogo.app.gogo_android.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tattoo.gogo.app.gogo_android.BuildConfig;

/**
 * Created by delirium on 17-3-2.
 */

public class GogoApi {

    private static final String HOST_URL = "http://tron.ink:12345/";
    private static final String HOST_URL_DEBUG = "http://192.168.1.145:12345/";
    private static GogoService mApi;

    public static GogoService getApi() {
        if (mApi == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BuildConfig.DEBUG ? HOST_URL_DEBUG : HOST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mApi = retrofit.create(GogoService.class);
        }
        return mApi;
    }
}
