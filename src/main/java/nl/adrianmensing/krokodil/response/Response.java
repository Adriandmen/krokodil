package nl.adrianmensing.krokodil.response;

import nl.adrianmensing.krokodil.logic.Entity;
import nl.adrianmensing.krokodil.utils.result.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * The Response interface.
 *
 * This is an interface that generalizes the implementation of the {@link ResponseEntity} class, that
 * also features an additional <code>update</code> method, used to update a Response entity accordingly,
 * in a 'builder pattern'-like fashion.
 *
 * Instances of implementations of this interfaces should be considered as simplified classes for returning
 * {@link ResponseEntity} instances. For more advanced configurations, consider using the {@link ResponseEntity} class
 * itself or the {@link javax.servlet.http.HttpServletResponse} in the controllers.
 *
 * @param <T> The return type of the response entity.
 * @since 0.1.0
 */
public interface Response<T> {

    Result<T> body();

    HttpStatus status();

    HttpHeaders headers();

    Response<T> update(Entity entity);

    static <R> ResponseEntity<R> convert(Result<R> body, HttpStatus status, HttpHeaders headers) {
        if (body.isSuccess())
            return ResponseEntity.status(status).headers(headers).body(body.getValue());
        return ResponseEntity.status(status).headers(headers).build();
    }

    default ResponseEntity<T> build() {
        return convert(body(), status(), headers());
    }

}
