package nl.adrianmensing.krokodil.logic.game.impl.crocodile;

import nl.adrianmensing.krokodil.logic.Player;
import nl.adrianmensing.krokodil.logic.game.Game;
import nl.adrianmensing.krokodil.logic.game.GameState;
import nl.adrianmensing.krokodil.logic.game.GameType.CrocodileGameType;
import nl.adrianmensing.krokodil.response.Response;
import nl.adrianmensing.krokodil.response.impl.ErrorResponse;
import nl.adrianmensing.krokodil.response.impl.JSONResponse;
import nl.adrianmensing.krokodil.utils.GameIDGenerator;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;

import static nl.adrianmensing.krokodil.logic.game.impl.crocodile.CrocodileSettings.TEETH_COUNT;

/**
 * The Crocodile game implementation.
 *
 * This class is essentially the 'heart' of the Crocodile game, as in this is the place where
 * it handles most of the logistics behind the game.
 *
 * @since 0.1.0
 */
public final class CrocodileGame extends Game<CrocodileGameType> {

    public CrocodileGame() {
        this(GameIDGenerator.randomGameID(6));
    }

    private CrocodileGame(String id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.host = null;
        this.state = GameState.INITIALIZING;
        this.settings = new CrocodileSettings();
        this.position = new HashMap<>();
    }

    @Override
    public Response<?> performAction(Player player, String action, Map<String, Object> params) {
        switch (CrocodileGameAction.valueOf(action)) {
            case START_GAME -> { return this.startGame(player); }
            case STOP_GAME  -> { return this.stopGame(player); }
            case RESET_GAME -> { return this.resetGame(player); }
            case PICK_TOOTH -> { return this.pickTooth(player, params); }
            default -> { return new JSONResponse<>("Unrecognized action received", HttpStatus.BAD_REQUEST); }
        }
    }

    @Override
    public void start() {
        // Initialization using the current settings.
        if (!this.position.containsKey("CurrentTurn")) {
            Random random = new Random();
            int index = random.nextInt(this.players.size());
            String nextTurnID = this.players.get(index);
            this.position.put("CurrentTurn", nextTurnID);
        }

        List<Tooth> teeth = new ArrayList<>();
        int teethCount = ((BigDecimal) this.settings.getSettings().get(TEETH_COUNT)).intValue();
        for (int index = 0; index < teethCount; index++) {
            teeth.add(new Tooth(index, true));
        }

        this.position.put("TeethList", teeth);
        this.state = GameState.IN_PROGRESS;
    }

    @Override
    public void finish() {
        this.state = GameState.FINISHED;
    }

    @Override
    public void reset() {
        this.state = GameState.INITIALIZING;
    }

    @Override
    public void stop() {
        this.state = GameState.ABANDONED;
    }

    @NotNull
    private Response<?> startGame(Player player) {
        if (!this.host.id().equals(player.id()))
            return new ErrorResponse<>("Only the owner of the game can start a game", HttpStatus.UNAUTHORIZED);

        if (!GameState.canStart(this.state))
            return new ErrorResponse<>("Invalid state to start a game", HttpStatus.BAD_REQUEST);

        this.start();
        return new JSONResponse<>(this);
    }

    @NotNull
    private Response<?> resetGame(Player player) {
        if (!this.host.id().equals(player.id()))
            return new ErrorResponse<>("Only the owner of the game can reset a game", HttpStatus.UNAUTHORIZED);

        if (!GameState.canReset(this.state))
            return new ErrorResponse<>("Invalid state to reset a game", HttpStatus.BAD_REQUEST);

        this.reset();
        return new JSONResponse<>(this);
    }

    @NotNull
    private Response<?> stopGame(Player player) {
        if (!this.host.id().equals(player.id()))
            return new ErrorResponse<>("Only the owner of the game can stop a game", HttpStatus.UNAUTHORIZED);

        if (!GameState.canStop(this.state))
            return new ErrorResponse<>("Invalid state to stop a game", HttpStatus.BAD_REQUEST);

        this.stop();
        return new JSONResponse<>(this);
    }

    private Response<CrocodileGame> pickTooth(Player player, Map<String, Object> params) {
        if (this.state != GameState.IN_PROGRESS)
            return new ErrorResponse<>("Cannot pick tooth in the current state", HttpStatus.BAD_REQUEST);
        if (!params.containsKey("tooth_number"))
            return new ErrorResponse<>("Missing 'tooth_number' param", HttpStatus.BAD_REQUEST);
        if (!this.position.getOrDefault("CurrentTurn", "").equals(player.id()))
            return new ErrorResponse<>("Player ID does not match current player turn", HttpStatus.BAD_REQUEST);

        @SuppressWarnings("unchecked")
        List<Tooth> teeth = ((List<Tooth>) this.position.get("ToothList"));
        int toothNumber = ((int) params.get("tooth_number"));

        Tooth tooth = teeth.get(toothNumber);

        if (!tooth.available())
            return new ErrorResponse<>("Invalid tooth number chosen", HttpStatus.BAD_REQUEST);
        teeth.set(toothNumber, new Tooth(tooth.number(), false));
        position.put("ToothList", teeth);

        // TODO: next turn

        return new JSONResponse<>(this);
    }
}
