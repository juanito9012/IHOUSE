package dao.modelo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "casas")
public class Casa {
 //UUID
 @Id
 @Column(name = "id_casa")
 private String id;
 @NaturalId
 @Column(name = "codigo_casa")
 private int codigoCasa;

 @Override
 public String toString() {
  return "ID: " + id + " Codigo Casa: " + codigoCasa;
 }

 // @Basic
 // @OneToMany
 // private List<Habitacion> habitaciones;
}
