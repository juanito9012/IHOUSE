package dao.modelo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")

public class Usuario {
    //UUID
    @Id
    @Column(name = "id_usuario")
    private String idUsuario;
    @Column(name = "nombre_usuario")
    @Basic
    private String nombreUsuario;
    @Basic
    @Column(name = "tipo_usuario")
    private String tipoUsuario;
    @Basic
    @Column(name = "email")
    private String email;
    @Basic
    @Column(name = "password")
    private String psswd;
    @Basic
    @Column(name = "clave_simetrica")
    private String claveSimetrica;
    @Basic
    @Column(name = "interacciones")
    private int interaciones;
    @Basic
    @Column(name = "salt")
    private String salt;
    @Basic
    @Column(name = "iv")
    private String iv;

    @Override
    public String toString() {
        return nombreUsuario;
    }

}
