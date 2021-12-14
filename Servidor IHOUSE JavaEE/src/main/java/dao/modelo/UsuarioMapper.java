package dao.modelo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioMapper {
    private String idUsuario;
    private String nombreUsuario;
    private String email;
    private TipoUsuario tipoUsuario;

    @Override
    public String toString() {
        return nombreUsuario;
    }
}
