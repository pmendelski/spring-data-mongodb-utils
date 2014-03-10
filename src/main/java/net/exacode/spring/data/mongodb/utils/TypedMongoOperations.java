package net.exacode.spring.data.mongodb.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.DbCallback;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.geo.GeoResults;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * Decorated {@link MongoOperations} so you don't need to pass the class type in
 * each method.
 * 
 * @author mendlik
 * 
 * @param <T>
 *            - type associated with a document
 * @param <I>
 *            - type of document's identifier
 */
public class TypedMongoOperations<T, I extends Serializable> {

	private final MongoOperations mongoOp;

	private final Class<T> entityClass;

	public TypedMongoOperations(MongoOperations mongoOp, Class<T> entityClass) {
		this.mongoOp = mongoOp;
		this.entityClass = entityClass;
	}

	public MongoConverter getConverter() {
		return mongoOp.getConverter();
	}

	public String getCollectionName() {
		return mongoOp.getCollectionName(entityClass);
	}

	public CommandResult executeCommand(String jsonCommand) {
		return mongoOp.executeCommand(jsonCommand);
	}

	public CommandResult executeCommand(DBObject command) {
		return mongoOp.executeCommand(command);
	}

	public CommandResult executeCommand(DBObject command, int options) {
		return mongoOp.executeCommand(command, options);
	}

	public void executeQuery(Query query, DocumentCallbackHandler dch) {
		mongoOp.executeQuery(query, getCollectionName(), dch);
	}

	public T execute(DbCallback<T> action) {
		return mongoOp.execute(action);
	}

	public T execute(CollectionCallback<T> callback) {
		return mongoOp.execute(entityClass, callback);
	}

	public T executeInSession(DbCallback<T> action) {
		return mongoOp.executeInSession(action);
	}

	public DBCollection createCollection() {
		return mongoOp.createCollection(entityClass);
	}

	public DBCollection createCollection(CollectionOptions collectionOptions) {
		return mongoOp.createCollection(entityClass, collectionOptions);
	}

	public DBCollection getCollection() {
		return mongoOp.getCollection(getCollectionName());
	}

	public boolean collectionExists() {
		return mongoOp.collectionExists(entityClass);
	}

	public void dropCollection() {
		mongoOp.dropCollection(entityClass);
	}

	public IndexOperations indexOps(String collectionName) {
		return mongoOp.indexOps(collectionName);
	}

	public IndexOperations indexOps() {
		return mongoOp.indexOps(entityClass);
	}

	public T findOne(Query query) {
		return mongoOp.findOne(query, entityClass);
	}

	public T findOne(Query query, String collectionName) {
		return mongoOp.findOne(query, entityClass, collectionName);
	}

	public List<T> find(Query query) {
		return mongoOp.find(query, entityClass);
	}

	public T findById(I id) {
		return mongoOp.findById(id, entityClass);
	}

	public GeoResults<T> geoNear(NearQuery near) {
		return mongoOp.geoNear(near, entityClass);
	}

	public T findAndModify(Query query, Update update) {
		return mongoOp.findAndModify(query, update, entityClass);
	}

	public T findAndModify(Query query, Update update,
			FindAndModifyOptions options) {
		return mongoOp.findAndModify(query, update, options, entityClass);
	}

	public T findAndModify(Query query, Update update,
			FindAndModifyOptions options, String collectionName) {
		return mongoOp.findAndModify(query, update, options, entityClass,
				collectionName);
	}

	public T findAndRemove(Query query) {
		return mongoOp.findAndRemove(query, entityClass);
	}

	public long count(Query query) {
		return mongoOp.count(query, entityClass);
	}

	public void insert(T objectToSave) {
		mongoOp.insert(objectToSave);
	}

	public void insert(Collection<T> batchToSave) {
		mongoOp.insert(batchToSave, entityClass);
	}

	public void insertAll(Collection<T> objectsToSave) {
		mongoOp.insertAll(objectsToSave);
	}

	public void save(T objectToSave) {
		mongoOp.save(objectToSave);
	}

	public WriteResult upsert(Query query, Update update) {
		return mongoOp.upsert(query, update, entityClass);
	}

	public WriteResult updateFirst(Query query, Update update) {
		return mongoOp.updateFirst(query, update, entityClass);
	}

	public WriteResult updateMulti(Query query, Update update) {
		return mongoOp.updateMulti(query, update, entityClass);
	}

	public void remove(T object) {
		mongoOp.remove(object);
	}

	public void remove(Query query) {
		mongoOp.remove(query, entityClass);
	}

	public List<T> findAll() {
		return mongoOp.findAll(entityClass);
	}

	public MapReduceResults<T> mapReduce(String inputCollectionName,
			String mapFunction, String reduceFunction) {
		return mongoOp.mapReduce(inputCollectionName, mapFunction,
				reduceFunction, entityClass);
	}

	public MapReduceResults<T> mapReduce(String inputCollectionName,
			String mapFunction, String reduceFunction,
			MapReduceOptions mapReduceOptions) {
		return mongoOp.mapReduce(inputCollectionName, mapFunction,
				reduceFunction, mapReduceOptions, entityClass);
	}

	public MapReduceResults<T> mapReduce(Query query,
			String inputCollectionName, String mapFunction,
			String reduceFunction) {
		return mongoOp.mapReduce(query, inputCollectionName, mapFunction,
				reduceFunction, entityClass);
	}

	public MapReduceResults<T> mapReduce(Query query,
			String inputCollectionName, String mapFunction,
			String reduceFunction, MapReduceOptions mapReduceOptions) {
		return mongoOp.mapReduce(query, inputCollectionName, mapFunction,
				reduceFunction, mapReduceOptions, entityClass);
	}

	public GroupByResults<T> group(String inputCollectionName, GroupBy groupBy) {
		return mongoOp.group(inputCollectionName, groupBy, entityClass);
	}

	public GroupByResults<T> group(Criteria criteria,
			String inputCollectionName, GroupBy groupBy) {
		return mongoOp.group(criteria, inputCollectionName, groupBy,
				entityClass);
	}

	public Set<String> getCollectionNames() {
		return mongoOp.getCollectionNames();
	}
}
