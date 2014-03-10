package net.exacode.spring.data.mongodb.utils.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.exacode.spring.data.mongodb.utils.IntegrationTestBase;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.util.JSONParseException;

public class MongoDbDataLoaderTest extends IntegrationTestBase {

	private static final String USERS_COLLECTION = "users";

	private static final String USERS_PER_LINE = "users-per-line.txt";
	private static final String USERS_PER_LINE_INCOMPLETE_OBJECT = "users-per-line-incomplete-object.txt";

	private static final String USERS_JSON = "users.json";
	private static final String USERS_JSON_WRONG_FIELD_NAME = "users-wrong-field-name.json";
	private static final String USERS_JSON_WRONG_FIELD_TYPE = "users-wrong-field-type.json";

	@Autowired
	private MongoDbDataLoader dataLoader;

	private DBCollection collection;

	@Override
	@Before
	public void before() {
		super.before();
		this.collection = mongo.getCollection(USERS_COLLECTION);
	}

	private String loadJsonFromFile(String dataFileName) {
		try {
			Resource resource = getDataResource(dataFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					resource.getInputStream()));
			return FileCopyUtils.copyToString(br);
		} catch (IOException e) {
			logger.error("Couldn't load file: {}", dataFileName, e);
			throw new AssertionError(e);
		}
	}

	private Resource getDataResource(String dataFileName) {
		return new ClassPathResource("data/" + dataFileName);
	}

	@Test
	public void shouldLoadUsersFromJsonFile() {
		// given
		String json = loadJsonFromFile(USERS_JSON);

		// when
		dataLoader.loadTo(collection).loadfrom(json);

		// then
		assertThat(collection.count()).isGreaterThan(0);
		assertThat(
				collection.findOne(new BasicDBObject("_id", "sherlock.holmes")))
				.isNotNull();
	}

	@Test
	public void shouldLoadUsersLineByLine() throws IOException {
		// when
		dataLoader.loadTo(collection).loadLineByLineFrom(
				getDataResource(USERS_PER_LINE));

		// then
		assertThat(collection.count()).isGreaterThan(0);
		assertThat(
				collection.findOne(new BasicDBObject("_id", "sherlock.holmes")))
				.isNotNull();
	}

	@Test(expected = JSONParseException.class)
	public void shouldThrowExcpetionOnBrokenJsonInLineByLineFile()
			throws IOException {
		dataLoader.loadTo(collection).loadLineByLineFrom(
				getDataResource(USERS_PER_LINE_INCOMPLETE_OBJECT));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExcpetionOnNoCollectionField() throws IOException {
		// given
		String json = loadJsonFromFile(USERS_JSON_WRONG_FIELD_NAME);

		// when
		dataLoader.loadTo(collection).loadfrom(json);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExcpetionOnWrongCollectionType() throws IOException {
		// given
		String json = loadJsonFromFile(USERS_JSON_WRONG_FIELD_TYPE);

		// when
		dataLoader.loadTo(collection).loadfrom(json);
	}
}
