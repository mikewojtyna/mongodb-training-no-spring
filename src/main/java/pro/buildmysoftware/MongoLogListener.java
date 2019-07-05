package pro.buildmysoftware;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;

import java.util.function.Consumer;

public class MongoLogListener {
	private final MongoCollection<Document> mongoCollection;

	public MongoLogListener(MongoClient mongoClient, String database,
				String collection) {
		mongoCollection = mongoClient.getDatabase(database)
			.getCollection(collection);
	}

	public void subscribe(Consumer<Document> consumer) {
		mongoCollection.watch()
			.forEach((Consumer<?
				super ChangeStreamDocument<Document>>) documentChangeStreamDocument -> consumer
				.accept(documentChangeStreamDocument
					.getFullDocument()));
	}
}
