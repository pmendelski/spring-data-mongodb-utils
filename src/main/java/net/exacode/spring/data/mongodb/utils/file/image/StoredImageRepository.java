package net.exacode.spring.data.mongodb.utils.file.image;

import net.exacode.spring.data.mongodb.utils.file.image.StoredImageDescriptor.Conversions;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

public class StoredImageRepository {

	private final GridFsOperations gridFs;

	public StoredImageRepository(GridFsOperations gridFs) {
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

	private DBObject convert(StoredImageDescriptor parameters) {
		BasicDBObject obj = new BasicDBObject();
		obj.append(StoredImageDescriptor_.conversion, parameters
				.getConversion().toString().toLowerCase());
		obj.append(StoredImageDescriptor_.type, parameters.getType().toString()
				.toLowerCase());
		obj.append(StoredImageDescriptor_.creationTimestamp,
				parameters.getCreationTimestamp());
		obj.append(StoredImageDescriptor_.width, parameters.getWidth());
		obj.append(StoredImageDescriptor_.height, parameters.getHeight());
		obj.append(StoredImageDescriptor_.userId, parameters.getUserId());
		obj.append(StoredImageDescriptor_.sourceImageId,
				parameters.getSourceImageId());
		return obj;
	}

	private StoredImage convert(GridFSDBFile gridFsFile) {
		StoredImage image = new StoredImage();
		image.setInputStream(gridFsFile.getInputStream());
		image.setId(gridFsFile.getId().toString());
		StoredImageDescriptor descriptor = new StoredImageDescriptor();
		DBObject meta = gridFsFile.getMetaData();
		descriptor.setConversion(Conversions.valueOf(meta
				.get(StoredImageDescriptor_.conversion).toString()
				.toUpperCase()));
		descriptor.setType(StoredImageDescriptor.Types.valueOf(meta
				.get(StoredImageDescriptor_.type).toString().toUpperCase()));
		descriptor.setHeight(Integer.parseInt(meta.get(
				StoredImageDescriptor_.height).toString()));
		descriptor.setWidth(Integer.parseInt(meta.get(
				StoredImageDescriptor_.width).toString()));
		descriptor.setSize(gridFsFile.getLength());
		descriptor.setCreationTimestamp(DateTime.parse(meta.get(
				StoredImageDescriptor_.creationTimestamp).toString()));
		if (meta.containsField(StoredImageDescriptor_.userId)
				&& meta.get(StoredImageDescriptor_.userId) != null) {
			descriptor.setUserId(meta.get(StoredImageDescriptor_.userId)
					.toString());
		}
		if (meta.containsField(StoredImageDescriptor_.sourceImageId)
				&& meta.get(StoredImageDescriptor_.sourceImageId) != null) {
			descriptor.setSourceImageId(meta.get(
					StoredImageDescriptor_.sourceImageId).toString());
		}

		image.setDescriptor(descriptor);
		return image;
	}
}
