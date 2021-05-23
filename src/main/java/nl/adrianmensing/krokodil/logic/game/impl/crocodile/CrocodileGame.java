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

    private static final String BAD_TOOTH = "BadTooth";
    private static final String TOOTH_NUMBER = "tooth_number";
    private static final String TEETH_LIST = "TeethList";
    private static final String CURRENT_TURN = "CurrentTurn";

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
        this.salt = GameIDGenerator.randomGameID(16);
    }

    @Override
    public Response<?> performAction(Player player, String action, Map<String, Object> params) {
        try {
            switch (CrocodileGameAction.valueOf(action)) {
                case START_GAME     -> { return this.startGame(player);             }
                case STOP_GAME      -> { return this.stopGame(player);              }
                case RESET_GAME     -> { return this.resetGame(player);             }
                case PICK_TOOTH     -> { return this.pickTooth(player, params);     }
                case UPDATE_SETTING -> { return this.updateSetting(player, params); }
                default -> { return new ErrorResponse<>("Unrecognized action received", HttpStatus.BAD_REQUEST); }
            }
        } catch (IllegalArgumentException e) {
            return new ErrorResponse<>("Unrecognized action received", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void start() {
        Random random = new Random();

        // Initialization using the current settings.
        if (!this.position.containsKey(CURRENT_TURN)) {
            int index = random.nextInt(this.players.size());
            String nextTurnID = this.players.get(index);
            this.position.put(CURRENT_TURN, nextTurnID);
        }

        List<Tooth> teeth = new ArrayList<>();
        int teethCount;
        Object teethCountSetting = this.settings.getSettings().get(TEETH_COUNT);

        if (teethCountSetting instanceof BigDecimal d) {
            teethCount = d.intValue();
        } else if (teethCountSetting instanceof Integer n) {
            teethCount = n;
        } else {
            throw new RuntimeException("Type mismatch");
        }

        for (int index = 1; index <= teethCount; index++) {
            teeth.add(new Tooth(index));
        }

        Tooth badTooth = teeth.get(random.nextInt(teeth.size()));

        this.position.put(BAD_TOOTH, CrocodileGameUtils.hashedTooth(badTooth, salt));
        this.position.put(TEETH_LIST, teeth);
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

    private Response<?> updateSetting(Player player, Map<String, Object> params) {
        if (!this.host.id().equals(player.id()))
            return new ErrorResponse<>(HttpStatus.UNAUTHORIZED);
        if (this.state != GameState.INITIALIZING)
            return new ErrorResponse<>("Cannot update settings in current state", HttpStatus.BAD_REQUEST);
        if (!params.containsKey("key"))
            return new ErrorResponse<>("Missing param field 'key'", HttpStatus.BAD_REQUEST);
        if (!params.containsKey("value"))
            return new ErrorResponse<>("Missing param field 'value'", HttpStatus.BAD_REQUEST);

        String key = (String) params.get("key");
        Object val = params.get("value");

        return this.settings.updateSetting(key, val);
    }

    public boolean isBadTooth(Tooth tooth) {
        return position.get(BAD_TOOTH).equals(CrocodileGameUtils.hashedTooth(tooth, salt));
    }

    private Response<CrocodileGame> pickTooth(Player player, Map<String, Object> params) {
        if (this.state != GameState.IN_PROGRESS)
            return new ErrorResponse<>("Cannot pick tooth in the current state", HttpStatus.BAD_REQUEST);
        if (!params.containsKey(TOOTH_NUMBER))
            return new ErrorResponse<>("Missing 'tooth_number' param", HttpStatus.BAD_REQUEST);
        if (!this.position.getOrDefault(CURRENT_TURN, "<none>").equals(player.id()))
            return new ErrorResponse<>("Player ID does not match current player turn", HttpStatus.BAD_REQUEST);

        @SuppressWarnings("unchecked")
        List<LinkedHashMap<String, Object>> teethLinked = ((List<LinkedHashMap<String, Object>>) this.position.get(TEETH_LIST));
        List<Tooth> teeth = new ArrayList<>();
        teethLinked.forEach(m -> teeth.add(new Tooth(((BigDecimal) m.get("number")).intValue(), ((boolean) m.get("available")))));

        int toothNumber = ((int) params.get(TOOTH_NUMBER)) - 1;

        System.out.println(teeth);
        System.out.println(toothNumber);
        Tooth pickedTooth = teeth.get(toothNumber);

        if (!pickedTooth.available())
            return new ErrorResponse<>("Invalid tooth number chosen", HttpStatus.BAD_REQUEST);
        teeth.set(toothNumber, new Tooth(pickedTooth.number(), false));
        position.put(TEETH_LIST, teeth);

        this.processTurn(pickedTooth);

        return new JSONResponse<>(this);
    }

    private void processTurn(Tooth tooth) {
        if (isBadTooth(tooth)) {
            this.position.put("X", tooth.number());
            this.finish();
        }

        this.setNextPlayerTurn();
    }

    private void setNextPlayerTurn() {
        String currentTurnPlayerID = (String) position.get(CURRENT_TURN);
        int currentIndex = players.indexOf(currentTurnPlayerID);
        int nextIndex = (currentIndex + 1) % players.size();
        position.put(CURRENT_TURN, players.get(nextIndex));
    }
}
