package nl.adrianmensing.krokodil.logic;

import nl.adrianmensing.krokodil.database.Storable;
import nl.adrianmensing.krokodil.utils.UserUtils;
import org.jetbrains.annotations.NotNull;

public class Player implements Entity, Storable {
    private final Integer id;
    private String username;

    public Player(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public void setUsername(@NotNull String username) {
        username = username.trim();
        if (UserUtils.isValidUsername(username)) {
            this.username = username;
        }
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
