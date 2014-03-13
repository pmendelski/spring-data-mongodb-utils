package net.exacode.spring.data.mongodb.utils.file;

import java.io.InputStream;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

@Document(collection = "fs.files")
public class MongoFile<T> {

	private Object id;

	private final String filename;

	@Transient
	private final InputStream content;

	private final T metadata;

	private String contentType;

	private DateTime uploadDate;

	// this checksum is created by GridFs
	private String md5;

	private MongoFile(Builder<T> builder) {
		this.id = builder.id;
		this.filename = builder.filename;
		this.content = builder.content;
		this.metadata = builder.metadata;
		this.contentType = builder.contentType;
		this.uploadDate = builder.uploadDate;
		this.md5 = builder.md5;
	}

	public MongoFile(String filename, InputStream content, T metadata) {
		this.filename = filename;
		this.content = content;
		this.metadata = metadata;
	}

	public String getFilename() {
		return filename;
	}

	public InputStream getContent() {
		return content;
	}

	public Object getId() {
		return id;
	}

	public String getContentType() {
		return contentType;
	}

	public DateTime getUploadDate() {
		return uploadDate;
	}

	public String getMd5() {
		return md5;
	}

	public T getMetadata() {
		return metadata;
	}

	public boolean isStored() {
		return md5 != null;
	}

	@Override
	public String toString() {
		return "MongoFile [id=" + id + "]";
	}

	public static <T> Builder<T> builder() {
		return new Builder<T>();
	}

	public static class Builder<T> {
		private Object id;
		private String filename;
		private InputStream content;
		private T metadata;
		private String contentType;
		private DateTime uploadDate;
		private String md5;

		public Builder<T> id(Object id) {
			this.id = id;
			return this;
		}

		public Builder<T> filename(String filename) {
			this.filename = filename;
			return this;
		}

		public Builder<T> content(InputStream content) {
			this.content = content;
			return this;
		}

		public Builder<T> metadata(T metadata) {
			this.metadata = metadata;
			return this;
		}

		public Builder<T> contentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public Builder<T> uploadDate(DateTime uploadDate) {
			this.uploadDate = uploadDate;
			return this;
		}

		public Builder<T> uploadDate(Date uploadDate) {
			this.uploadDate = new DateTime(uploadDate);
			return this;
		}

		public Builder<T> md5(String md5) {
			this.md5 = md5;
			return this;
		}

		public MongoFile<T> build() {
			Assert.notNull(id);
			Assert.notNull(filename);
			Assert.notNull(content);
			Assert.notNull(uploadDate);
			Assert.notNull(md5);
			return new MongoFile<T>(this);
		}
	}

}
