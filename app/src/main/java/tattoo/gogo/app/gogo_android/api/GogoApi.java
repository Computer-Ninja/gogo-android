package tattoo.gogo.app.gogo_android.api;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings.Secure;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tattoo.gogo.app.gogo_android.GogoAndroid;
import tattoo.gogo.app.gogo_android.GogoConst;

/**
 * Created by delirium on 17-3-2.
 */

public class GogoApi {

    public static final String HOST_URL = "http://api.gogo.tattoo:12345/";
    //public static final String HOST_URL = "http://192.168.1.153:12346/";
    private static GogoService mApi;

    public static GogoService getApi() {
        if (mApi == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        String id = Secure.getString(GogoAndroid.getInstance().getContentResolver(),
                                Secure.ANDROID_ID);
                        String auth = GogoConst.O_AUTH_AUTHENTICATION;
                        PackageInfo pInfo = null;
                        try {
                            pInfo = GogoAndroid.getInstance().getPackageManager()
                                    .getPackageInfo(GogoAndroid.getInstance()
                                            .getPackageName(), 0);
                            auth += ";" + pInfo.versionName;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        Request request = chain.request();
                        Request newRequest;

                        newRequest = request.newBuilder()
                                .addHeader(GogoConst.HEADER_AUTHONRIZATION, auth)
                                .addHeader(GogoConst.HEADER_X_CLIENT_ID, id)
                                .build();
                        return chain.proceed(newRequest);

                    })
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
