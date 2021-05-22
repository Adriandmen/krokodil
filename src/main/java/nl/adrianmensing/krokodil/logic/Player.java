package nl.adrianmensing.krokodil.logic;

import nl.adrianmensing.krokodil.database.Storable;
import nl.adrianmensing.krokodil.logic.game.Game;
import nl.adrianmensing.krokodil.response.Response;
import nl.adrianmensing.krokodil.response.impl.ErrorResponse;
import nl.adrianmensing.krokodil.response.impl.JSONResponse;
import nl.adrianmensing.krokodil.utils.UserUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class Player implements Entity, Storable {
    private final String id;
    private final String username;
    private final String game;

    public String id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String game() {
        return game;
    }

    public Player(@NotNull String id, String username, String game) {
        this.id = id;
        this.username = username;
        this.game = game;
    }

    public Player() {
        this(UUID.randomUUID().toString());
    }

    public Player(String id) {
        this(id, null);
    }

    public Player(String id, String username) {
        this(id, username, null);
    }

    @NotNull
    @Contract("_ -> new")
    public Response<Player> setUsername(@NotNull String username) {
        username = username.trim();

        if (UserUtils.isValidUsername(username))
            return new JSONResponse<>(new Player(this.id, username, this.game));

        return new ErrorResponse<>("The given username is invalid.", HttpStatus.BAD_REQUEST);
    }

    public boolean hasValidUsername() {
        return this.username != null && UserUtils.isValidUsername(this.username);
    }

    public Player withGame(Game<?> game) {
        return new Player(this.id, this.username, game.getId());
    }

    public boolean inGame() {
        return this.game != null && !this.game.isEmpty();
    }
}
