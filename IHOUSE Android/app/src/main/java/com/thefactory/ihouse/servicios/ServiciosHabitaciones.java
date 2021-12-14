package com.thefactory.ihouse.servicios;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.thefactory.ihouse.dao.DaoHabitaciones;
import com.thefactory.ihouse.errores.ApiError;
import com.thefactory.ihouse.modelo.Habitacion;

import java.util.List;

import io.vavr.control.Either;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ServiciosHabitaciones {

    private DaoHabitaciones daoHabitaciones;

    //Borrar Habitaciones

    public Either<ApiError, List<Habitacion>> getHabitaciones() {
        daoHabitaciones = new DaoHabitaciones();
        return daoHabitaciones.getHabitaciones();
    }

    public Either<ApiError, Habitacion> addHabitacion(Habitacion h) {
        daoHabitaciones = new DaoHabitaciones();
        return daoHabitaciones.addHabitacion(h);
    }

    public Either<ApiError, Habitacion> deleteHabitacion(Habitacion h) {
        daoHabitaciones = new DaoHabitaciones();
        return daoHabitaciones.deleteHabitacion(h);
    }

    public Either<ApiError, Habitacion> updateHabitacion(Habitacion h) {
        daoHabitaciones = new DaoHabitaciones();
        return daoHabitaciones.updateHabitacion(h);
    }
}
