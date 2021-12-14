package com.thefactory.ihouse.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ConfigurationSingleton_Retrofit_Login {
    private static OkHttpClient clientOK;
    private static Retrofit retrofit;
    private static String url;

    private ConfigurationSingleton_Retrofit_Login() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Retrofit getInstance() {
        if (clientOK == null) {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            clientOK = new OkHttpClient.Builder()
                    .readTimeout(Duration.of(10, ChronoUnit.MINUTES))
                    .callTimeout(Duration.of(10, ChronoUnit.MINUTES))
                    .connectTimeout(Duration.of(10, ChronoUnit.MINUTES))
                    .addInterceptor(chain -> {
                                Request original = chain.request();

                                Request.Builder builder1 = original.newBuilder();
                                Request request = builder1.build();
                                return chain.proceed(request);
                            }
                    )
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .build();
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(clientOK)
                .build();

        return retrofit;
    }

    public static void setUrl(String s) {
        url = s;
    }

    public static String getUrl() {
        return url;
    }
}
