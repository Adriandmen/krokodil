package nl.adrianmensing.krokodil.utils.result;

import java.util.Map;

/**
 * The Result interface.
 *
 * Provides a way to wrap successful executions with the to-be-returned object, and an error message for cases
 * where the execution was not successful. This is reminiscent of throwing exceptions, except that for failures,
 * the error message is preserved and can be used later on. An example of where this could be used is when returning
 * unsuccessful API calls with meaningful error messages.
 *
 * @param <T> The type of the wrapped value
 * @since 0.1.0
 */
public interface Result<T> {

    boolean isSuccess();

    boolean isFailure();

    String getErrorMessage();

    T getValue();

    default Object getContent() {
        if (this.isSuccess())
            return this.getValue();
        return this.getErrorMessage();
    }

}
