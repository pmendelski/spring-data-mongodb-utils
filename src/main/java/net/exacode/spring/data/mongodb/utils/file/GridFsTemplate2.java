package net.exacode.spring.data.mongodb.utils.file;

import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereFilename;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.QueryMapper;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * {@link GridFsOperations} implementation to store content into MongoDB GridFS.
 * 
 * @author Oliver Gierke
 * @author Philipp Schneider
 * @author Thomas Darimont
 */
public class GridFsTemplate2 implements GridFsOperations,
		ResourcePatternResolver {

	private final MongoDbFactory dbFactory;
	private final String bucket;
	private final MongoConverter converter;
	private final QueryMapper queryMapper;

	/**
	 * Creates a new {@link GridFsTemplate} using the given
	 * {@link MongoDbFactory} and {@link MongoConverter}.
	 * 
	 * @param dbFactory
	 *            must not be {@literal null}.
	 * @param converter
	 *            must not be {@literal null}.
	 */
	public GridFsTemplate2(MongoDbFactory dbFactory, MongoConverter converter) {
		this(dbFactory, converter, null);
	}

	/**
	 * Creates a new {@link GridFsTemplate} using the given
	 * {@link MongoDbFactory} and {@link MongoConverter}.
	 * 
	 * @param dbFactory
	 *            must not be {@literal null}.
	 * @param converter
	 *            must not be {@literal null}.
	 * @param bucket
	 */
	public GridFsTemplate2(MongoDbFactory dbFactory, MongoConverter converter,
			String bucket) {

		Assert.notNull(dbFactory);
		Assert.notNull(converter);

		this.dbFactory = dbFactory;
		this.converter = converter;
		this.bucket = bucket;

		this.queryMapper = new QueryMapper(converter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.mongodb.gridfs.GridFsOperations#store(java.io
	 * .InputStream, java.lang.String)
	 */
	@Override
	public GridFSFile store(InputStream content, String filename) {
		return store(content, filename, (Object) null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.mongodb.gridfs.GridFsOperations#store(java.io
	 * .InputStream, java.lang.String, java.lang.String)
	 */
	@Override
	public GridFSFile store(InputStream content, String filename,
			String contentType) {
		return store(content, filename, contentType, (Object) null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.mongodb.gridfs.GridFsOperations#store(java.io
	 * .InputStream, java.lang.String, java.lang.Object)
	 */
	@Override
	public GridFSFile store(InputStream content, String filename,
			Object metadata) {

		return store(content, filename, null, metadata);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.mongodb.gridfs.GridFsOperations#store(java.io
	 * .InputStream, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public GridFSFile store(InputStream content, String filename,
			String contentType, Object metadata) {

		DBObject dbObject = null;

		if (metadata != null) {
			dbObject = new BasicDBObject();
			converter.write(metadata, dbObject);
		}

		return store(content, filename, contentType, dbObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.mongodb.gridfs.GridFsOperations#store(java.io
	 * .InputStream, java.lang.String, com.mongodb.DBObject)
	 */
	@Override
	public GridFSFile store(InputStream content, String filename,
			DBObject metadata) {
		return this.store(content, filename, null, metadata);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.mongodb.gridfs.GridFsOperations#store(java.io
	 * .InputStream, java.lang.String, com.mongodb.DBObject)
	 */
	@Override
	public GridFSFile store(InputStream content, String filename,
			String contentType, DBObject metadata) {

		Assert.notNull(content);
		Assert.hasText(filename);

		GridFSInputFile file = getGridFs().createFile(content);
		file.setFilename(filename);

		if (metadata != null) {
			file.setMetaData(metadata);
		}

		if (contentType != null) {
			file.setContentType(contentType);
		}

		file.save();
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.mongodb.gridfs.GridFsOperations#find(com.mongodb
	 * .DBObject)
	 */
	@Override
	public List<GridFSDBFile> find(Query query) {

		if (query == null) {
			return getGridFs().find((DBObject) null);
		}

		DBObject queryObject = getMappedQuery(query.getQueryObject());
		DBObject sortObject = getMappedQuery(query.getSortObject());

		return getGridFs().find(queryObject, sortObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.mongodb.gridfs.GridFsOperations#findOne(com.
	 * mongodb.DBObject)
	 */
	@Override
	public GridFSDBFile findOne(Query query) {
		return getGridFs().findOne(getMappedQuery(query));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.mongodb.gridfs.GridFsOperations#delete(org.
	 * springframework.data.mongodb.core.query.Query)
	 */
	@Override
	public void delete(Query query) {
		getGridFs().remove(getMappedQuery(query));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.core.io.ResourceLoader#getClassLoader()
	 */
	@Override
	public ClassLoader getClassLoader() {
		return dbFactory.getClass().getClassLoader();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.core.io.ResourceLoader#getResource(java.lang.String)
	 */
	@Override
	public GridFsResource getResource(String location) {

		GridFSDBFile file = findOne(query(whereFilename().is(location)));
		return file != null ? new GridFsResource(file) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.core.io.support.ResourcePatternResolver#getResources
	 * (java.lang.String)
	 */
	@Override
	public GridFsResource[] getResources(String locationPattern) {

		if (!StringUtils.hasText(locationPattern)) {
			return new GridFsResource[0];
		}

		AntPath path = new AntPath(locationPattern);

		if (path.isPattern()) {

			List<GridFSDBFile> files = find(query(whereFilename().regex(
					path.toRegex())));
			List<GridFsResource> resources = new ArrayList<GridFsResource>(
					files.size());

			for (GridFSDBFile file : files) {
				resources.add(new GridFsResource(file));
			}

			return resources.toArray(new GridFsResource[resources.size()]);
		}

		return new GridFsResource[] { getResource(locationPattern) };
	}

	private DBObject getMappedQuery(Query query) {
		return query == null ? new Query().getQueryObject()
				: getMappedQuery(query.getSortObject());
	}

	private DBObject getMappedQuery(DBObject query) {
		return query == null ? null : queryMapper.getMappedObject(query, null);
	}

	private GridFS getGridFs() {
		DB db = dbFactory.getDb();
		return bucket == null ? new GridFS(db) : new GridFS(db, bucket);
	}
}
