package nl.adrianmensing.krokodil.utils.result;

import java.util.NoSuchElementException;

public final record Success<R>(R value) implements Result<R> {
    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        throw new NoSuchElementException();
    }

    @Override
    public final R getValue() {
        return value;
    }
}
