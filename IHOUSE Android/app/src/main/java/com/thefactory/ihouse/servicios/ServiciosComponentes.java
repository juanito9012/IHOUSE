package com.thefactory.ihouse.servicios;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.thefactory.ihouse.dao.DaoComponentes;
import com.thefactory.ihouse.errores.ApiError;
import com.thefactory.ihouse.modelo.Componente;
import com.thefactory.ihouse.modelo.Habitacion;

import java.util.List;

import io.vavr.control.Either;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ServiciosComponentes {

    private DaoComponentes daoComponentes;


    public Either<ApiError, List<Componente>> getComponentes(Habitacion h) {
        daoComponentes = new DaoComponentes();
        return daoComponentes.getComponentes(h);
    }

    public Either<ApiError, Componente> addComponente(Componente c) {
        daoComponentes = new DaoComponentes();
        return daoComponentes.addComponente(c);
    }

    public Either<ApiError, Componente> updateComponente(Componente c)  {
        daoComponentes = new DaoComponentes();

        return daoComponentes.updateComponente(c);
    }

    public Either<ApiError, Componente> deleteComponente(Componente c) {
        daoComponentes = new DaoComponentes();

        return daoComponentes.deleteComponente(c);
    }

    public Either<ApiError, Componente> moverComponente(Componente c)  {
        daoComponentes = new DaoComponentes();
        return daoComponentes.moverComponente(c);
    }

}
