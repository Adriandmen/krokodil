package nl.adrianmensing.krokodil.controller.api;

import nl.adrianmensing.krokodil.database.manager.GameDataManager;
import nl.adrianmensing.krokodil.database.manager.PlayerDataManager;
import nl.adrianmensing.krokodil.logic.Player;
import nl.adrianmensing.krokodil.logic.game.Game;
import nl.adrianmensing.krokodil.logic.game.GameType;
import nl.adrianmensing.krokodil.logic.game.impl.crocodile.CrocodileGame;
import nl.adrianmensing.krokodil.response.Response;
import nl.adrianmensing.krokodil.response.impl.ErrorResponse;
import nl.adrianmensing.krokodil.response.impl.JSONResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @GetMapping("")
    public ResponseEntity<?> getGameInfo(@CookieValue(value = "session_id") String sessionID) {
        Player player = PlayerDataManager.getPlayerByID(sessionID);
        if (player == null)
            return new ErrorResponse<Player>("Invalid 'session_id' received", HttpStatus.UNAUTHORIZED).build();

        if (!player.inGame())
            return new ErrorResponse<Player>("Cannot find currently joined game", HttpStatus.BAD_REQUEST).build();

        Game<?> game = GameDataManager.getGameByID(player.game());

        return new JSONResponse<>(game).build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGame(@CookieValue(value = "session_id") String sessionID) {
        Player player = PlayerDataManager.getPlayerByID(sessionID);
        if (player == null)
            return new ErrorResponse<>("Invalid 'session_id' received", HttpStatus.UNAUTHORIZED).build();

        if (player.inGame() && GameDataManager.getGameByID(player.game()) != null)
            return new ErrorResponse<>("Cannot create a new game when already joined in another game", HttpStatus.BAD_REQUEST).build();

        if (!player.hasValidUsername())
            return new ErrorResponse<>("Cannot create a new game without a valid username", HttpStatus.BAD_REQUEST).build();

        Game<GameType.CrocodileGameType> game = new CrocodileGame();
        player = new Player(player.id(), player.username(), game.getId());
        game.addPlayer(player);
        game.setHost(player);

        GameDataManager.saveGame(game);
        PlayerDataManager.savePlayer(player);

        return new JSONResponse<>(player).build();
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinGame(@CookieValue(value = "session_id") String sessionID,
                                      @RequestParam(value = "game_id") String gameID) {
        Player player = PlayerDataManager.getPlayerByID(sessionID);
        if (player == null)
            return new ErrorResponse<>("Unrecognized token", HttpStatus.UNAUTHORIZED).build();
        if (player.inGame() && GameDataManager.getGameByID(player.game()) != null)
            return new ErrorResponse<>("Already joined another game", HttpStatus.BAD_REQUEST).build();
        if (!player.hasValidUsername())
            return new ErrorResponse<>("Cannot join a game without a valid username", HttpStatus.BAD_REQUEST).build();

        Game<?> game = GameDataManager.getGameByID(gameID);
        if (game == null)
            return new ErrorResponse<>("Could not find Game with the given ID", HttpStatus.NOT_FOUND).build();

        game.addPlayer(player);
        player = player.withGame(game);

        GameDataManager.saveGame(game);
        PlayerDataManager.savePlayer(player);

        return new JSONResponse<>(player).build();
    }

    @PostMapping("/action")
    public ResponseEntity<?> postAction(@CookieValue(value = "session_id") String sessionID,
                                        @RequestParam(value = "action_name") String actionName,
                                        @RequestParam(value = "params", required = false) String params) {
        Player player = PlayerDataManager.getPlayerByID(sessionID);
        if (player == null || !player.inGame())
            return new ErrorResponse<>("Unrecognized token", HttpStatus.UNAUTHORIZED).build();

        Game<?> game = GameDataManager.getGameByID(player.game());
        if (game == null)
            return new ErrorResponse<>("Unable to find the corresponding game", HttpStatus.NOT_FOUND).build();

        Map<String, Object> p = new HashMap<>();

        if (params != null)
            Arrays.stream(params.split(";")).forEach(s -> p.put(s.split("=")[0], s.split("=")[1]));
        Response<?> response = game.performAction(player, actionName, p);

        GameDataManager.saveGame(game);
        return response.build();
    }
}
