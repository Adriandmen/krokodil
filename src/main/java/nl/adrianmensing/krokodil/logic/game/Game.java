package nl.adrianmensing.krokodil.logic.game;

import nl.adrianmensing.krokodil.database.manager.DataManager;
import nl.adrianmensing.krokodil.database.Storable;
import nl.adrianmensing.krokodil.logic.Player;
import nl.adrianmensing.krokodil.logic.game.settings.GameSettings;
import nl.adrianmensing.krokodil.response.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * The Game class.
 *
 * This class has a one-to-many association with {@link Player} instances, where a single game
 * can have multiple players. Because of this relationship, careful attention needs to be paid to the
 * getters and eventual setters of any extensions of this class. Preferably, we would like to get the data
 * of these fields from some implementation of the {@link DataManager}. For games that are in progress,
 * retrieval of this data will be cached and should not have a lot of impact on performance.
 *
 * @param <T> The {@link GameType} denoting the type of the game.
 * @since 0.1.0
 */
public abstract class Game<T extends GameType> implements Storable {
    protected String id;
    protected List<String> players;
    protected Player host;
    protected GameState state;
    protected Map<String, Object> position;
    protected GameSettings<T> settings;

    public String getId() {
        return id;
    }

    public List<String> getPlayers() {
        return players;
    }

    public Player getHost() {
        return host;
    }

    public GameState getState() {
        return state;
    }

    public Map<String, Object> getPosition() {
        return position;
    }

    public GameSettings<T> getSettings() {
        return settings;
    }

    public void addPlayer(@NotNull Player player) {
        if (this.state != GameState.INITIALIZING)
            return;
        if (players.contains(player.id()))
            return;

        players.add(player.id());
    }

    public void removePlayer(String playerID) {
        players.remove(playerID);
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setPosition(Map<String, Object> position) {
        this.position = position;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }

    /**
     * Perform an action with the given params on behalf of the given {@link Player}. This action
     * will then be executed, depending on whether the given params are valid, and returns a {@link Response}
     * instance containing the info along with it.
     *
     * @param player The {@link Player} that is making this request. This is important to pass along, in order
     *               to check whether this player has the correct role to perform certain actions.
     * @param action The name of the actions that needs to be performed.
     * @param params The (additional) params used by the action.
     * @return       A {@link Response} instance containing information about the execution of the action.
     */
    public abstract Response<?> performAction(Player player, String action, @Nullable Map<String, Object> params);

    public abstract void start();

    public abstract void finish();

    public abstract void reset();

    public abstract void stop();
}
