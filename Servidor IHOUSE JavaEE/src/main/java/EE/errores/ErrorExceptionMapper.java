package EE.errores;

import dao.errores.ApiError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.LocalDateTime;


@Provider
public class ErrorExceptionMapper implements ExceptionMapper<ErrorException> {

    public Response toResponse(ErrorException exception) {
        ApiError apiError = ApiError.builder().fecha(LocalDateTime.now().toString()).message(exception.getMessage()).code(exception.getCodigo().getStatusCode()).build();
        return Response.status(exception.getCodigo())
                .entity(apiError)
                .type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}