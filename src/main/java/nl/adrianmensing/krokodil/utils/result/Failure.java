package nl.adrianmensing.krokodil.utils.result;

import java.util.NoSuchElementException;

public final class Failure<R> implements Result<R> {

    private final String errorMessage;

    public Failure(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public final R getValue() {
        throw new NoSuchElementException();
    }

}
