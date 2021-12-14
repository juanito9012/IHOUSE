package com.thefactory.ihouse.dao.retrofit;

import com.thefactory.ihouse.modelo.Usuario;
import com.thefactory.ihouse.modelo.UsuarioMapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UsuarioAPI {
    @POST("domotica/user/register")
    Call<UsuarioMapper> registrarUsuario(@Body Usuario u);

    @POST("domotica/user/login")
    Call<String> loginUsuario(@Body Usuario u);

    @PUT("domotica/user")
    Call<UsuarioMapper> actualizarUsuario(@Body Usuario u);

    @GET("domotica/user/miembros")
    Call<List<UsuarioMapper>> getMiembros();

    @GET("domotica/user")
    Call<UsuarioMapper> getUsuario(@Query("nombreUsuario") String nombreUsuario);

    @DELETE("domotica/user")
    Call<UsuarioMapper> eliminarUsuario(@Query("idUsuario") String idUsuario);

}
