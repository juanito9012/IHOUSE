package com.thefactory.ihouse.modelo;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Usuario {
    //UUID
    private String idUsuario;
    private String nombreUsuario;
    private TipoUsuario tipoUsuario;
    private String email;
    private String psswd;
    private String claveSimetrica;
    private int interaciones;
    private String salt;
    private String iv;
    private String claveActivacion;


    @Override
    public String toString() {
        return nombreUsuario;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(idUsuario, usuario.idUsuario);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }
}
