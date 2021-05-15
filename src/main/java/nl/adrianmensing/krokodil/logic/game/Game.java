package nl.adrianmensing.krokodil.logic.game;

import nl.adrianmensing.krokodil.database.manager.DataManager;
import nl.adrianmensing.krokodil.database.Storable;
import nl.adrianmensing.krokodil.logic.Player;

import java.util.List;

/**
 * The Game class.
 *
 * This class has a one-to-many association with {@link Player} instances, where a single game
 * can have multiple players. Because of this relationship, careful attention need to be paid to the
 * getters and eventual setters of any extensions of this class. Preferably, we would like to get the data
 * of these fields from some implementation of the {@link DataManager}. For games that are in progress,
 * retrieval of this data will be cached and should not have a lot of impact on performance.
 *
 * @param <T> The {@link GameType} denoting the type of the game.
 * @since 0.1.0
 */
public abstract class Game<T extends GameType> implements Storable {
    protected Integer id;
    protected List<Player> players;
    protected Player host;
    protected GameState state;
    protected GameSettings<T> settings;

    public List<Player> getPlayers() {
        return players;
    }

    public Player getHost() {
        return host;
    }

    public GameState getState() {
        return state;
    }

    public GameSettings<T> getSettings() {
        return settings;
    }

    public abstract void start();

    public abstract void finish();

    public abstract void reset();

    public abstract void stop();
}
