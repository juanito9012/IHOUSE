package com.thefactory.ihouse.servicios;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.thefactory.ihouse.dao.DaoUsuario;
import com.thefactory.ihouse.errores.ApiError;
import com.thefactory.ihouse.modelo.Usuario;
import com.thefactory.ihouse.modelo.UsuarioMapper;
import com.thefactory.ihouse.utils.UtilsCifrado;

import java.util.List;

import io.vavr.control.Either;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ServiciosUsuarios {

    private DaoUsuario daoUsuario;

    public Either<ApiError, UsuarioMapper> registrarUsuario(Usuario u) {
        daoUsuario = new DaoUsuario();
        return daoUsuario.registrarUsuario(UtilsCifrado.cifrarContraseña(u));
    }

    public Either<ApiError, String> loginUsuario(Usuario u) {
        daoUsuario = new DaoUsuario();
        return daoUsuario.loginUsuario(UtilsCifrado.cifrarContraseña(u));
    }

    public Either<ApiError, UsuarioMapper> getUsuario(Usuario u) {
        daoUsuario = new DaoUsuario();
        return daoUsuario.getUsuario(u);
    }


    public Either<ApiError, List<UsuarioMapper>> getMiembros() {
        daoUsuario = new DaoUsuario();
        return daoUsuario.getMiembros();
    }

    public Either<ApiError, UsuarioMapper> actulizarUsuario(Usuario u) {
        daoUsuario = new DaoUsuario();

        if (u.getPsswd() != null){
            u = UtilsCifrado.cifrarContraseña(u);
        }
        return daoUsuario.actualizarUsuario(u);
    }

    public Either<ApiError, UsuarioMapper> eliminarUsuario(UsuarioMapper um) {
        daoUsuario = new DaoUsuario();
        return daoUsuario.eliminarUsuario(um);
    }
}
