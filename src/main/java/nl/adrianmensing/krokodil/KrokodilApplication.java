package nl.adrianmensing.krokodil;

import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KrokodilApplication {

	public static void main(String[] args) throws Exception {
        DynamoDBService.setupTables();

		SpringApplication.run(KrokodilApplication.class, args);
	}
}
