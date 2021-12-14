package dao.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionCasa {
    private String idHabitacion;
    private String idComponente;
    private Posicion posicion;
    private Time hora;
}
