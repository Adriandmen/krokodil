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

public class DynamoDBService implements DatabaseService {
    private static final Regions REGION = Regions.EU_CENTRAL_1;
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

    public static void createGameSettingsTable() {
        String tableName = "GameSettings";

        try {
            System.out.printf("Attempting to create table '%s'%n", tableName);
            CreateTableRequest request = new CreateTableRequest();
            request.withTableName(tableName)
                    .withKeySchema(Collections.singletonList(new KeySchemaElement("GameID", KeyType.HASH)))
                    .withAttributeDefinitions(Collections.singletonList(new AttributeDefinition("GameID", ScalarAttributeType.N)))
                    .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

            Table table = getDynamoDB().createTable(request);
            table.waitForActive();
            System.out.println("Successfully created table " + tableName);
        } catch (Exception e) {
            System.err.println("Unable to create table");
            e.printStackTrace();
        }
    }
}
