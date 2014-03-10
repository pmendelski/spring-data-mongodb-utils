package net.exacode.spring.data.mongodb.utils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;

/**
 * Base class for a MongoDB repository.
 * 
 * @author mendlik
 * 
 * @param <T>
 * @param <ID>
 */
public class BaseMongoRepository<T, ID extends Serializable> {

	protected static final String ID_ATTR = "_id";

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected TypedMongoOperations<T, ID> mongo;

	protected MongoOperations mongoOp;

	private Class<T> entityClass;

	@SuppressWarnings("unchecked")
	private BaseMongoRepository() {
		super();
		Class<?> cl = getClass();
		while (!BaseMongoRepository.class.getSimpleName().equals(
				cl.getSuperclass().getSimpleName())) {
			if (cl.getGenericSuperclass() instanceof ParameterizedType) {
				break;
			}
			cl = cl.getSuperclass();
		}
		if (cl.getGenericSuperclass() instanceof ParameterizedType) {
			entityClass = (Class<T>) ((ParameterizedType) cl
					.getGenericSuperclass()).getActualTypeArguments()[0];
		}
	}

	public BaseMongoRepository(MongoOperations mongoOperations) {
		this();
		this.mongoOp = mongoOperations;
		this.mongo = new TypedMongoOperations<T, ID>(mongoOperations,
				entityClass);
	}

	public void setMongoOperations(MongoOperations mongoOperations) {
		this.mongoOp = mongoOperations;
		this.mongo = new TypedMongoOperations<T, ID>(mongoOperations,
				entityClass);
	}

	public T findOne(ID id) {
		return mongo.findById(id);
	}

	public List<T> findAll() {
		return findAll(new Query());
	}

	public Iterable<T> findAll(Iterable<ID> ids) {
		Set<ID> parameters = new HashSet<ID>();
		for (ID id : ids) {
			parameters.add(id);
		}
		return findAll(new Query(new Criteria(ID_ATTR).in(parameters)));
	}

	public Page<T> findAll(final Pageable pageable) {
		Long count = count();
		List<T> list = findAll(new Query().with(pageable));
		return new PageImpl<T>(list, pageable, count);
	}

	public List<T> findAll(Sort sort) {
		return findAll(new Query().with(sort));
	}

	public boolean exists(ID id) {
		return mongo.findOne(getQueryById(id)) != null;
	}

	public long count() {
		return mongo.count(new Query());
	}

	protected Query query(Criteria criteria) {
		return Query.query(criteria);
	}

	protected Criteria where(String key) {
		return Criteria.where(key);
	}

	protected Query getQueryById(ID id) {
		return new Query(new Criteria(ID_ATTR).in(id));
	}

	protected List<T> findAll(Query query) {
		if (query == null) {
			return Collections.emptyList();
		}
		return mongo.find(query);
	}

	protected Page<T> findPage(Query query, Pageable pageable) {
		long count = mongo.count(query);
		query.with(pageable);
		List<T> items = mongo.find(query);
		return new PageImpl<T>(items, pageable, count);
	}

	protected Page<T> findPage(Query query, Pageable pageable, Sort sort) {
		long count = mongo.count(query);
		query.with(pageable);
		query.with(sort);
		List<T> items = mongo.find(query);
		return new PageImpl<T>(items, pageable, count);
	}

	public T findOne(Query query) {
		return mongo.findOne(query);
	}

	public T findOne(Query query, String collectionName) {
		return mongo.findOne(query, collectionName);
	}

	public List<T> find(Query query) {
		return mongo.find(query);
	}

	public T findById(ID id) {
		return mongo.findById(id);
	}

	public T findAndModify(Query query, Update update) {
		return mongo.findAndModify(query, update);
	}

	public T findAndModify(Query query, Update update,
			FindAndModifyOptions options) {
		return mongo.findAndModify(query, update, options);
	}

	public T findAndModify(Query query, Update update,
			FindAndModifyOptions options, String collectionName) {
		return mongo.findAndModify(query, update, options, collectionName);
	}

	public T findAndRemove(Query query) {
		return mongo.findAndRemove(query);
	}

	public long count(Query query) {
		return mongo.count(query);
	}

	public void insert(T objectToSave) {
		mongo.insert(objectToSave);
	}

	public void insert(Collection<T> batchToSave) {
		mongo.insert(batchToSave);
	}

	public void insertAll(Collection<T> objectsToSave) {
		mongo.insertAll(objectsToSave);
	}

	public void save(T objectToSave) {
		mongo.save(objectToSave);
	}

	public WriteResult upsert(Query query, Update update) {
		return mongo.upsert(query, update);
	}

	public WriteResult updateFirst(Query query, Update update) {
		return mongo.updateFirst(query, update);
	}

	public WriteResult updateMulti(Query query, Update update) {
		return mongo.updateMulti(query, update);
	}

	public void remove(T object) {
		mongo.remove(object);
	}

	public void remove(Query query) {
		mongo.remove(query);
	}

	public MapReduceResults<T> mapReduce(String inputCollectionName,
			String mapFunction, String reduceFunction) {
		return mongo
				.mapReduce(inputCollectionName, mapFunction, reduceFunction);
	}

	public MapReduceResults<T> mapReduce(String inputCollectionName,
			String mapFunction, String reduceFunction,
			MapReduceOptions mapReduceOptions) {
		return mongo.mapReduce(inputCollectionName, mapFunction,
				reduceFunction, mapReduceOptions);
	}

	public MapReduceResults<T> mapReduce(Query query,
			String inputCollectionName, String mapFunction,
			String reduceFunction) {
		return mongo.mapReduce(query, inputCollectionName, mapFunction,
				reduceFunction);
	}

	public MapReduceResults<T> mapReduce(Query query,
			String inputCollectionName, String mapFunction,
			String reduceFunction, MapReduceOptions mapReduceOptions) {
		return mongo.mapReduce(query, inputCollectionName, mapFunction,
				reduceFunction, mapReduceOptions);
	}

	public GroupByResults<T> group(String inputCollectionName, GroupBy groupBy) {
		return mongo.group(inputCollectionName, groupBy);
	}

	public GroupByResults<T> group(Criteria criteria,
			String inputCollectionName, GroupBy groupBy) {
		return mongo.group(criteria, inputCollectionName, groupBy);
	}

}
