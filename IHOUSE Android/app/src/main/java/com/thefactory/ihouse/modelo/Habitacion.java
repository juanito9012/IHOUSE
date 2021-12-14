package com.thefactory.ihouse.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitacion {
    //UUID
    private String id;
    private String nombreHabitacion;
    private String lugar;
//    @OneToMany
//    @JoinColumn(name = "id_componente",referencedColumnName = "id_componente")
//    private List<Componente> componentes;

    @Override
    public String toString() {
        return nombreHabitacion;
    }
}
