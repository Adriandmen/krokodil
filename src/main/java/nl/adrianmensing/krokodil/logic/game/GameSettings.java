package nl.adrianmensing.krokodil.logic.game;

import nl.adrianmensing.krokodil.database.Storable;

import java.util.Map;

/**
 * Game settings class.
 *
 * This class is a basic abstract class that contains the settings field,
 * which is a key-value stored data structure. Therefore, it is important to store {@link GameSettings}
 * in a key-value store database, as the queries to retrieve these settings are fairly simple
 * and only update at most one row per query.
 *
 * It is worth mentioning that this is a <i>weak entity</i>, as it has a one-to-one association
 * with a single {@link Game} instance, where the game is considered to be the owner. This means
 * that there is no direct reference from a {@link GameSettings} instance to a {@link Game} instance.
 *
 * @param <T> The {@link GameType} for the settings.
 * @since 0.1.0
 */
public abstract class GameSettings<T extends GameType> implements Storable {
    private Map<String, Object> settings;

    public Map<String, Object> getSettings() {
        return settings;
    }

    /**
     * Updates the setting using the given <code>key</code> and <code>value</code>. This method is purposefully left
     * as an abstract method, in the event that extension of this class require to do <code>key</code> and/or
     * <code>value</code> validation (e.g., whether a certain <code>key</code> is a valid key for that subclass).
     *
     * @param key   The key for the setting that will be updated.
     * @param value The new value for the given key.
     */
    public abstract void updateSetting(String key, Object value);
}
