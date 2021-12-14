package dao.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "habitaciones")
public class Habitacion {
    //UUID
    @Id
    @Column(name = "id_habitacion")
    private String id;
    @Basic
    @Column(name = "nombre_habitacion")
    private String nombreHabitacion;
    @Basic
    @Column(name = "lugar")
    private String lugar;
//    @OneToMany
//    @JoinColumn(name = "id_componente",referencedColumnName = "id_componente")
//    private List<Componente> componentes;

    @Override
    public String toString() {
        return nombreHabitacion;
    }
}
