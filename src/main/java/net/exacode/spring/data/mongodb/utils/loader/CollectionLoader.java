package net.exacode.spring.data.mongodb.utils.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * Used by {@link MongoDbDataLoader}.
 * <p>
 * Loads documents to concrete collection.
 * 
 * @author mendlik
 */
class CollectionLoader {

	private static class Loader {
		private static final int MAX_BUFFER_SIZE = 100000;
		private final DBCollection collection;
		private int count;
		private final List<DBObject> nameTransformations = new ArrayList<DBObject>();

		public Loader(DBCollection collection) {
			this.collection = collection;
		}

		private void parseAndLoad(String jsonObject) {
			DBObject dbObject = (DBObject) JSON.parse(jsonObject);
			load(dbObject);
		}

		private void load(DBObject dbObject) {
			nameTransformations.add(dbObject);
			if (nameTransformations.size() >= MAX_BUFFER_SIZE) {
				flush();
			}
			count++;
		}

		private void flush() {
			collection.insert(nameTransformations);
			nameTransformations.clear();
		}

	}

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final DBCollection collection;

	public CollectionLoader(DBCollection collection) {
		this.collection = collection;
	}

	public void loadfrom(String json) {
		loadfrom(json, collection.getName());
	}

	public void loadfrom(String json, String fieldName) {
		DBObject dbObject = (DBObject) JSON.parse(json);
		// validate
		if (!dbObject.containsField(fieldName)) {
			throw new IllegalArgumentException(
					"Cannot load json to MongoDB. Wrong JSON structure. "
							+ "Need an object with an array. "
							+ "Expected field: " + fieldName);
		}
		// load
		logger.info("Loading name transformations from json from field: {}",
				fieldName);
		Object object = dbObject.get(fieldName);
		if (!(object instanceof BasicDBList)) {
			throw new IllegalArgumentException(
					"Cannot load json to MongoDB. Wrong JSON structure. "
							+ "Need an object with an array. "
							+ "Expected an array of objects.");
		}
		BasicDBList dbList = (BasicDBList) dbObject.get(fieldName);
		Loader loader = new Loader(collection);
		for (Object o : dbList) {
			loader.load((DBObject) o);
		}
		loader.flush();
		logger.info("Documents loaded. Count: {}", loader.count);
	}

	public void loadLineByLineFrom(Resource... resources) throws IOException {
		Loader loader = new Loader(collection);
		for (Resource resource : resources) {
			logger.info("Loading name transformations from: {}",
					resource.getFilename());
			loadLineByLineFrom(loader, resource.getInputStream());
			logger.info("Transformations loaded. Count: {}", loader.count);
		}
	}

	private void loadLineByLineFrom(Loader loader, InputStream is)
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line;
		while ((line = br.readLine()) != null) {
			loader.parseAndLoad(line);
		}
		loader.flush();
		br.close();
		logger.info("Documents loaded. Count: {}", loader.count);
	}

}
