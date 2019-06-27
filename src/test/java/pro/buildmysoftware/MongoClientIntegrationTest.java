package pro.buildmysoftware;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Projections.*;
import static org.assertj.core.api.Assertions.assertThat;

public class MongoClientIntegrationTest {

	private static final String DATABASE = "no-spring-db";
	private static final String DOCUMENTS_COLLECTION = "documents";
	private static final String POJO_COLLECTION = "pojo";
	// @formatter:off
	private static final String CONNECTION_STRING =
		"mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=replica-set-0";
	// @formatter:on
	private MongoClient mongoClient;

	private MongoClientSettings settings() {
		return MongoClientSettings.builder()
			.applyConnectionString(new ConnectionString(CONNECTION_STRING))
			.codecRegistry(CodecRegistries
				.fromRegistries(com.mongodb.MongoClient
					.getDefaultCodecRegistry(),
					CodecRegistries
					.fromProviders(PojoCodecProvider
						.builder().automatic(true)
						.build()))).build();
	}

	@BeforeEach
	void initMongoClient() {
		mongoClient = MongoClients.create(settings());
	}

	@AfterEach
	void closeMongoClient() {
		mongoClient.close();
	}

	@DisplayName("query view example")
	@Test
	@Disabled
	void queryView() throws Exception {
		MongoCollection<Document> totalScoreMsg = mongoClient
			.getDatabase(DATABASE).getCollection("totalScoreMsg");

		totalScoreMsg.find()
			.forEach((Consumer<Document>) document -> System.out
				.println(document.toJson()));
	}

	@DisplayName("aggregate example")
	@Test
	void aggregate() throws Exception {
		// given
		MongoCollection<Document> collection = mongoClient
			.getDatabase(DATABASE).getCollection("scoreMsg");
		collection.insertMany(List.of(new Document("msg", "hello")
			.append("score", 3), new Document("msg", "hello")
			.append("score", 2), new Document("msg", "hi")
			.append("score", 1), new Document("msg", "hello")
			.append("score", 1)));

		// when
		AggregateIterable<Document> aggregate = collection
			.aggregate(List.of(
				// @formatter:off
					match(gt("score", 1)),
					group("$msg", sum("totalScore",
						"$score")),
					project(fields(computed("message",
						"$_id"), include("totalScore")))
					// @formatter:on
			));

		// then
		aggregate.forEach((Consumer<Document>) document -> System.out
			.println(document.toJson()));
	}

	@DisplayName("receive change streams update")
	@Disabled
	@Test
	void changeStreams() throws Exception {
		// given
		MongoCollection<Document> collection = mongoClient
			.getDatabase(DATABASE)
			.getCollection(DOCUMENTS_COLLECTION);

		// when
		collection.watch().iterator()
			.forEachRemaining(System.out::println);
	}

	@DisplayName("add any document example")
	@Test
	void addDocument() throws Exception {
		// given
		MongoCollection<Document> anyCollection = mongoClient
			.getDatabase(DATABASE)
			.getCollection(DOCUMENTS_COLLECTION);
		Document anyDocument = new Document("msg", "hello");

		// when
		anyCollection.insertOne(anyDocument);

		// then
		Document documentFromDb = anyCollection.find(eq("msg",
			"hello"))
			.first();
		assertThat(documentFromDb).isNotNull();
		assertThat(documentFromDb.getString("msg")).isEqualTo("hello");
	}

	@DisplayName("use read concern example")
	@Test
	@Disabled
	void readConcern() throws Exception {
		// given
		MongoCollection<Document> collection = mongoClient
			.getDatabase(DATABASE)
			.getCollection(DOCUMENTS_COLLECTION)
			.withReadConcern(ReadConcern.MAJORITY);

		// when
		Bson filter = eq("_id", new ObjectId("5d127c41425932059d6ab293"
		));
		FindIterable<Document> documents = collection.find(filter);

		// then
		Document first = documents.first();
		System.out.println(first.toJson());
		assertThat(first).isNotNull();
	}

	@DisplayName("insert document using write concern example")
	@Test
	void writeConcern() throws Exception {
		// given
		MongoCollection<Document> collection = mongoClient
			.getDatabase(DATABASE)
			.getCollection(DOCUMENTS_COLLECTION)
			.withWriteConcern(WriteConcern.W1);

		// when
		collection.insertOne(new Document("msg", "hello from Java"));

		// then
		Document helloFromJavaFromDb = collection
			.find(eq("msg", "hello from Java")).first();
		assertThat(helloFromJavaFromDb).isNotNull();
		assertThat(helloFromJavaFromDb.getString("msg"))
			.isEqualTo("hello from Java");
	}

	@DisplayName("insert pojo example")
	@Test
	void pojo() throws Exception {
		// given
		MongoCollection<Pojo> collection = mongoClient
			.getDatabase(DATABASE)
			.getCollection(POJO_COLLECTION, Pojo.class);
		Pojo pojo = new Pojo(ObjectId
			.get(), "string 0", 1, new Pojo(ObjectId
			.get(), "string 1", 2, null));

		// when
		collection.insertOne(pojo);

		// then
		Bson filter = eq("_id", pojo.getId());
		Pojo pojoFromDb = collection.find(filter).first();
		assertThat(pojoFromDb).isEqualTo(pojo);
	}
}
