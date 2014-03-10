package net.exacode.spring.data.mongodb.utils.file.simple;

import net.exacode.spring.data.mongodb.utils.file.image.StoredImage;
import net.exacode.spring.data.mongodb.utils.file.image.StoredImageDescriptor_;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

@Repository
public class MongoFileRepository {

	private final GridFsOperations gridFs;

	@Autowired
	public MongoFileRepository(GridFsOperations gridFs) {
		this.gridFs = gridFs;
	}

	public StoredImage findImage(String id) {
		Query query = Query.query(Criteria.where("filename").is(id));
		GridFSDBFile gridFsFile = gridFs.findOne(query);
		if (gridFsFile == null) {
			return null;
		} else {
			return convert(gridFsFile);
		}
	}

	public void saveImage(StoredImage image) {
		String contentType = image.getDescriptor().getType()
				.getHttpContentType();
		DBObject parameters = convert(image.getDescriptor());
		gridFs.store(image.getInputStream(), image.getId(), contentType,
				parameters);
	}

	public void deleteImageBySourceId(String id) {
		Query query = Query.query(Criteria
				.where("meta." + StoredImageDescriptor_.sourceImageId).is(id)
				.orOperator(Criteria.where("id").is(id)));
		gridFs.delete(query);
	}
}
