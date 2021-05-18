package nl.adrianmensing.krokodil.controller.api;

import nl.adrianmensing.krokodil.database.manager.GameDataManager;
import nl.adrianmensing.krokodil.database.manager.PlayerDataManager;
import nl.adrianmensing.krokodil.logic.Player;
import nl.adrianmensing.krokodil.logic.game.Game;
import nl.adrianmensing.krokodil.logic.game.GameType;
import nl.adrianmensing.krokodil.logic.game.impl.crocodile.CrocodileGame;
import nl.adrianmensing.krokodil.response.impl.ErrorResponse;
import nl.adrianmensing.krokodil.response.impl.JSONResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @GetMapping("/")
    public ResponseEntity<?> getGameInfo(@CookieValue(value = "session_id") String sessionID) {
        Player player = PlayerDataManager.getPlayerByID(sessionID);
        if (player == null)
            return new ErrorResponse<Player>("Invalid 'session_id' received", HttpStatus.UNAUTHORIZED).build();

        if (player.game() == null)
            return new ErrorResponse<Player>("Cannot find currently joined game", HttpStatus.BAD_REQUEST).build();

        Game<?> game = GameDataManager.getGameByID(player.game());

        return new JSONResponse<>(game).build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGame(@CookieValue(value = "session_id") String sessionID) {
        Player player = PlayerDataManager.getPlayerByID(sessionID);
        if (player == null)
            return new ErrorResponse<Player>("Invalid 'session_id' received", HttpStatus.UNAUTHORIZED).build();

        if (player.game() != null)
            return new ErrorResponse<Player>("Cannot create a new game when already joined in another game", HttpStatus.BAD_REQUEST).build();

        if (!player.hasValidUsername())
            return new ErrorResponse<Player>("Cannot create a new game without a valid username", HttpStatus.BAD_REQUEST).build();

        Game<GameType.CrocodileGameType> game = new CrocodileGame();
        player = new Player(player.id(), player.username(), game.getId());
        game.addPlayer(player);
        game.setHost(player);

        GameDataManager.saveGame(game);
        PlayerDataManager.savePlayer(player);

        return new JSONResponse<>(player).build();
    }
}
