package net.exacode.spring.data.mongodb.utils.file;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MongoFileDescriptor {

	private final String parentFileId;

	private final String contentType;

	private final String ownerId;

	private final long size;

	private final DateTime creationTimestamp;

	private MongoFileDescriptor(Builder builder) {
		this.parentFileId = builder.parentFileId;
		this.contentType = builder.contentType;
		this.ownerId = builder.userId;
		this.size = builder.size;
		this.creationTimestamp = builder.creationTimestamp;
	}

	public String getParentFileId() {
		return parentFileId;
	}

	public String getContentType() {
		return contentType;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public long getSize() {
		return size;
	}

	public DateTime getCreationTimestamp() {
		return creationTimestamp;
	}

	public static class Builder {
		private String parentFileId;
		private String contentType;
		private String userId;
		private long size;
		private DateTime creationTimestamp = DateTime.now();

		public Builder previousFileId(String previousFileId) {
			this.parentFileId = previousFileId;
			return this;
		}

		public Builder contentType(String type) {
			this.contentType = type;
			return this;
		}

		public Builder userId(String userId) {
			this.userId = userId;
			return this;
		}

		public Builder size(long size) {
			this.size = size;
			return this;
		}

		public Builder creationTimestamp(DateTime creationTimestamp) {
			this.creationTimestamp = creationTimestamp;
			return this;
		}

		public MongoFileDescriptor build() {
			return new MongoFileDescriptor(this);
		}
	}

}
