package pro.buildmysoftware;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mongodb.client.model.Filters.eq;
import static org.assertj.core.api.Assertions.assertThat;

public class MongoClientIntegrationTest {

	private MongoClient mongoClient;

	private MongoClientSettings settings() {
		return MongoClientSettings.builder()
			.applyConnectionString(new ConnectionString("mongodb" + "://localhost:27017"))
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

	@DisplayName("add any document example")
	@Test
	void test0() throws Exception {
		// given
		MongoCollection<Document> anyCollection = mongoClient
			.getDatabase("mongodb-training-no-spring-db")
			.getCollection("anyCollection");
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
	void test1() throws Exception {
		// given
		MongoCollection<Document> collection = mongoClient
			.getDatabase("user").getCollection("user")
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

	@DisplayName("insert document example")
	@Test
	void test2() throws Exception {
		// given
		MongoCollection<Document> collection = mongoClient
			.getDatabase("mongodb-training-no-spring-db")
			.getCollection("anyCollection");

		// when
		collection.insertOne(new Document("msg", "hello from Java"));

		// then
		Document helloFromJavaFromDb = collection
			.find(eq("msg", "hello from Java")).first();
		assertThat(helloFromJavaFromDb).isNotNull();
		assertThat(helloFromJavaFromDb.getString("msg"))
			.isEqualTo("hello from Java");
	}

	@DisplayName("insert pojo")
	@Test
	void test3() throws Exception {
		// given
		MongoCollection<Pojo> collection = mongoClient
			.getDatabase("mongodb-training-no-spring-db")
			.getCollection("pojoCollection", Pojo.class);
		Pojo pojo = new Pojo(ObjectId
			.get(), "string 0", 1, new Pojo(ObjectId
			.get(), "string 1", 2, null));

		// when
		collection.insertOne(pojo);

		// then
		Bson filter = Filters.eq("_id", pojo.getId());
		Pojo pojoFromDb = collection.find(filter).first();
		assertThat(pojoFromDb).isEqualTo(pojo);
	}
}
