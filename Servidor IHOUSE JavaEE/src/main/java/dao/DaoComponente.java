package dao;

import EE.errores.ErrorException;
import config.HibernateUtilsSingleton;
import lombok.extern.log4j.Log4j2;
import dao.modelo.Componente;
import org.hibernate.Session;

import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.UUID;

@Log4j2
public class DaoComponente {

    public List<Componente> getComponentes(String idHabitacion) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        try {
            session.beginTransaction();

            List<Componente> componentes = session.createQuery("from Componente where habitacion.id = :i", Componente.class)
                    .setParameter("i", idHabitacion)
                    .list();

            session.getTransaction().commit();

            return componentes;
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error la casa", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }

    public Componente addComponente(Componente c) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();

        c.setId(UUID.randomUUID().toString());
        try {
            session.beginTransaction();
            session.save(c);
            session.getTransaction().commit();
            return c;
        } catch (PersistenceException e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            if (e.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new ErrorException("El pin ya esta usado", Response.Status.BAD_REQUEST);
            } else {
                throw new ErrorException(e.getMessage(), Response.Status.BAD_REQUEST);
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al añadir componente", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }

    }

    public Componente updateComponente(Componente c) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        try {
            session.beginTransaction();
            session.update(c);
            session.getTransaction().commit();
            return c;
        } catch (PersistenceException e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            if (e.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new ErrorException("el pin ya esta usado", Response.Status.BAD_REQUEST);
            } else {
                throw new ErrorException(e.getMessage(), Response.Status.BAD_REQUEST);
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al actualizar la informacion del componente", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }

    }

    public Componente deleteComponente(String idComponente) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();

        try {
            session.beginTransaction();
            session.createQuery("delete from Componente where id_componente =: idComponente ")
              .setParameter("idComponente", idComponente)
              .executeUpdate();
            session.getTransaction().commit();
            return Componente.builder()
                    .id(idComponente).build();
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al eliminar componente", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }


}
