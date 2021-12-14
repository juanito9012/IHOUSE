package com.thefactory.ihouse.dao;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.thefactory.ihouse.dao.retrofit.UsuarioAPI;
import com.thefactory.ihouse.errores.ApiError;
import com.thefactory.ihouse.modelo.Usuario;
import com.thefactory.ihouse.modelo.UsuarioMapper;
import com.thefactory.ihouse.utils.ConfigurationSingleton_Retrofit_API;
import com.thefactory.ihouse.utils.ConfigurationSingleton_Retrofit_Login;

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
public class DaoUsuario {
    private UsuarioAPI usuarioAPILogin = ConfigurationSingleton_Retrofit_Login.getInstance().create(UsuarioAPI.class);
    private static UsuarioAPI usuarioAPI;
    private final String DAOUSUARIO_DEBUG = "DAOUSUARIO_DEBUG";


    public Either<ApiError, UsuarioMapper> registrarUsuario(Usuario u) {
        try {
            Call<UsuarioMapper> call = usuarioAPI.registrarUsuario(u);
            Response<UsuarioMapper> response = call.execute();
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
            Log.e(DAOUSUARIO_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al registrar Usuario").fecha(LocalDateTime.now().toString()).build());
        }
    }

    public Either<ApiError, String> loginUsuario(Usuario u) {
        try {
            Call<String> call = usuarioAPILogin.loginUsuario(u);
            Response<String> response = call.execute();
            if (response.isSuccessful()) {
                //Se recoge el JWT y se inserta en la configuracion Singleton
                ConfigurationSingleton_Retrofit_API.cargarInstance(response.body());
                usuarioAPI = ConfigurationSingleton_Retrofit_API.getInstance().create(UsuarioAPI.class);
                return Either.right(response.body());
            } else {
                if (response.errorBody().contentType().equals(MediaType.get("application/json"))) {
                    return Either.left(toApiError(response.errorBody().string(),response.code()));
                } else {
                    return Either.left(ApiError.builder().message("Error de comunicacion").fecha(LocalDateTime.now().toString()).build());
                }
            }
        } catch (Exception e) {
            Log.e(DAOUSUARIO_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al hacer login Usuario").fecha(LocalDateTime.now().toString()).build());

        }
    }


    public Either<ApiError, List<UsuarioMapper>> getMiembros() {
        try {
            Call<List<UsuarioMapper>> call = usuarioAPI.getMiembros();
            Response<List<UsuarioMapper>> response = call.execute();
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
            Log.e(DAOUSUARIO_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al coger miembros").fecha(LocalDateTime.now().toString()).build());

        }
    }

    public Either<ApiError, UsuarioMapper> getUsuario(Usuario u) {
        try {
            Call<UsuarioMapper> call = usuarioAPI.getUsuario(u.getNombreUsuario());
            Response<UsuarioMapper> response = call.execute();
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
            Log.e(DAOUSUARIO_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al coger usuario").fecha(LocalDateTime.now().toString()).build());

        }
    }


    public Either<ApiError, UsuarioMapper> actualizarUsuario(Usuario u) {
        try {
            Call<UsuarioMapper> call = usuarioAPI.actualizarUsuario(u);
            Response<UsuarioMapper> response = call.execute();
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
            Log.e(DAOUSUARIO_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al actualizar usuario").fecha(LocalDateTime.now().toString()).build());

        }
    }

    public Either<ApiError, UsuarioMapper> eliminarUsuario(UsuarioMapper um) {
        try {
            Call<UsuarioMapper> call = usuarioAPI.eliminarUsuario(um.getIdUsuario());
            Response<UsuarioMapper> response = call.execute();
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
            Log.e(DAOUSUARIO_DEBUG, e.getMessage(), e);
            return Either.left(ApiError.builder().message("Error al eliminar usuario").fecha(LocalDateTime.now().toString()).build());

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
