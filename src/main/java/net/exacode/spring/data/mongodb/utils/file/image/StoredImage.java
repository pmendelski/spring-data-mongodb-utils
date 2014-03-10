package net.exacode.spring.data.mongodb.utils.file.image;

import java.io.IOException;
import java.io.InputStream;

import net.exacode.spring.data.mongodb.utils.file.image.StoredImageDescriptor.Types;

public class StoredImage {
	public static StoredImage build(String id, String ownerId, InputStream is,
			Types type) throws IOException {
		StoredImage image = new StoredImage();
		image.setInputStream(is);
		image.setId(id);
		image.setDescriptor(StoredImageDescriptor.build(ownerId, type));
		return image;
	}

	private InputStream inputStream;

	private String id;

	private StoredImageDescriptor descriptor;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StoredImageDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(StoredImageDescriptor descriptor) {
		this.descriptor = descriptor;
	}

}
