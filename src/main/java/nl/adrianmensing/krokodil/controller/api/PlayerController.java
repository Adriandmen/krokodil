package nl.adrianmensing.krokodil.controller.api;

import nl.adrianmensing.krokodil.controller.ControllerUtils;
import nl.adrianmensing.krokodil.logic.Player;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PlayerController {
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 7;  // 7 days.

    @GetMapping("/info")
    public Map<String, Object> info(@CookieValue(value = "session_id", defaultValue = "") String sessionID,
                                    HttpServletResponse response) {
        long curr = System.nanoTime();

        Player player = ControllerUtils.getPlayerFromSession(sessionID, response);

        long next = System.nanoTime();

        Map<String, Object> ans = new HashMap<>();
        ans.put("elapsed", "%f ms".formatted(((double) next - curr) / 1_000_000));
        ans.put("body", player);

        return ans;
    }
}
