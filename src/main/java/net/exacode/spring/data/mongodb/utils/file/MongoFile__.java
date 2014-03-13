package net.exacode.spring.data.mongodb.utils.file;

/**
 * Spring Data MongoDB meta model of {@link MongoFile}.
 * 
 */
public class MongoFile__ {

	public final String _class = "_class";

	public static final String contentType = "contentType";
	public static final String uploadDate = "uploadDate";
	public static final String filename = "filename";
	public static final String id = "_id";
	public static final String md5 = "md5";
	public static final String metadata = "metadata";

	public static final String metadata(String metadataField) {
		return metadata + "." + metadataField;
	}
}
