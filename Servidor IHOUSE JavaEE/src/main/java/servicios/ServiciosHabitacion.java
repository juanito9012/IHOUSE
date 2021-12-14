package servicios;

import EE.errores.ErrorException;
import dao.DaoHabitacion;
import dao.modelo.Habitacion;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

public class ServiciosHabitacion {
    @Inject
    private DaoHabitacion daoHabitacion;

    public List<Habitacion> getHabitaciones() {
        return daoHabitacion.getHabitaciones();
    }

    public Habitacion addHabitacion(Habitacion h) {
        if (h != null){
            return daoHabitacion.addHabitacion(h);
        }
        throw new ErrorException("Error al añadir habitacion, NULL", Response.Status.BAD_REQUEST);
    }

    public Habitacion deleteHabitacion(String idHabitacion) {
        if(idHabitacion != null){
            return daoHabitacion.deleteHabitacion(idHabitacion);
        }
        throw new ErrorException("Error al eliminar habitacion, NULL", Response.Status.BAD_REQUEST);
    }

    public Habitacion updateHabitacion(Habitacion h) {
        if (h != null){
            return daoHabitacion.updateHabitacion(h);
        }
        throw new ErrorException("Error al actualizar habitacion, NULL", Response.Status.BAD_REQUEST);
    }
}
