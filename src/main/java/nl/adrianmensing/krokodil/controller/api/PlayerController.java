package nl.adrianmensing.krokodil.controller.api;

import nl.adrianmensing.krokodil.controller.ControllerUtils;
import nl.adrianmensing.krokodil.database.manager.PlayerDataManager;
import nl.adrianmensing.krokodil.logic.Player;
import nl.adrianmensing.krokodil.response.Response;
import nl.adrianmensing.krokodil.response.impl.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @GetMapping("/info")
    public Map<String, Object> info(@CookieValue(value = "session_id", defaultValue = "") String sessionID,
                                    HttpServletResponse response) {
        long curr = System.nanoTime();

        Player player = PlayerDataManager.getPlayerByID(sessionID);

        long next = System.nanoTime();

        Map<String, Object> ans = new HashMap<>();
        ans.put("elapsed", "%f ms".formatted(((double) next - curr) / 1_000_000).replace(',', '.'));
        ans.put("body", player);

        return ans;
    }

    @PutMapping("/username")
    public ResponseEntity<?> username(@CookieValue(value = "session_id") String playerID,
                                      @RequestParam(value = "username") String username) {
        Player player = PlayerDataManager.getPlayerByID(playerID);
        if (player == null)
            return new ErrorResponse<Player>("Invalid 'session_id' received.", HttpStatus.UNAUTHORIZED).build();

        Response<Player> response = player.setUsername(username);

        if (response.result().isSuccess()) {
            PlayerDataManager.savePlayer(response.result().getValue());
        }

        System.out.println("saved to DB");

        return response.build();
    }
}
