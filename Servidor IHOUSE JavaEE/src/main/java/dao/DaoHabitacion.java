package dao;

import EE.errores.ErrorException;
import config.HibernateUtilsSingleton;
import lombok.extern.log4j.Log4j2;
import dao.modelo.Habitacion;
import org.hibernate.Session;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Log4j2
public class DaoHabitacion {

    public Habitacion deleteHabitacion (String idHabitacion) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        try {
            session.beginTransaction();

            session.createQuery("DELETE FROM Componente where habitacion.id =: ih")
                    .setParameter("ih", idHabitacion)
                    .executeUpdate();

            int eliminado = session.createQuery("DELETE from Habitacion WHERE id =: ih")
                    .setParameter("ih", idHabitacion)
                    .executeUpdate();

            session.getTransaction().commit();

            if (eliminado != 0) {
                return Habitacion.builder().id(idHabitacion).build();
            }
            throw new ErrorException("No se ha eliminado nada reinincie la aplicacion", Response.Status.BAD_REQUEST);
        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al eliminar habitacion", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }

    public Habitacion updateHabitacion (Habitacion h) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        try {

            session.beginTransaction();
            session.update(h);
            session.getTransaction().commit();

            return h;

        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al actualizar habitacion", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }

    public Habitacion addHabitacion (Habitacion h) {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        h.setId(UUID.randomUUID().toString());
        try {

            session.beginTransaction();
            session.save(h);
            session.getTransaction().commit();

            return h;

        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error al añadir habitacion", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }

    public List<Habitacion> getHabitaciones() {
        Session session = HibernateUtilsSingleton.getInstance().getSession();
        try {
            session.beginTransaction();
            List<Habitacion> habitaciones = session.createQuery("from Habitacion")
                    .list();
            session.getTransaction().commit();
            if (habitaciones != null) {
                return habitaciones;
            }
            throw new ErrorException("Esta casa no tiene habitaciones", Response.Status.BAD_REQUEST);

        } catch (Exception e) {
            session.getTransaction().rollback();
            log.error(e.getMessage(), e);
            throw new ErrorException("Error las habitaciones de la casa", Response.Status.BAD_REQUEST);
        } finally {
            session.close();
        }
    }
}
