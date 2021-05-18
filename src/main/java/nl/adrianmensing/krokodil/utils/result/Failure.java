package nl.adrianmensing.krokodil.utils.result;

import java.util.NoSuchElementException;

public final record Failure<R>(String errorMessage) implements Result<R> {

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
