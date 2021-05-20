package nl.adrianmensing.krokodil.logic.game.impl.crocodile;

public enum CrocodileGameAction {
    // Admin actions, setting up the game etc.
    START_GAME,
    RESET_GAME,
    CANCEL_GAME,
    STOP_GAME,
    UPDATE_SETTING,

    // Access to current turn
    PICK_TOOTH
}
