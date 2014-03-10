package net.exacode.spring.data.mongodb.utils.file.simple;

import java.io.InputStream;

public class MongoFile {

	private final String id;

	private final InputStream inputStream;

	private final MongoFileDescriptor descriptor;

	public MongoFile(String id, InputStream inputStream,
			MongoFileDescriptor descriptor) {
		this.id = id;
		this.inputStream = inputStream;
		this.descriptor = descriptor;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public String getId() {
		return id;
	}

	public MongoFileDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public String toString() {
		return "MongoFile [id=" + id + "]";
	}

}
