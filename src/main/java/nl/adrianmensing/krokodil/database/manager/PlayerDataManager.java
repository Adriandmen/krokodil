package nl.adrianmensing.krokodil.database.manager;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBService;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBTables;
import nl.adrianmensing.krokodil.logic.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Optional;

// TODO: Implement some form of caching to reduce redundant database calls,
//       because this is unnecessarily expensive.
public final class PlayerDataManager implements DataManager<Player> {

    @Nullable
    public static Player getPlayerByID(String id) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("PlayerID", id);
        Item item = DynamoDBService.getDynamoDB().getTable(DynamoDBTables.PLAYERS).getItem(spec);

        if (item == null)
            return null;

        return new Player(item.getString("PlayerID"), item.getString("Username"), item.getString("CurrentGame"));
    }

    public static void savePlayer(@NotNull Player player) {
        Item item = new Item()
                .withPrimaryKey("PlayerID", player.id())
                .withString("Username", Optional.ofNullable(player.username()).orElse(""))
                .withString("CurrentGame", Optional.ofNullable(player.game()).orElse(""))
                .withString("LastUpdated", ZonedDateTime.now().toString());

        DynamoDBService.getDynamoDB().getTable(DynamoDBTables.PLAYERS).putItem(item);
    }
}
