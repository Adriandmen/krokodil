package nl.adrianmensing.krokodil.database.manager;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBService;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBTables;
import nl.adrianmensing.krokodil.logic.game.Game;
import nl.adrianmensing.krokodil.logic.game.GameState;
import nl.adrianmensing.krokodil.logic.game.GameType;
import nl.adrianmensing.krokodil.logic.game.impl.crocodile.CrocodileGame;
import nl.adrianmensing.krokodil.logic.game.impl.crocodile.CrocodileSettings;
import nl.adrianmensing.krokodil.logic.game.settings.GameSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO: Implement some form of caching to reduce redundant database calls,
//       because this is unnecessarily expensive.
public class GameDataManager implements DataManager<Game<?>> {

    @Nullable
    public static Game<?> getGameByID(String id) {
        if (id == null)
            return null;

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("GameID", id);
        Item item = DynamoDBService.getDynamoDB().getTable(DynamoDBTables.GAMES).getItem(spec);

        if (item == null)
            return null;

        Game<?> game = new CrocodileGame();
        String gameID = item.getString("GameID");
        String hostID = item.getString("Host");
        List<String> players = item.getList("Players");
        GameState state = GameState.valueOf(item.getString("State"));
        Map<String, Object> position = item.getMap("Position");
        String salt = item.getString("Salt");
        GameSettings<GameType.CrocodileGameType> settings = new CrocodileSettings(item.getMap("Settings"));

        game.setId(gameID);
        game.setHost(PlayerDataManager.getPlayerByID(hostID));
        game.getPlayers().addAll(players);
        game.setPosition(position);
        game.setState(state);
        game.setSalt(salt);
        game.setSettings(settings);

        return game;
    }

    public static void saveGame(@NotNull Game<?> game) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String positionJson;
        try {
            positionJson = ow.writeValueAsString(Optional.ofNullable(game.getPosition()).orElse(new HashMap<>()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        Item item = new Item()
                .withPrimaryKey("GameID", game.getId())
                .withString("Host", game.getHost().id())
                .withList("Players", game.getPlayers())
                .withString("State", game.getState().name())
                .withMap("Settings", game.getSettings().getSettings())
                .withJSON("Position", positionJson)
                .withString("Salt", game.getSalt())
                .withString("LastUpdated", ZonedDateTime.now().toString());

        DynamoDBService.getDynamoDB().getTable(DynamoDBTables.GAMES).putItem(item);
    }

}
