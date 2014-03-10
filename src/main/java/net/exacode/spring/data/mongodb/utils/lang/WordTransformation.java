package net.exacode.spring.data.mongodb.utils.lang;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "util.lang.words")
public class WordTransformation {

	@SuppressWarnings("serial")
	public static class WordTransformationId implements Serializable {

		private final String word;

		private final String lang;

		public WordTransformationId(String word, String lang) {
			super();
			this.word = word;
			this.lang = lang;
		}

		public String getWord() {
			return word;
		}

		public String getLang() {
			return lang;
		}

	}

	@Id
	private WordTransformationId id;

	private Map<String, String> transformations;

	public WordTransformationId getId() {
		return id;
	}

	public void setId(WordTransformationId id) {
		this.id = id;
	}

	public String transform(String transformationKey) {
		String tranformation = transformations.get(transformationKey);
		return (tranformation == null) ? id.word : tranformation;
	}

}
