package dao.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "componentes")
public class Componente {
    //UUID
    @Id
    @Column(name = "id_componente")
    private String id;
    @Basic
    @Column(name = "nombre_componente")
    private String nombreComponente;
    @Basic
    @Column(name = "tipo_componente")
    private TipoComponente tipoComponente;
    @Basic
    @Column(name = "fecha_instalacion")
    private LocalDate fechaInstalacion;
    @Basic
    @Column(name = "gpiopinLED")
    private String gpioPinLED;
    @Basic
    @Column(name = "gpiopin_motor_subir")
    private String gpiopinMotorSubir;
    @Basic
    @Column(name = "gpiopin_motor_bajar")
    private String gpiopinMotorBajar;
    @ManyToOne
    @JoinColumn(name = "id_habitacion", referencedColumnName = "id_habitacion")
    private Habitacion habitacion;

    @Transient
    private Posicion posicion;

    @Override
    public String toString() {
        return nombreComponente;
    }
}
