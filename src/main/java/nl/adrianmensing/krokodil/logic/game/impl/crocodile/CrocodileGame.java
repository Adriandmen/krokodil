package nl.adrianmensing.krokodil.logic.game.impl.crocodile;

import nl.adrianmensing.krokodil.logic.game.Game;
import nl.adrianmensing.krokodil.logic.game.GameState;
import nl.adrianmensing.krokodil.logic.game.GameType.CrocodileGameType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

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
        this(UUID.randomUUID().toString());
    }

    private CrocodileGame(String id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.host = null;
        this.state = GameState.INITIALIZING;
        this.settings = new CrocodileSettings();
    }

    @Override
    public void start() {
        this.state = GameState.start(this.state);
    }

    @Override
    public void finish() {
        this.state = GameState.finish(this.state);
    }

    @Override
    public void reset() {
        this.state = GameState.reset(this.state);
    }

    @Override
    public void stop() {
        this.state = GameState.stop(this.state);
    }
}
