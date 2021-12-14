package EE.filtros;

import EE.errores.ErrorException;
import io.jsonwebtoken.*;
import dao.modelo.TipoUsuario;
import dao.modelo.UsuarioMapper;
import servicios.ServiciosUsuario;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.X509Certificate;


@Provider
@Filtered
public class ServerFilterJAX implements ContainerRequestFilter {

    @Context
    private HttpServletRequest request;
    @Inject
    private ServiciosUsuario su;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        String jwt = request.getHeader("Authorization");
        PublicKey clavePublica = getClavePublica();

        if (jwt != null) {
            try {

                Jws<Claims> jwts = Jwts.parserBuilder()
                        .setSigningKey(clavePublica)
                        .build()
                        .parseClaimsJws(jwt.substring(8));

                UsuarioMapper um = UsuarioMapper.builder()
                        .idUsuario((String) jwts.getBody().get("idUsuario"))
                        .tipoUsuario(TipoUsuario.valueOf((String) jwts.getBody().get("tipoUsuario")))
                        .nombreUsuario((String) jwts.getBody().get("nombreUsuario"))
                        .build();

                su.verificarUsuario(um);

            } catch (ExpiredJwtException ex) {
                throw new ErrorException("El token a expirado", Response.Status.UNAUTHORIZED);
            } catch (JwtException ex) {
                throw new ErrorException("Clave publica del token erronea", Response.Status.UNAUTHORIZED);
            }
        }
    }


    private PublicKey getClavePublica() {
        try {
            KeyStore ksLoad = KeyStore.getInstance("PKCS12");
            ksLoad.load(new FileInputStream(request.getServletContext().getRealPath("/WEB-INF/server_cert.pfx")), "".toCharArray());

            X509Certificate certLoad = (X509Certificate) ksLoad.getCertificate("publica");
            return certLoad.getPublicKey();

        } catch (Exception ex) {
            throw new ErrorException("Error al coger la clave publica", Response.Status.BAD_REQUEST);
        }
    }
}