package nl.adrianmensing.krokodil.logic;

import nl.adrianmensing.krokodil.database.Storable;
import nl.adrianmensing.krokodil.utils.UserUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public record Player(@NotNull String id, String username) implements Entity, Storable {

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

    public Optional<Player> setUsername(@NotNull String username) {
        username = username.trim();

        if (UserUtils.isValidUsername(username))
            return Optional.of(new Player(this.id, username));

        return Optional.empty();
    }
}
