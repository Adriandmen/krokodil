package nl.adrianmensing.krokodil.logic;

import nl.adrianmensing.krokodil.database.Storable;
import nl.adrianmensing.krokodil.utils.UserUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record Player(Integer id, String username) implements Entity, Storable {

    public Player {
        if (username != null && !UserUtils.isValidUsername(username))
            throw new IllegalArgumentException("Username '%s' is not a valid username".formatted(username));
    }

    public Player(Integer id) {
        this(id, null);
    }

    public Optional<Player> setUsername(@NotNull String username) {
        username = username.trim();

        if (UserUtils.isValidUsername(username))
            return Optional.of(new Player(this.id, username));

        return Optional.empty();
    }

}
