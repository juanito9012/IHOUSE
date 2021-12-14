package dao.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Password {
    private String password;
    private int interaciones;
    private String salt;
    private String iv;
    private String claveSimetrica;
}
