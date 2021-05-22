package nl.adrianmensing.krokodil.controller.web;

import nl.adrianmensing.krokodil.controller.ControllerUtils;
import nl.adrianmensing.krokodil.database.manager.PlayerDataManager;
import nl.adrianmensing.krokodil.logic.Player;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class MainController {

    @GetMapping("/")
    public String hello(@CookieValue(value = "session_id", defaultValue = "") String sessionID,
                        HttpServletResponse response) {
        Player player = ControllerUtils.getPlayerFromSession(sessionID, response);
        PlayerDataManager.savePlayer(player);


        return "index";
    }

    @GetMapping("/welcome")
    public String welcome(@CookieValue(value = "session_id", required = false) String sessionID,
                          HttpServletResponse response) {
        if (sessionID == null || PlayerDataManager.getPlayerByID(sessionID) == null) {
            response.setHeader("Location", "/");
            response.setStatus(HttpStatus.FOUND.value());
        }

        return "welcome";
    }
}
