package servicios;

import EE.errores.ErrorException;
import com.google.gson.Gson;
import dao.DaoUsuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import dao.modelo.Password;
import dao.modelo.TipoUsuario;
import dao.modelo.Usuario;
import dao.modelo.UsuarioMapper;
import org.modelmapper.ModelMapper;
import utils.UtilsCifrado;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
public class ServiciosUsuario {
    @Inject
    private DaoUsuario daoUsuario;
    @Inject
    private ModelMapper mapper;

    public UsuarioMapper registrarUsuario(Usuario usuario) {
        if (usuario != null) {
            Usuario u = daoUsuario.registrarUsuario(usuario);
            return mapper.map(u, UsuarioMapper.class);
        }
        throw new ErrorException("Error al registrar Usuario, NULL", Response.Status.BAD_REQUEST);
    }

    public String login(Usuario u, String path) {
        if (u != null && path != null) {
            Usuario u1 = daoUsuario.getUsuario(u);
            Usuario usuarioBBDD = UtilsCifrado.descifrarContraseña(u1);
            Usuario usuarioLogin = UtilsCifrado.descifrarContraseña(u);

            if (usuarioLogin.getPsswd().equals(usuarioBBDD.getPsswd())) {
                UsuarioMapper um = mapper.map(usuarioBBDD, UsuarioMapper.class);
                return generarJwt(um, path);
            }
            throw new ErrorException("El usuario o la contraseña son incorrectos", Response.Status.BAD_REQUEST);
        }
        throw new ErrorException("Error al hacer login, NULL", Response.Status.BAD_REQUEST);
    }

    public List<UsuarioMapper> getMiembros() {
        List<Usuario> usuarios = daoUsuario.getUsuarios();
        List<UsuarioMapper> usuarioMappers = new ArrayList<>();

        usuarios.forEach(usuario -> {
            UsuarioMapper um = mapper.map(usuario, UsuarioMapper.class);
            usuarioMappers.add(um);
        });
        return usuarioMappers;
    }

    public void verificarUsuario(UsuarioMapper um) {
        if (um != null) {
            daoUsuario.verificarUsuario(um);
        } else {
            throw new ErrorException("Error al verificar usuario, NULL", Response.Status.BAD_REQUEST);
        }
    }

    public UsuarioMapper getUsuario(String nombreUsuario) {
        if (nombreUsuario != null) {
            Usuario u = daoUsuario.getUsuario(nombreUsuario);
            return mapper.map(u, UsuarioMapper.class);
        }
        throw new ErrorException("Error al coger Usuario, NULL", Response.Status.BAD_REQUEST);
    }


    public UsuarioMapper actualizarUsuario(Usuario usuario) {
        if (usuario != null) {
            Usuario u = daoUsuario.updateUsuario(usuario);
            return mapper.map(u, UsuarioMapper.class);
        }
        throw new ErrorException("Error al actualizar Usuario, NULL", Response.Status.BAD_REQUEST);
    }


    public UsuarioMapper eliminarUsuario(String idUsuario) {
        if (idUsuario != null) {
            Usuario u = daoUsuario.deleteUsuario(idUsuario);
            return mapper.map(u, UsuarioMapper.class);
        }
        throw new ErrorException("Error al eliminar Usuario, NULL", Response.Status.BAD_REQUEST);
    }

    public void crearUsuarioAdministrador() {
        Usuario u = Usuario.builder()
                .nombreUsuario("root")
                .psswd("root")
                .email("root@root.com")
                .tipoUsuario(String.valueOf(TipoUsuario.ADMIN))
                .build();
        daoUsuario.crearUsuarioAdministrador(UtilsCifrado.cifrarContraseña(u));
    }

    private String generarJwt(UsuarioMapper um, String path) {
        try {
            Gson gson = new Gson();
            Password psswd = gson.fromJson(Files.newBufferedReader(new File(Paths.get(path + "/psswd.json").toAbsolutePath().toString()).toPath()), Password.class);
            psswd = UtilsCifrado.descifrarContraseña(psswd);

            KeyStore ksLoad = KeyStore.getInstance("PKCS12");
            ksLoad.load(new FileInputStream(path + "/server_cert.pfx"), "".toCharArray());
            KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(psswd.getPassword().toCharArray());
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) ksLoad.getEntry("privada", pp);
            PrivateKey clavePrivada = privateKeyEntry.getPrivateKey();

            String jwt = Jwts.builder()
                    .setIssuer("THEFACTORY:IHOUSE")
                    .setSubject("SERVIDOR_IHOUSE")
                    .claim("nombreUsuario", um.getNombreUsuario())
                    .claim("tipoUsuario", um.getTipoUsuario().toString())
                    .claim("idUsuario", um.getIdUsuario())
                    .setIssuedAt(Date.from(LocalDate.now()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()))
                    .setExpiration(Date.from(LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.UTC)))
                    .signWith(clavePrivada, SignatureAlgorithm.RS512)
                    .compact();

            //Date.from(LocalDate.now().plus(30, ChronoUnit.MINUTES)
            //                            .atStartOfDay(ZoneId.systemDefault())
            //                            .toInstant())
            return jwt;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al generar JWT", Response.Status.BAD_REQUEST);
        }
    }


}
