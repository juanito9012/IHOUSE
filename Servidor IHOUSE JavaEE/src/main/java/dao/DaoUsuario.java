package dao;

import EE.errores.ErrorException;
import config.HibernateUtilsSingleton;
import lombok.extern.log4j.Log4j2;
import dao.modelo.TipoUsuario;
import dao.modelo.Usuario;
import dao.modelo.UsuarioMapper;
import org.hibernate.Session;

import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.UUID;

@Log4j2
public class DaoUsuario {

    public Usuario registrarUsuario(Usuario u) {
        //Objeto no tiene id autoincremental asi que se hace transaccion
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        u.setIdUsuario(UUID.randomUUID().toString());
        try {
            session.beginTransaction();
            session.save(u);
            session.getTransaction().commit();
            //Hacemos un cambio de objeto para no devolver la contraseña
            return u;
        } catch (PersistenceException e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            if (e.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new ErrorException("El usuario ya existe", Response.Status.BAD_REQUEST);
            } else {
                throw new ErrorException(e.getMessage(), Response.Status.BAD_REQUEST);
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al añadir usuario", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }

    public void crearUsuarioAdministrador(Usuario u) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        u.setIdUsuario(UUID.randomUUID().toString());
        try {
            session.beginTransaction();
            int numeroAdministradores = session.createQuery("FROM Usuario WHERE tipoUsuario =: administrador")
                    .setParameter("administrador", String.valueOf(TipoUsuario.ADMIN))
                    .list().size();
            session.getTransaction().commit();

            if (numeroAdministradores == 0) {
                session.beginTransaction();
                session.save(u);
                session.getTransaction().commit();
            }
        } catch (PersistenceException e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            if (e.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new ErrorException("El usuario ya existe", Response.Status.BAD_REQUEST);
            } else {
                throw new ErrorException(e.getMessage(), Response.Status.BAD_REQUEST);
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al añadir usuario", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }

    public Usuario updateUsuario(Usuario u) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        try {
            session.beginTransaction();
            if (u.getPsswd() != null) {
                session.update(u);
            } else {
                session.createQuery("UPDATE Usuario SET nombreUsuario =: nombreUsuario, tipoUsuario =: tipoUsuario, email =: email WHERE idUsuario =: idUsuario")
                        .setParameter("nombreUsuario", u.getNombreUsuario())
                        .setParameter("tipoUsuario", u.getTipoUsuario())
                        .setParameter("email", u.getEmail())
                        .setParameter("idUsuario", u.getIdUsuario())
                        .executeUpdate();
            }
            session.getTransaction().commit();
            return u;
        } catch (PersistenceException e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            if (e.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new ErrorException("El usuario ya existe", Response.Status.BAD_REQUEST);
            } else {
                throw new ErrorException(e.getMessage(), Response.Status.BAD_REQUEST);
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al actualizar usuario", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }

    public Usuario deleteUsuario(String idUsuario) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        try {
            session.beginTransaction();
            int eliminados = session.createQuery("DELETE FROM Usuario WHERE idUsuario =: idUsuario ")
                    .setParameter("idUsuario", idUsuario)
                    .executeUpdate();
            session.getTransaction().commit();

            if (eliminados == 1){
                return Usuario.builder().idUsuario(idUsuario).build();
            }
            throw new ErrorException("No se ha borrado ningun usuario", Response.Status.BAD_REQUEST);

        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al eliminar Usuario", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }


    public Usuario getUsuario(Usuario u) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        try {
            session.beginTransaction();
            Usuario u1 = session.createQuery("FROM Usuario WHERE nombreUsuario =: nombreUsuario", Usuario.class)
                    .setParameter("nombreUsuario", u.getNombreUsuario())
                    .list().stream()
                    .findFirst()
                    .orElse(null);
            session.getTransaction().commit();
            if (u1 != null) {
                return u1;
            }
            throw new ErrorException("El usuario o la contraseña son incorrectos", Response.Status.BAD_REQUEST);

        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("El usuario o la contraseña son incorrectos", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }

    public List<Usuario> getUsuarios() {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        try {
            session.beginTransaction();
            List<Usuario> usuarios = session.createQuery("FROM Usuario")
                    .list();
            session.getTransaction().commit();
            return usuarios;
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al coger miembros de la casa", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }

    public void verificarUsuario(UsuarioMapper um) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        Usuario u = null;
        try {
            session.beginTransaction();
            u = session.createQuery("FROM Usuario WHERE nombreUsuario =: nombreUsuario", Usuario.class)
                    .setParameter("nombreUsuario", um.getNombreUsuario())
                    .list()
                    .stream()
                    .findFirst()
                    .orElse(null);
            session.getTransaction().commit();

            if (u != null) {
                if (!TipoUsuario.valueOf(u.getTipoUsuario()).equals(um.getTipoUsuario())) {
                    throw new ErrorException("Error al verificar usuario", Response.Status.UNAUTHORIZED);
                }
            }else {
                throw new ErrorException("Error al verificar usuario", Response.Status.UNAUTHORIZED);
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            if (!TipoUsuario.valueOf(u.getTipoUsuario()).equals(um.getTipoUsuario())) {
                throw new ErrorException("Error al verificar usuario", Response.Status.UNAUTHORIZED);
            }
            throw new ErrorException("Error al verificar usuario", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }


    public Usuario getUsuario(String nombreUsuario) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        try {
            session.beginTransaction();
            Usuario u = session.createQuery("FROM Usuario WHERE nombreUsuario =: nombreUsuario", Usuario.class)
                    .setParameter("nombreUsuario", nombreUsuario)
                    .list().stream()
                    .findFirst()
                    .orElse(null);
            session.getTransaction().commit();
            return u;
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al coger Usuario", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }
}
