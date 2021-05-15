package nl.adrianmensing.krokodil.logic;

import nl.adrianmensing.krokodil.database.Storable;
import nl.adrianmensing.krokodil.utils.UserUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Player implements Entity, Storable {
    private final Integer id;
    private String username;
    private Session session;

    public Player(int id, String username) {
        this.id = id;
        this.username = username;
        this.session = null;
    }

    public void setUsername(@NotNull String username) {
        username = username.trim();
        if (UserUtils.isValidUsername(username)) {
            this.username = username;
        }

        // TODO: should this return void?
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player))
            return false;

        Player other = (Player) obj;

        return Objects.equals(this.id, other.id)
            && Objects.equals(this.username, other.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.username);
    }
}
