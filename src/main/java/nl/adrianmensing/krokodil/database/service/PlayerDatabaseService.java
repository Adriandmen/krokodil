package nl.adrianmensing.krokodil.database.service;

import nl.adrianmensing.krokodil.database.DatabaseDriver;
import nl.adrianmensing.krokodil.model.Player;
import nl.adrianmensing.krokodil.utils.SessionIDGenerator;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class PlayerDatabaseService implements DatabaseService<Player> {

    private static final int SESSION_ID_LENGTH = 32;

    /**
     * Retrieves the player data by association of the given session id, and transforms
     * this data into a {@link Player} object.
     *
     * @param sessionID The session id associated with the player.
     * @return          A {@link Player} object if such an association was found, null otherwise.
     */
    public static Player getPlayerBySessionID(String sessionID) throws SQLException {
        PreparedStatement statement = DatabaseDriver
                .getConnection()
                .prepareStatement("""
                    SELECT p.PlayerID, p.Username FROM players AS p INNER JOIN (
                        SELECT PlayerID
                        FROM session_player
                        WHERE SessionID = ?
                    ) AS sp on p.PlayerID = sp.PlayerID;
                 """);

        statement.setString(1, sessionID);
        ResultSet result = statement.executeQuery();

        if (result.next()) {
            int playerID = result.getInt("PlayerID");
            String username = result.getString("Username");

            return new Player(playerID, username);
        }

        return null;
    }

    public static String createSessionIdFromPlayer(Player player) throws SQLException {
        PreparedStatement statement = DatabaseDriver
                .getConnection()
                .prepareStatement("""
                    INSERT INTO session_player (SessionID, PlayerID, ExpireDate)
                    VALUES (?, ?, ?);
                """);

        String randomSessionID = SessionIDGenerator.randomSessionID(SESSION_ID_LENGTH);
        int playerID = player.getId();
        Timestamp expireTime = Timestamp.from(Instant.now().plus(7, ChronoUnit.DAYS));

        statement.setString(1, randomSessionID);
        statement.setInt(2, playerID);
        statement.setTimestamp(3, expireTime);

        if (statement.executeUpdate() > 0)
            return randomSessionID;

        throw new SQLException("Could not create session ID for player.");
    }

    public static Player createNewPlayer(String username) throws SQLException {
        PreparedStatement statement = DatabaseDriver
                .getConnection()
                .prepareStatement("""
                    INSERT INTO players (Username) VALUES (?);
                """, new String[] { "PlayerID" });

        statement.setString(1, username);

        if (statement.executeUpdate() > 0) {
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int playerID = generatedKeys.getInt(1);
                return new Player(playerID, username);
            }
        }

        throw new RuntimeException("Could not create new player.");
    }

    @Override
    public Player readColumnFromDatabase(ResultSet result) {
        return null;
    }

    @Override
    public Player writeColumnToDatabase(Player column) {
        return null;
    }
}
