package com.thefactory.ihouse.ui.habitaciones;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thefactory.ihouse.modelo.Componente;
import com.thefactory.ihouse.modelo.Habitacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RequiresApi(api = Build.VERSION_CODES.N)
public class HabitacionesViewModel extends ViewModel {

    private MutableLiveData<Map<Habitacion, List<Componente>>> habitacionesComponentesMutableLiveData;
    private MutableLiveData<List<Habitacion>> habitacionesMutableLiveData;


    public HabitacionesViewModel() {
        habitacionesMutableLiveData = new MutableLiveData<>();
        habitacionesComponentesMutableLiveData = new MutableLiveData<>();
    }


    public LiveData<Map<Habitacion, List<Componente>>> getHabitacionesComponentes() {
        return habitacionesComponentesMutableLiveData;
    }

    public void setHabitacionesComponentes(Map<Habitacion, List<Componente>> habitaciones) {
        habitacionesComponentesMutableLiveData.setValue(habitaciones);
    }

    public LiveData<List<Habitacion>> getHabitaciones() {
        return habitacionesMutableLiveData;
    }

    public void setHabitaciones(List<Habitacion> habitaciones) {
        habitacionesMutableLiveData.setValue(habitaciones);
    }

    public void deleteHabitacion(Habitacion h) {
        habitacionesComponentesMutableLiveData.getValue().remove(h, habitacionesComponentesMutableLiveData.getValue().get(h));
        habitacionesMutableLiveData.getValue().remove(h);
    }

    public void actualizarHabitacion(Habitacion habitacionAntes, Habitacion habitacionActualizada) {
        habitacionesMutableLiveData.getValue().remove(habitacionAntes);
        habitacionesMutableLiveData.getValue().add(habitacionActualizada);
        List<Componente> componentes = habitacionesComponentesMutableLiveData.getValue().get(habitacionAntes);
        habitacionesComponentesMutableLiveData.getValue().remove(habitacionAntes, componentes);
        habitacionesComponentesMutableLiveData.getValue().put(habitacionActualizada, componentes);
    }

    public void addHabitacion(Habitacion h) {
        habitacionesComponentesMutableLiveData.getValue().put(h, new ArrayList<>());
        habitacionesMutableLiveData.getValue().add(h);
    }
}