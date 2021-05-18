package nl.adrianmensing.krokodil.database.manager;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBService;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBTables;
import nl.adrianmensing.krokodil.logic.Player;
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
        List<Map<String, String>> players = item.getList("Players");
        GameState state = GameState.valueOf(item.getString("State"));
        GameSettings<GameType.CrocodileGameType> settings = new CrocodileSettings(item.getMap("Settings"));

        game.setId(gameID);
        game.setHost(PlayerDataManager.getPlayerByID(hostID));

        for (Map<String, String> player : players) {
            game.addPlayer(new Player(player.get("id"), player.get("name"), gameID));
        }

        game.setState(state);
        game.setSettings(settings);

        return game;
    }

    public static void saveGame(@NotNull Game<?> game) {
        List<Map<String, String>> players = game.getPlayers().stream().map(player -> {
            Map<String, String> m = new HashMap<>();
            m.put("id", player.id());
            m.put("name", player.username());
            return m;
        }).toList();

        Item item = new Item()
                .withPrimaryKey("GameID", game.getId())
                .withString("Host", game.getHost().id())
                .withList("Players", players)
                .withString("State", game.getState().name())
                .withMap("Settings", game.getSettings().getSettings())
                .withString("LastUpdated", ZonedDateTime.now().toString());

        DynamoDBService.getDynamoDB().getTable(DynamoDBTables.GAMES).putItem(item);
    }

}
