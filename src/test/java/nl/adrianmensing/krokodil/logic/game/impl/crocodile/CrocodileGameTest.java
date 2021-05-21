package nl.adrianmensing.krokodil.logic.game.impl.crocodile;

import nl.adrianmensing.krokodil.logic.Player;
import nl.adrianmensing.krokodil.response.Response;
import nl.adrianmensing.krokodil.response.impl.ErrorResponse;
import nl.adrianmensing.krokodil.response.impl.JSONResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static nl.adrianmensing.krokodil.logic.game.GameState.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Not exactly unit tests but more semi-e2e tests.
 */
public class CrocodileGameTest {

    private CrocodileGame game;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    public void setup() {
        game = new CrocodileGame();
        player1 = new Player("A", "Adnan");
        player2 = new Player("B", "Bariuw");
        player3 = new Player("C", "Carol");
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        player1 = player1.withGame(game);
        player2 = player2.withGame(game);
        player3 = player3.withGame(game);
        game.setHost(player1);
    }

    // TODO: worst tests i've written in a while, but it is 1AM so fuck it.
    //       simplify/refactor when possible.

    @Test
    public void SimpleGameInitializationTest() {
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

    @Test
    public void PickBadToothInGameTest() {
        game.performAction(player1, "START_GAME");

        assertThat(game.getState()).isEqualTo(IN_PROGRESS);

        game.getPosition().put("CurrentTurn", player2.id());

        Optional<Tooth> optBadTooth = getTeethList(game).stream().filter(game::isBadTooth).findFirst();
        String currentPlayerID = ((String) game.getPosition().get("CurrentTurn"));
        assertThat(optBadTooth).isNotEmpty();
        assertThat(currentPlayerID).isEqualTo(player2.id());

        Tooth badTooth = optBadTooth.get();
        Response<?> response = game.performAction(player2, "PICK_TOOTH", Map.of("tooth_number", badTooth.number()));

        assertThat(response).isInstanceOf(JSONResponse.class);
        assertThat(game.getState()).isEqualTo(FINISHED);
    }

    @Test
    public void PickGoodToothAndGoToNextTurn() {
        game.performAction(player1, "START_GAME");
        game.getPosition().put("CurrentTurn", player2.id());

        assertThat(getTeethList(game)).filteredOn(Tooth::available).hasSize(10);
        Tooth goodTooth = getTeethList(game).stream().filter(Tooth::available).filter(t -> !game.isBadTooth(t)).findFirst().orElseThrow();
        Response<?> response = game.performAction(player2, "PICK_TOOTH", Map.of("tooth_number", goodTooth.number()));

        assertThat(response).isInstanceOf(JSONResponse.class);
        assertThat(game.getState()).isEqualTo(IN_PROGRESS);
        assertThat(game.getPosition().get("CurrentTurn")).isEqualTo(player3.id());
        assertThat(getTeethList(game)).filteredOn(Tooth::available).hasSize(9);

        Tooth goodTooth2 = getTeethList(game).stream().filter(Tooth::available).filter(t -> !game.isBadTooth(t)).findFirst().orElseThrow();
        Response<?> response2 = game.performAction(player3, "PICK_TOOTH", Map.of("tooth_number", goodTooth2.number()));

        assertThat(goodTooth).isNotEqualTo(goodTooth2);
        assertThat(response2).isInstanceOf(JSONResponse.class);
        assertThat(game.getState()).isEqualTo(IN_PROGRESS);
        assertThat(game.getPosition().get("CurrentTurn")).isEqualTo(player1.id());
        assertThat(getTeethList(game)).filteredOn(Tooth::available).hasSize(8);
    }




    @SuppressWarnings("unchecked")
    private static List<Tooth> getTeethList(CrocodileGame game) {
        return ((List<Tooth>) game.getPosition().get("TeethList"));
    }
}
