package net.exacode.spring.data.mongodb.utils.lang;

import net.exacode.spring.data.mongodb.utils.lang.WordTransformation.WordTransformationId;
import net.exacode.spring.data.mongodb.utils.lang.names.NameTransformation_;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class WordTransformationService {

	private final MongoOperations mongoTemplate;

	@Autowired
	public WordTransformationService(MongoOperations mongoOperations) {
		this.mongoTemplate = mongoOperations;
	}

	public String transform(String lang, String word, String transformationKey) {
		WordTransformationId id = new WordTransformationId(word, lang);
		Query query = Query.query(Criteria.where(NameTransformation_.id._path)
				.is(id));
		WordTransformation transformation = mongoTemplate.findOne(query,
				WordTransformation.class);
		return (transformation == null) ? word : transformation
				.transform(transformationKey);
	}
}
