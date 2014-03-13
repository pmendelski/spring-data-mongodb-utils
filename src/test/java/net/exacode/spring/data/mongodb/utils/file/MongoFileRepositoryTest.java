package net.exacode.spring.data.mongodb.utils.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.exacode.spring.data.mongodb.utils.IntegrationTestBase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoFileRepositoryTest extends IntegrationTestBase {

	private static final String FILE1_VER1 = "text1-ver1.txt";
	private static final String FILE1_VER2 = "text1-ver2.txt";
	private static final String FILE2_VER1 = "text2-ver1.txt";
	private static final String FILE2_VER2 = "text2-ver2.txt";

	private String getFileExpectedContent(String filename) {
		int dotIdx = filename.indexOf('.');
		return (dotIdx >= 0) ? filename.substring(0, dotIdx) : filename;
	}

	private InputStream getFileInputStream(String filename) {
		return this.getClass().getResourceAsStream(filename);
	}

	private String convertToString(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			throw new AssertionError(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	@Autowired
	private MongoFileRepository mongoFileRepository;

	@Test
	public void shouldInsertFile() {
		// given
		String filename = FILE1_VER1;
		InputStream is = getFileInputStream(filename);
		MongoFile<DBObject> mongoFile = new MongoFile<DBObject>(filename, is,
				new BasicDBObject());

		// when
		mongoFileRepository.insert(mongoFile);

		// then
		MongoFile<DBObject> retrieved = mongoFileRepository
				.findByFilename(filename);
		assertThat(retrieved).isNotNull();
		assertThat(retrieved.getFilename()).isEqualTo(filename);
		assertThat(convertToString(retrieved.getContent())).isEqualTo(
				getFileExpectedContent(filename));
	}

	@Test
	public void shouldInsertFileWithSameFilenameAndDifferentUploadDate() {
		// given
		String filename = "sample.txt";
		String filename1 = FILE1_VER1;
		String filename2 = FILE1_VER2;
		InputStream is1 = getFileInputStream(filename1);
		InputStream is2 = getFileInputStream(filename2);
		MongoFile<DBObject> mongoFile1 = new MongoFile<DBObject>(filename, is1,
				new BasicDBObject());
		MongoFile<DBObject> mongoFile2 = new MongoFile<DBObject>(filename, is2,
				new BasicDBObject());

		// when
		mongoFileRepository.insert(mongoFile1);
		mongoFileRepository.insert(mongoFile2);

		// then
		MongoFile<DBObject> retrieved = mongoFileRepository
				.findByFilename(filename);
		assertThat(retrieved).isNotNull();
		assertThat(retrieved.getFilename()).isEqualTo(filename);
		assertThat(convertToString(retrieved.getContent())).isEqualTo(
				getFileExpectedContent(filename2));
	}
}
