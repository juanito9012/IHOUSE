package com.thefactory.ihouse.dao;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.thefactory.ihouse.dao.retrofit.ComponenteAPI;
import com.thefactory.ihouse.errores.ApiError;
import com.thefactory.ihouse.modelo.Componente;
import com.thefactory.ihouse.modelo.Habitacion;
import com.thefactory.ihouse.utils.ConfigurationSingleton_Retrofit_API;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.vavr.control.Either;
import io.vavr.control.Try;
import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DaoComponentes {
    private ComponenteAPI componenteAPI = ConfigurationSingleton_Retrofit_API.getInstance().create(ComponenteAPI.class);
    private final String DAOCOMPONENTES_DEBUG = "DAOCOMPONENTES_DEBUG";


    public Either<ApiError, List<Componente>> getComponentes(Habitacion h) {
        try {
            Call<List<Componente>> call = componenteAPI.getComponentes(h.getId());
            Response<List<Componente>> response = call.execute();
            if (response.isSuccessful()) {
                return Either.right(response.body());
            } else {
                if (response.code() == 401) {
                    return Either.left(ApiError.builder().message("Unauthorized Error").code(response.code()).fecha(LocalDateTime.now().toString()).build());
                } else {
                    if (response.errorBody().contentType().equals(MediaType.get("application/json"))) {
                        return Either.left(toApiError(response.errorBody().string(),response.code()));
                    } else {
                        return Either.left(ApiError.builder().message("Error de comunicacion").fecha(LocalDateTime.now().toString()).build());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(DAOCOMPONENTES_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al coger componentes").fecha(LocalDateTime.now().toString()).build());
        }
    }

    public Either<ApiError, Componente> addComponente(Componente c) {
        try {
            Call<Componente> call = componenteAPI.addComponente(c);
            Response<Componente> response = call.execute();
            if (response.isSuccessful()) {
                return Either.right(response.body());
            } else {
                if (response.code() == 401) {
                    return Either.left(ApiError.builder().message("Unauthorized Error").code(response.code()).fecha(LocalDateTime.now().toString()).build());
                } else {
                    if (response.errorBody().contentType().equals(MediaType.get("application/json"))) {
                        return Either.left(toApiError(response.errorBody().string(),response.code()));
                    } else {
                        return Either.left(ApiError.builder().message("Error de comunicacion").fecha(LocalDateTime.now().toString()).build());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(DAOCOMPONENTES_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al añadir componente").fecha(LocalDateTime.now().toString()).build());
        }
    }

    public Either<ApiError, Componente> updateComponente(Componente c) {
        try {
            Call<Componente> call = componenteAPI.updateComponente(c);
            Response<Componente> response = call.execute();
            if (response.isSuccessful()) {
                return Either.right(response.body());
            } else {
                if (response.code() == 401) {
                    return Either.left(ApiError.builder().message("Unauthorized Error").code(response.code()).fecha(LocalDateTime.now().toString()).build());
                } else {
                    if (response.errorBody().contentType().equals(MediaType.get("application/json"))) {
                        return Either.left(toApiError(response.errorBody().string(),response.code()));
                    } else {
                        return Either.left(ApiError.builder().message("Error de comunicacion").fecha(LocalDateTime.now().toString()).build());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(DAOCOMPONENTES_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al actualizar componente").fecha(LocalDateTime.now().toString()).build());
        }
    }

    public Either<ApiError, Componente> deleteComponente(Componente c) {
        try {
            Call<Componente> call = componenteAPI.deleteComponente(c.getId());
            Response<Componente> response = call.execute();
            if (response.isSuccessful()) {
                return Either.right(response.body());
            } else {
                if (response.code() == 401) {
                    return Either.left(ApiError.builder().message("Unauthorized Error").code(response.code()).fecha(LocalDateTime.now().toString()).build());
                } else {
                    if (response.errorBody().contentType().equals(MediaType.get("application/json"))) {
                        return Either.left(toApiError(response.errorBody().string(),response.code()));
                    } else {
                        return Either.left(ApiError.builder().message("Error de comunicacion").fecha(LocalDateTime.now().toString()).build());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(DAOCOMPONENTES_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al eliminar componente").fecha(LocalDateTime.now().toString()).build());
        }
    }

    public Either<ApiError, Componente> moverComponente(Componente c) {
        try {
            Call<Componente> call = componenteAPI.moverComponente(c);
            Response<Componente> response = call.execute();
            if (response.isSuccessful()) {
                return Either.right(response.body());
            } else {
                if (response.code() == 401) {
                    return Either.left(ApiError.builder().message("Unauthorized Error").code(response.code()).fecha(LocalDateTime.now().toString()).build());
                } else {
                    if (response.errorBody().contentType().equals(MediaType.get("application/json"))) {
                        return Either.left(toApiError(response.errorBody().string(),response.code()));
                    } else {
                        return Either.left(ApiError.builder().message("Error de comunicacion").fecha(LocalDateTime.now().toString()).build());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(DAOCOMPONENTES_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al mover componente").fecha(LocalDateTime.now().toString()).build());
        }
    }

    private ApiError toApiError(String respuesta, int code) {
        AtomicReference<ApiError> api = new AtomicReference<>();
        Gson gson = new Gson();
        Try.of(() -> gson.fromJson(respuesta, ApiError.class))
                .onSuccess(api::set)
                .onFailure(throwable -> api.set(ApiError.builder().message(throwable.getMessage() + " Error de parseo de la respuesta").code(code).build()));
        return api.get();
    }
}
