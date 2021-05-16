package nl.adrianmensing.krokodil.logic.game.impl.crocodile;

import nl.adrianmensing.krokodil.logic.game.settings.GameSettings;
import nl.adrianmensing.krokodil.logic.game.GameType.CrocodileGameType;
import nl.adrianmensing.krokodil.logic.game.settings.TypeMismatchException;
import nl.adrianmensing.krokodil.logic.game.settings.UnknownSettingException;

import java.util.HashMap;
import java.util.Map;

public final class CrocodileSettings extends GameSettings<CrocodileGameType> {
    public static final String TEETH_COUNT = "teeth_count";
    public static final String BAD_TEETH_COUNT = "bad_teeth_count";

    private static final Map<String, Class<?>> ALLOWED_SETTINGS = new HashMap<>();

    static {
        ALLOWED_SETTINGS.put(TEETH_COUNT, Integer.class);
        ALLOWED_SETTINGS.put(BAD_TEETH_COUNT, Integer.class);
    }

    public CrocodileSettings() {
        this.updateSetting(TEETH_COUNT, 10);
        this.updateSetting(BAD_TEETH_COUNT, 1);
    }

    @Override
    public void updateSetting(String key, Object value) {
        if (!ALLOWED_SETTINGS.containsKey(key))
            throw new UnknownSettingException("Unknown key '%s' is not a valid setting key.".formatted(key));

        // Perform some type validation before processing the update.
        // This ensures that the types will be consistent throughout the runtime.
        Class<?> settingType = ALLOWED_SETTINGS.get(key);

        // Equivalent of the `value instanceof settingType` condition.
        if (!settingType.isInstance(value)) {
            String errorMessage = "The value '%s' is not of type '%s'.";
            throw new TypeMismatchException(errorMessage.formatted(value.toString(), settingType.getName()));
        }

        this.settings.put(key, value);
    }
}
