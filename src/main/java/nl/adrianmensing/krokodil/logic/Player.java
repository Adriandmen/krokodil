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

public record Player(@NotNull String id, String username, String game) implements Entity, Storable {

    public Player {
        if (username != null && !username.isEmpty() && !UserUtils.isValidUsername(username))
            throw new IllegalArgumentException("Username '%s' is not a valid username".formatted(username));
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
}
