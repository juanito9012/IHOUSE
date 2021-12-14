package EE.errores;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.ws.rs.core.Response;


@EqualsAndHashCode(callSuper = true)
@Data
public class ErrorException extends RuntimeException {

  private Response.Status codigo;

  public ErrorException(String message, Response.Status codigo) {
    super(message);
    this.codigo = codigo;
  }

}
