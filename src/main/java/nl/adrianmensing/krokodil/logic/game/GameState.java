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

    public static GameState start(GameState state) {
        if (state == GameState.INITIALIZING) {
            return IN_PROGRESS;
        } else {
            throw new IllegalStateTransitionException();
        }
    }

    public static GameState finish(GameState state) {
        if (state == GameState.IN_PROGRESS) {
            return FINISHED;
        } else {
            throw new IllegalStateTransitionException();
        }
    }

    public static GameState reset(GameState state) {
        switch (state) {
            case FINISHED, IN_PROGRESS -> { return INITIALIZING; }
            default -> throw new IllegalStateTransitionException();
        }
    }

    public static GameState stop(GameState state) {
        switch (state) {
            case FINISHED, INITIALIZING -> { return ABANDONED; }
            default -> throw new IllegalStateTransitionException();
        }
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

