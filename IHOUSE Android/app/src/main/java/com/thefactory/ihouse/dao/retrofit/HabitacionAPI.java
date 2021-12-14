package com.thefactory.ihouse.dao.retrofit;


import com.thefactory.ihouse.modelo.Habitacion;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

import java.util.List;

public interface HabitacionAPI {

    @GET("domotica/habitacion")
    Call<List<Habitacion>> getHabitaciones();

    @POST("domotica/habitacion")
    Call<Habitacion> addHabitacion(@Body Habitacion h);

    @DELETE("domotica/habitacion")
    Call<Habitacion> deleteHabitacion(@Query("idHabitacion") String idHabitacion);

    @PUT("domotica/habitacion")
    Call<Habitacion> updateHabitacion(@Body Habitacion h);
}
