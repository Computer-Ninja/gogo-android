package tattoo.gogo.app.gogo_android.api;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by delirium on 17-3-2.
 */

public class GogoApi {

    public static final String HOST_URL = "http://api.gogo.tattoo:12345/";
    //public static final String HOST_URL = "http://192.168.1.153:12345/";
    private static GogoService mApi;

    public static GogoService getApi() {
        if (mApi == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .build();
            Gson gson = new GsonBuilder()
                    .addSerializationExclusionStrategy(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            if (f.getName().equals("id")) {
                                return true;
                            }
                            return false;
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(HOST_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            mApi = retrofit.create(GogoService.class);
        }
        return mApi;
    }
}
