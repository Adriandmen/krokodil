package nl.adrianmensing.krokodil.logic;

import nl.adrianmensing.krokodil.database.Storable;

import java.sql.Timestamp;

public class Session implements Storable {
    private final String sessionID;
    private final Player player;
    private final Timestamp expireTime;

    public Session(String sessionID, Player player, Timestamp expireTime) {
        this.sessionID = sessionID;
        this.player = player;
        this.expireTime = expireTime;
    }

    public String getSessionID() {
        return sessionID;
    }

    public Player getPlayer() {
        return player;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }
}
