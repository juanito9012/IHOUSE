package EE.rest;

import EE.filtros.Filtered;
import dao.modelo.Componente;
import servicios.ServiciosComponente;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("componente/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Filtered
public class RestComponente {
    @Inject
    private ServiciosComponente sc;

    @GET
    public List<Componente> getComponentes(@QueryParam("idHabitacion") String idHabitacion) {
        return sc.getComponentes(idHabitacion);
    }

    @POST
    public Componente addComponente(Componente c) {
        return sc.addComponente(c);
    }

    @PUT
    public Componente updateComponente(Componente c) {
        return sc.updateComponente(c);
    }

    @DELETE
    public Componente deleteComponente(@QueryParam("idComponente") String idComponente) {
        return sc.deleteComponente(idComponente);
    }

    @PUT
    @Path("/mover")
    public Componente moverComponente(Componente c) {
        return sc.moverComponente(c);

    }

}
