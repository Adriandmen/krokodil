package nl.adrianmensing.krokodil.logic.game.settings;

public class TypeMismatchException extends RuntimeException {
    public TypeMismatchException() {
        super();
    }

    public TypeMismatchException(String message) {
        super(message);
    }
}
