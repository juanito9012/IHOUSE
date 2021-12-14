package com.thefactory.ihouse.dao.retrofit;


import com.thefactory.ihouse.modelo.Componente;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ComponenteAPI {

    @POST("domotica/componente")
    Call<Componente> addComponente(@Body Componente c);

    @GET("domotica/componente")
    Call<List<Componente>> getComponentes(@Query("idHabitacion") String idHabitacion);

    @PUT("domotica/componente")
    Call<Componente> updateComponente(@Body Componente c);

    @DELETE("domotica/componente")
    Call<Componente> deleteComponente(@Query("idComponente") String idComponente);

    @PUT("domotica/componente/mover")
    Call<Componente> moverComponente(@Body Componente c);
}
