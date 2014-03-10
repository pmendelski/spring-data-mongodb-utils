package net.exacode.spring.data.mongodb.utils.file.image;

import java.io.IOException;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class StoredImageDescriptor {

	public static enum Types {
		PNG, JPEG;

		public String getHttpContentType() {
			return "image/" + this.toString().toLowerCase();
		}

		public static Types findByHttpContentType(String contentType) {
			contentType = contentType.toLowerCase();
			for (Types type : Types.values()) {
				if (type.getHttpContentType().equals(contentType)) {
					return type;
				}
			}
			return null;
		}
	}

	public static enum Conversions {
		NONE, SCALED, CROPPED;
	}

	public static StoredImageDescriptor build(String ownerId, Types type)
			throws IOException {
		StoredImageDescriptor image = new StoredImageDescriptor();
		image.setConversion(Conversions.NONE);
		image.setType(type);
		image.setUserId(ownerId);
		return image;
	}

	private String sourceImageId;

	private Types type;

	private Conversions conversion = Conversions.NONE;

	private String userId;

	private int width;

	private int height;

	private long size;

	private DateTime creationTimestamp = DateTime.now();

	public String getSourceImageId() {
		return sourceImageId;
	}

	public void setSourceImageId(String sourceImageId) {
		this.sourceImageId = sourceImageId;
	}

	public void setConversion(Conversions conversion) {
		this.conversion = conversion;
	}

	public Types getType() {
		return type;
	}

	public void setType(Types type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Conversions getConversion() {
		return conversion;
	}

	public DateTime getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(DateTime creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

}
