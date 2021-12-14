package com.thefactory.ihouse.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Componente {
    //UUID
    private String id;
    private String nombreComponente;
    private TipoComponente tipoComponente;
    private LocalDate fechaInstalacion;
    private String gpioPinLED;
    private String gpiopinMotorSubir;
    private String gpiopinMotorBajar;
    private Habitacion habitacion;
    private Posicion posicion;

    @Override
    public String toString() {
        return nombreComponente;
    }
}
