package nl.adrianmensing.krokodil.controller;

import nl.adrianmensing.krokodil.database.service.PlayerDatabaseService;
import nl.adrianmensing.krokodil.model.Player;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@RestController
public class PlayerController {
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 7;  // 7 days.

    @GetMapping("/player")
    public Player testPlayer(
            @CookieValue(value = "session_id", defaultValue = "") String sessionID,
            @RequestParam(value = "username", defaultValue = "Adnan") String username,
            HttpServletResponse response) throws SQLException {

        // Check if there already exists a player associated with an existing session ID.
        if (!sessionID.isEmpty()) {
            Player player = PlayerDatabaseService.getPlayerBySessionID(sessionID);

            if (player != null)
                return player;
        }

        Player player = PlayerDatabaseService.createNewPlayer(username);

        sessionID = PlayerDatabaseService.createSessionIdFromPlayer(player);
        Cookie cookie = new Cookie("session_id", sessionID);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
        return player;
    }
}
