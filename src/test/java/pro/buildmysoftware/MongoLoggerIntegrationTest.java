package pro.buildmysoftware;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoLoggerIntegrationTest {
	private static final String LOG_DATABASE = "log-db";
	private static final String LOG_COLLECTION = "logs";

	private MongoClient mongoClient() {
		return MongoClients.create("mongodb" + "://localhost:27017");
	}

	@DisplayName("should log and filter")
	@Test
	void logAndFilter() throws Exception {
		// given
		try (MongoClient mongoClient = mongoClient()) {
			MongoLogger mongoLogger = new MongoLogger(mongoClient,
				LOG_DATABASE, LOG_COLLECTION);

			// when
			mongoLogger.log(Instant.now()
				.toString(), "information", "INFO");

			// then
			Document filteredLog = mongoLogger.filter("INFO")
				.first();
			assertThat(filteredLog.getString("msg"))
				.isEqualTo("information");
		}
	}

	@DisplayName("should subscribe to log messages")
	@Test
	void subscribe() throws Exception {
		// given
		try (MongoClient mongoClient = mongoClient()) {
			MongoLogger mongoLogger = new MongoLogger(mongoClient,
				LOG_DATABASE, LOG_COLLECTION);
			MongoLogListener mongoLogListener =
				new MongoLogListener(mongoClient, LOG_DATABASE
					, LOG_COLLECTION);

			new Thread(() -> mongoLogListener
				.subscribe(document -> System.out
					.println(document.toJson()))).start();
			Thread.sleep(500);
			mongoLogger.log(Instant.now()
				.toString(), "information", "INFO");
			mongoLogger.log(Instant.now()
				.toString(), "information", "INFO");

			Thread.sleep(10000);
		}
	}
}
