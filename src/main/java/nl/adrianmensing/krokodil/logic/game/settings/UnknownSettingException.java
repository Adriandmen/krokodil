package nl.adrianmensing.krokodil.logic.game.settings;

public class UnknownSettingException extends RuntimeException {
    public UnknownSettingException() {
        super();
    }

    public UnknownSettingException(String message) {
        super(message);
    }
}
