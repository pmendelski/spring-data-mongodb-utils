package net.exacode.spring.data.mongodb.utils.file.simple;

import net.exacode.spring.data.mongodb.utils.file.image.StoredImageDescriptor;
import net.exacode.spring.data.mongodb.utils.file.image.StoredImageDescriptor.Conversions;
import net.exacode.spring.data.mongodb.utils.file.image.StoredImageDescriptor_;
import net.exacode.spring.data.mongodb.utils.file.simple.MongoFileDescriptor.Builder;

import org.joda.time.DateTime;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoFileDescriptorConverter {
	private DBObject convert(MongoFileDescriptor descriptor) {
		BasicDBObject obj = new BasicDBObject();
		obj.append(MongoFileDescriptor_.previousFileId,
				descriptor.getPreviousFileId());
		obj.append(MongoFileDescriptor_.size, descriptor.getSize());
		obj.append(MongoFileDescriptor_.creationTimestamp,
				descriptor.getCreationTimestamp());
		obj.append(MongoFileDescriptor_.type, descriptor.getType());
		obj.append(MongoFileDescriptor_.userId, descriptor.getUserId());
		return obj;
	}

	private MongoFileDescriptor convert(DBObject dbObject) {
		Builder builder = new MongoFileDescriptor.Builder();
		builder.previousFileId(
				dbObject.get(MongoFileDescriptor_.previousFileId).toString())
				.size(Long.parseLong(dbObject.get(MongoFileDescriptor_.size)
						.toString()))
				.creationTimestamp(
						dbObject.get(MongoFileDescriptor_.creationTimestamp)
								.toString());

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
