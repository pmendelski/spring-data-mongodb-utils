package net.exacode.spring.data.mongodb.utils.file;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

@Repository
public class MongoFileRepository {

	Logger log = LoggerFactory.getLogger(getClass());

	private final GridFsOperations gridFs;

	private final MongoOperations mongo;

	private final MongoConverter mongoConverter;

	@Autowired
	public MongoFileRepository(GridFsOperations gridFs, MongoOperations mongo,
			MongoConverter mongoConverter) {
		this.gridFs = gridFs;
		this.mongo = mongo;
		this.mongoConverter = mongoConverter;
	}

	public MongoFile<DBObject> findByFilename(String filename) {
		return findByFilename(filename, DBObject.class);
	}

	public <T> MongoFile<T> findByFilename(String filename,
			Class<T> metadataType) {
		return findBy(MongoFile__.filename, filename, metadataType);
	}

	@SuppressWarnings({ "unchecked" })
	public String[] listFilenames(String locationPattern,
			Criteria... additionalCriteria) {
		if (!StringUtils.hasText(locationPattern)) {
			return new String[0];
		}
		AntPath path = new AntPath(locationPattern);

		Criteria criteria = null;
		if (path.isPattern()) {
			criteria = where(MongoFile__.filename).regex(path.toRegex());
		} else {
			criteria = where(MongoFile__.filename).is(locationPattern);
		}
		if (additionalCriteria != null && additionalCriteria.length != 0) {
			criteria = criteria.andOperator(additionalCriteria);
		}
		Query query = query(criteria);
		List<String> filenames = mongo.getCollection("fs.files").distinct(
				"filename", query.getQueryObject());
		return filenames.toArray(new String[0]);
	}

	public void insert(MongoFile<?> file) {
		Object metadata = file.getMetadata();
		DBObject metadataDbObject = (DBObject) mongoConverter
				.convertToMongoType(metadata);
		gridFs.store(file.getContent(), file.getFilename(),
				file.getContentType(), metadataDbObject);
	}

	protected void delete(Query query) {
		gridFs.delete(query);
	}

	protected <T> MongoFile<T> findBy(String property, String value,
			Class<T> metadataType) {
		Query query = query(where(property).is(value)).with(
				new Sort(Direction.ASC, MongoFile__.uploadDate));
		log.info("Query: {}", query.getQueryObject());
		GridFSDBFile gridFsFile = gridFs.findOne(query);
		return createMongoFile(gridFsFile, metadataType);
	}

	@SuppressWarnings("unchecked")
	protected <T> MongoFile<T> createMongoFile(GridFSDBFile gridFsFile,
			Class<T> metadataType) {
		if (gridFsFile == null) {
			return null;
		}
		T metadata = null;
		if (DBObject.class.equals(metadataType)) {
			metadata = (T) gridFsFile.getMetaData();
		} else {
			metadata = mongoConverter.read(metadataType,
					gridFsFile.getMetaData());
		}
		return MongoFile.<T> builder().content(gridFsFile.getInputStream())
				.id(gridFsFile.getId())
				.contentType(gridFsFile.getContentType())
				.uploadDate(gridFsFile.getUploadDate())
				.filename(gridFsFile.getFilename()).metadata(metadata)
				.md5(gridFsFile.getMD5()).build();
	}
}
