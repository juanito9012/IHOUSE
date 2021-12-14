package servicios;

import EE.errores.ErrorException;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import dao.DaoComponente;
import lombok.SneakyThrows;
import dao.modelo.Componente;
import dao.modelo.Posicion;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

public class ServiciosComponente {
    @Inject
    private DaoComponente daoComponente;

    public List<Componente> getComponentes(String idHabitacion) {
        if (idHabitacion != null) {
            List<Componente> componentes = daoComponente.getComponentes(idHabitacion);
            componentes.forEach(componente -> componente.setHabitacion(null));
            return componentes;
        }
        throw new ErrorException("Error al coger componentes, NULL", Response.Status.BAD_REQUEST);
    }

    public Componente addComponente(Componente c) {
        if (c != null) {
            return daoComponente.addComponente(c);
        }
        throw new ErrorException("Error al añadir componente, NULL", Response.Status.BAD_REQUEST);
    }

    public Componente updateComponente(Componente c) {
        if (c != null) {
            return daoComponente.updateComponente(c);
        }
        throw new ErrorException("Error al actualizar componente, NULL", Response.Status.BAD_REQUEST);
    }

    public Componente deleteComponente(String idString) {
        if (idString != null) {
            return daoComponente.deleteComponente(idString);
        }
        throw new ErrorException("Error al eliminar componente, NULL", Response.Status.BAD_REQUEST);
    }

    @SneakyThrows
    public Componente moverComponente(Componente c) {
        if (c != null) {
            GpioController gpio = GpioFactory.getInstance();
            GpioPinDigitalOutput pin = null;
            switch (c.getTipoComponente()) {
                case MOTOR:
                    if (c.getPosicion().equals(Posicion.SUBIDO)) {
                        pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(Integer.parseInt(c.getGpiopinMotorSubir())), c.getNombreComponente());
                        pin.low();
                    } else if (c.getPosicion().equals(Posicion.ABAJO)) {
                        pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(Integer.parseInt(c.getGpiopinMotorBajar())), c.getNombreComponente());
                        pin.low();
                    } else if (c.getPosicion().equals(Posicion.PARADO_ARRIBA)) {
                        pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(Integer.parseInt(c.getGpiopinMotorSubir())), c.getNombreComponente());
                        pin.high();
                    } else if (c.getPosicion().equals(Posicion.PARADO_ABAJO)) {
                        pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(Integer.parseInt(c.getGpiopinMotorBajar())), c.getNombreComponente());
                        pin.high();
                    }
                    break;
                case LED:
                    pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(Integer.parseInt(c.getGpioPinLED())), c.getNombreComponente());
                    if (c.getPosicion() == Posicion.OFF) {
                        pin.low();
                    } else if (c.getPosicion() == Posicion.ON) {
                        pin.high();
                    }
            }
            pin.clearProperties();
            gpio.shutdown();
            gpio.unprovisionPin(pin);
            return c;
        }
        throw new ErrorException("Error al mover componente, NULL", Response.Status.BAD_REQUEST);
    }

}