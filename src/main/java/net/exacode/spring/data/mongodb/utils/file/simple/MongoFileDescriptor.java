package net.exacode.spring.data.mongodb.utils.file.simple;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MongoFileDescriptor {

	private final String previousFileId;

	private final String type;

	private final String userId;

	private final long size;

	private final DateTime creationTimestamp;

	private MongoFileDescriptor(Builder builder) {
		this.previousFileId = builder.previousFileId;
		this.type = builder.type;
		this.userId = builder.userId;
		this.size = builder.size;
		this.creationTimestamp = builder.creationTimestamp;
	}

	public String getPreviousFileId() {
		return previousFileId;
	}

	public String getType() {
		return type;
	}

	public String getUserId() {
		return userId;
	}

	public long getSize() {
		return size;
	}

	public DateTime getCreationTimestamp() {
		return creationTimestamp;
	}

	public static class Builder {
		private String previousFileId;
		private String type;
		private String userId;
		private long size;
		private DateTime creationTimestamp = DateTime.now();

		public Builder previousFileId(String previousFileId) {
			this.previousFileId = previousFileId;
			return this;
		}

		public Builder type(String type) {
			this.type = type;
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
