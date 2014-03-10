package net.exacode.spring.data.mongodb.utils.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import com.mongodb.DBCollection;

/**
 * Loads data to MongoDb collections.
 * 
 * @author mendlik
 * 
 */
@Service
public class MongoDbDataLoader {

	private final MongoOperations mongo;

	@Autowired
	public MongoDbDataLoader(MongoOperations mongo) {
		this.mongo = mongo;
	}

	public CollectionLoader loadTo(DBCollection collection) {
		return new CollectionLoader(collection);
	}

	public CollectionLoader loadTo(String collectionName) {
		return new CollectionLoader(mongo.getCollection(collectionName));
	}

	public CollectionLoader loadTo(Class<?> entityClass) {
		return loadTo(mongo.getCollectionName(entityClass));
	}

}
