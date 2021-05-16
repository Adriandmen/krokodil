package nl.adrianmensing.krokodil;

import nl.adrianmensing.krokodil.database.manager.GameSettingsDataManager;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBService;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBTables;
import nl.adrianmensing.krokodil.logic.game.Game;
import nl.adrianmensing.krokodil.logic.game.GameType;
import nl.adrianmensing.krokodil.logic.game.impl.crocodile.CrocodileGame;

//@SpringBootApplication
public class KrokodilApplication {

	public static void main(String[] args) {

	    if (!DynamoDBService.getExistingTableNames().contains(DynamoDBTables.GAME_SETTINGS))
	        DynamoDBService.createGameSettingsTable();
	    else
            System.out.println("Table already exists");

        Game<GameType.CrocodileGameType> game = new CrocodileGame(123);

        GameSettingsDataManager.insertGameSettingsByGame(game);

//		SpringApplication.run(KrokodilApplication.class, args);
	}
}
