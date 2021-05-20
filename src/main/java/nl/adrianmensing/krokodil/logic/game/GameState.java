package nl.adrianmensing.krokodil.logic.game;

import org.jetbrains.annotations.NotNull;

/**
 * Game state class. Indicates in which state a game currently is in.
 *
 * Provided in this class are static helper methods to perform state transitions for states
 * in which those transitions can be applied. Otherwise, an invalid state transition will
 * throw an {@link IllegalStateTransitionException}.
 *
 * @since 0.1.0
 */
public enum GameState {
    INITIALIZING,
    IN_PROGRESS,
    FINISHED,
    ABANDONED;

    public static boolean canStart(GameState state) {
        return state == INITIALIZING;
    }

    public static boolean canFinish(GameState state) {
        return state == IN_PROGRESS;
    }

    public static boolean canReset(GameState state) {
        return state == IN_PROGRESS || state == FINISHED;
    }

    public static boolean canStop(GameState state) {
        return state == INITIALIZING || state == FINISHED;
    }
}

class IllegalStateTransitionException extends RuntimeException {
    public IllegalStateTransitionException() {
        super();
    }

    public IllegalStateTransitionException(@NotNull String message) {
        super(message);
    }
}

