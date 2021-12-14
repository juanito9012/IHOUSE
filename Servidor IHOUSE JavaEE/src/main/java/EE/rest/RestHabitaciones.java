package EE.rest;

import EE.filtros.Filtered;
import dao.modelo.Habitacion;
import servicios.ServiciosHabitacion;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("habitacion/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Filtered
public class RestHabitaciones {

    @Inject
    private ServiciosHabitacion sh;

    @GET
    public List<Habitacion> getHabitaciones() {
        return sh.getHabitaciones();
    }

    @POST
    public Habitacion addHabitacion(Habitacion h) {
        return sh.addHabitacion(h);
    }

    @DELETE
    public Habitacion deleteHabitacion(@QueryParam("idHabitacion") String idHabitacion) {
        return sh.deleteHabitacion(idHabitacion);
    }

    @PUT
    public Habitacion updateHabitacion(Habitacion h) {
        return sh.updateHabitacion(h);
    }

}
