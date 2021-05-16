package nl.adrianmensing.krokodil.database.manager;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBService;
import nl.adrianmensing.krokodil.logic.game.Game;
import nl.adrianmensing.krokodil.logic.game.GameType;
import nl.adrianmensing.krokodil.logic.game.settings.GameSettings;
import org.jetbrains.annotations.NotNull;

import static nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBTables.GAME_SETTINGS;

public class GameSettingsDataManager implements DataManager<GameSettings<?>> {

    public static <T extends GameType> void insertGameSettingsByGame(@NotNull Game<T> game) {
        GameSettings<T> settings = game.getSettings();

        if (settings == null)
            throw new NullPointerException("Game settings does not exist");

        Table table = DynamoDBService.getDynamoDB().getTable(GAME_SETTINGS);

        Item item = new Item()
                .withPrimaryKey("GameID", game.getId())
                .withMap("Settings", settings.getSettings());

        table.putItem(item);
    }

}
