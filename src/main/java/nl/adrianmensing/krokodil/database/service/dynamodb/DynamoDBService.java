package nl.adrianmensing.krokodil.database.service.dynamodb;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.*;
import nl.adrianmensing.krokodil.database.service.DatabaseService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBTables.*;

public class DynamoDBService implements DatabaseService {
    private static final Regions REGION = Regions.fromName(System.getenv("REGION"));
    private static DynamoDB dynamoDB = null;

    public static synchronized DynamoDB getDynamoDB() {
        if (dynamoDB == null) {
            AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(REGION).build();
            dynamoDB = new DynamoDB(client);
        }

        return dynamoDB;
    }

    @NotNull
    public static List<String> getExistingTableNames() {
        List<String> tableNames = new ArrayList<>();
        TableCollection<ListTablesResult> tables = getDynamoDB().listTables();

        for (Table table : tables) {
            tableNames.add(table.getTableName());
        }

        return tableNames;
    }

    private static void createPlayersTable() throws InterruptedException {
        String tableName = PLAYERS;

        // Schema:
        // [(pk) PlayerID, Username, LastUpdated]
        CreateTableRequest request = new CreateTableRequest();
        request.withTableName(tableName)
                .withKeySchema(Collections.singletonList(new KeySchemaElement("PlayerID", KeyType.HASH)))
                .withAttributeDefinitions(Collections.singletonList(new AttributeDefinition("PlayerID", ScalarAttributeType.S)))
                .withProvisionedThroughput(
                        new ProvisionedThroughput()
                                .withReadCapacityUnits(10L)
                                .withWriteCapacityUnits(10L));

        Table table = getDynamoDB().createTable(request);
        table.waitForActive();
        System.out.println("Successfully created table " + tableName);
    }

    private static void createGamesTable() throws InterruptedException {
        String tableName = GAMES;

        // Schema:
        // [(pk) GameID, Host, Players, State, Settings]
        CreateTableRequest request = new CreateTableRequest();
        request.withTableName(tableName)
                .withKeySchema(Collections.singletonList(new KeySchemaElement("GameID", KeyType.HASH)))
                .withAttributeDefinitions(Collections.singletonList(new AttributeDefinition("GameID", ScalarAttributeType.S)))
                .withProvisionedThroughput(
                        new ProvisionedThroughput()
                                .withReadCapacityUnits(10L)
                                .withWriteCapacityUnits(10L));

        Table table = getDynamoDB().createTable(request);
        table.waitForActive();
        System.out.println("Successfully created table " + tableName);
    }

    public static void setupTables() throws InterruptedException {
        List<String> tables = List.of(GAMES, PLAYERS);
        List<String> existingTableNames = getExistingTableNames();

        for (String tableName : tables) {
            if (!existingTableNames.contains(tableName)) {
                switch (tableName) {
                    case GAMES -> createGamesTable();
                    case PLAYERS -> createPlayersTable();
                    default -> throw new NoSuchElementException("Creator function of table '%s' does not exist".formatted(tableName));
                }
            }
        }

        System.out.println("Successfully created all tables");
    }
}
