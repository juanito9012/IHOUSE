package com.thefactory.ihouse.ui.usuarios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thefactory.ihouse.modelo.Usuario;
import com.thefactory.ihouse.modelo.UsuarioMapper;

import java.util.List;
import java.util.Objects;

public class MiembrosViewModel extends ViewModel {

    private MutableLiveData<List<UsuarioMapper>> usuarios;

    public MiembrosViewModel() {
        usuarios = new MutableLiveData<>();

    }

    public LiveData<List<UsuarioMapper>> getMiembros() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioMapper> miembros) {
        usuarios.setValue(miembros);
    }

    public void actualizarUsuario(UsuarioMapper usuarioAnterior,UsuarioMapper usuarioNuevo) {
        Objects.requireNonNull(usuarios.getValue()).remove(usuarioAnterior);
        usuarios.getValue().add(usuarioNuevo);
    }

    public void addUsuario(UsuarioMapper u) {
        usuarios.getValue().add(u);
    }

    public void eliminarUsuario(UsuarioMapper u) {
        usuarios.getValue().remove(u);
    }

}