package nl.adrianmensing.krokodil.controller;

import nl.adrianmensing.krokodil.database.manager.PlayerDataManager;
import nl.adrianmensing.krokodil.logic.Player;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class ControllerUtils {
    /**
     * The maximum age for the 'Player Cookie'. Currently set to (60 * 60 * 24) = 1 day * 7 = 7 days.
     */
    private static final int PLAYER_COOKIE_MAX_AGE = 60 * 60 * 24 * 7;

    @NotNull
    public static Cookie cookieFromPlayer(@NotNull Player player) {
        Cookie cookie = new Cookie("session_id", player.id());
        cookie.setMaxAge(PLAYER_COOKIE_MAX_AGE);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    /**
     * Gets a {@link Player} instance from the given sessionID, a value from a cookie. If this does
     * not exist, or contains an invalid PlayerID, a new Player is created. The ID of the player is
     * equivalent to the sessionID and is returned as a cookie <i>implicitly</i>, using the passed
     * {@link HttpServletResponse}. The cookie is created and added in the response.
     *
     * @param sessionID The PlayerID retrieved from the cookie or empty if it does not exist
     * @param response  The {@link HttpServletResponse} in which the cookie is set for the PlayerID
     * @return          The resulting {@link Player} object from the given sessionID, or a new instance
     *                  when the sessionID is empty or is an invalid PlayerID.
     */
    @NotNull
    public static Player getPlayerFromSession(@NotNull String sessionID, HttpServletResponse response) {
        Player player = null;
        if (!sessionID.isEmpty()) {
            player = PlayerDataManager.getPlayerByID(sessionID);
        }

        if (player == null) {
            player = new Player();
            PlayerDataManager.savePlayer(player);
        }

        Cookie cookie = ControllerUtils.cookieFromPlayer(player);
        response.addCookie(cookie);
        return player;
    }
}
