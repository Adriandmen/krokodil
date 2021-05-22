package nl.adrianmensing.krokodil.utils.result;

import java.util.NoSuchElementException;

public final class Success<R> implements Result<R> {

    private final R value;

    public Success(R value) {
        this.value = value;
    }

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
