package pro.buildmysoftware;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class MongoLogger {
	private final MongoCollection<Document> mongoCollection;

	public MongoLogger(MongoClient mongoClient, String database,
			   String collection) {
		mongoCollection = mongoClient.getDatabase(database)
			.getCollection(collection);
	}

	public void log(String timestamp, String msg, String level) {
		Document logDocument = new Document("timestamp", timestamp)
			.append("msg", msg).append("level", level);
		mongoCollection.insertOne(logDocument);
	}

	public FindIterable<Document> filter(String level) {
		return mongoCollection.find(Filters.eq("level", level));
	}
}
