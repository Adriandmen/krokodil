package nl.adrianmensing.krokodil.logic.game.impl.crocodile;

import nl.adrianmensing.krokodil.logic.Player;
import nl.adrianmensing.krokodil.response.impl.ErrorResponse;
import nl.adrianmensing.krokodil.response.impl.JSONResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static nl.adrianmensing.krokodil.logic.game.GameState.INITIALIZING;
import static nl.adrianmensing.krokodil.logic.game.GameState.IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;

public class CrocodileGameTest {

    @Test
    public void SimpleGameInitializationTest() {
        CrocodileGame game = new CrocodileGame();

        Player player1 = new Player("A", "Adnan");
        Player player2 = new Player("B", "Bariuw");
        Player player3 = new Player("C", "Carol");

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        player1 = player1.withGame(game);
        player2 = player2.withGame(game);
        player3 = player3.withGame(game);
        game.setHost(player1);

        assertThat(game.getHost()).isEqualTo(player1);
        assertThat(game.getHost()).isNotIn(player2, player3);
        assertThat(game.getState()).isEqualTo(INITIALIZING);

        assertThat(game.performAction(player2, "START_GAME", null)).isInstanceOf(ErrorResponse.class);
        assertThat(game.getState()).isEqualTo(INITIALIZING);

        assertThat(game.performAction(player3, "START_GAME", null)).isInstanceOf(ErrorResponse.class);
        assertThat(game.getState()).isEqualTo(INITIALIZING);

        assertThat(game.performAction(player1, "START_GAME", null))
                .isNotInstanceOf(ErrorResponse.class)
                .isInstanceOf(JSONResponse.class);
        assertThat(game.getState()).isEqualTo(IN_PROGRESS);

        assertThat(game.getPosition().get("CurrentTurn")).isIn(player1.id(), player2.id(), player3.id());
        assertThat(game.getPosition().get("TeethList")).asList().hasSize(10)
                .allMatch(t -> ((Tooth) t).available())
                .map(t -> ((Tooth) t).number()).isSorted();

        game.getPosition().put("CurrentTurn", player2.id());
        assertThat(game.performAction(player1, "PICK_TOOTH", Map.of("tooth_number", 4))).isInstanceOf(ErrorResponse.class);
        assertThat(game.getPosition().get("TeethList")).asList().allMatch(t -> ((Tooth) t).available());

        assertThat(game.performAction(player2, "PICK_TOOTH", Map.of("tooth_number", 4)))
                .isNotInstanceOf(ErrorResponse.class)
                .isInstanceOf(JSONResponse.class);
        assertThat(game.getPosition().get("TeethList")).asList()
                .doesNotContain(new Tooth(4, true))
                .contains(new Tooth(4, false));
    }
}
