package EE.rest;

import EE.filtros.Filtered;
import dao.modelo.Usuario;
import dao.modelo.UsuarioMapper;
import servicios.ServiciosUsuario;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("user/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestUsuarios {
    @Inject
    private ServiciosUsuario su;

    @POST
    @Path("register/")
    public UsuarioMapper registrarUsuario(Usuario u) {
        return su.registrarUsuario(u);
    }

    @POST
    @Path("login/")
    public String loginUsuario(Usuario u, @Context HttpServletRequest request) {
        String path = request.getServletContext().getRealPath("WEB-INF");
        return su.login(u, path);
    }

    @GET
    @Path(("/miembros"))
    @Filtered
    public List<UsuarioMapper> getMiembros() {
        return su.getMiembros();
    }

    @GET
    @Filtered
    public UsuarioMapper getUsuario(@QueryParam("nombreUsuario") String nombreUsuario) {
        return su.getUsuario(nombreUsuario);
    }

    @PUT
    @Filtered
    public UsuarioMapper actualizarUsuario(Usuario u) {
        return su.actualizarUsuario(u);
    }

    @DELETE
    @Filtered
    public UsuarioMapper eliminarUsuario(@QueryParam("idUsuario") String idUsuario) {
        return su.eliminarUsuario(idUsuario);
    }

}
