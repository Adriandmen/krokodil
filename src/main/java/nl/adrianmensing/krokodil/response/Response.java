package nl.adrianmensing.krokodil.response;

import nl.adrianmensing.krokodil.logic.Entity;
import nl.adrianmensing.krokodil.response.impl.ErrorContent;
import nl.adrianmensing.krokodil.utils.result.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    private static ResponseEntity<?> convert(Object contents, HttpStatus status, HttpHeaders headers) {
        return ResponseEntity.status(status).headers(headers).body(contents);
    }

    /**
     * Builds and finalizes the current {@link Response} with the given body, status, and headers.
     * It uses these fields and constructs the proper {@link ResponseEntity} which will be returned.
     * Error messages are constructed and handled with {@link ErrorContent} instances.
     *
     * @return A {@link ResponseEntity} containing either an instance of type <code>&lt;T&gt;</code>, or
     *         an instance with an error message stating what went wrong.
     * @see ErrorContent
     */
    default ResponseEntity<?> build() {
        if (this.body().isSuccess())
            return convert(body().getValue(), status(), headers());
        return convert(new ErrorContent(body().getErrorMessage(), status()).contents(), status(), headers());
    }
}
