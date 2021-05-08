package nl.adrianmensing.krokodil.model;

public class Player extends StorableModel {
    private final Integer id;
    private final String username;

    public Player(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
