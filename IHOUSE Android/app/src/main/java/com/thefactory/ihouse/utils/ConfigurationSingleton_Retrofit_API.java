package com.thefactory.ihouse.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.*;

import lombok.extern.log4j.Log4j2;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ConfigurationSingleton_Retrofit_API {
    private static OkHttpClient clientOK;
    private static Retrofit retrofit;

    private ConfigurationSingleton_Retrofit_API() {
    }

    public static void cargarInstance(String jwt) {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        clientOK = new OkHttpClient.Builder()
                .readTimeout(Duration.of(10, ChronoUnit.MINUTES))
                .callTimeout(Duration.of(10, ChronoUnit.MINUTES))
                .connectTimeout(Duration.of(10, ChronoUnit.MINUTES))
                .addInterceptor(chain -> {
                            Request original = chain.request();
                            Request.Builder builder1 = original.newBuilder()
                                    .header("Authorization", "Bearer: " + jwt);
                            Request request = builder1.build();
                            return chain.proceed(request);
                        }
                )
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    @Override
                    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                        return LocalDate.parse(jsonElement.getAsJsonPrimitive().getAsString());
                    }
                }).registerTypeAdapter(LocalDate.class, new JsonSerializer() {
                    @Override
                    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
                        return new JsonPrimitive(LocalDate.now().toString());
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString());
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, new JsonSerializer() {
                    @Override
                    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
                        return new JsonPrimitive(LocalDateTime.now().toString());
                    }
                })
                .create();
        
        retrofit = new Retrofit.Builder()
                .baseUrl(ConfigurationSingleton_Retrofit_Login.getUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(clientOK)
                .build();
    }

    public static Retrofit getInstance() {
        return retrofit;
    }
}
