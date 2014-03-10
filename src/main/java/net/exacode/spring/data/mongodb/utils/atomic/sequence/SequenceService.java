package net.exacode.spring.data.mongodb.utils.atomic.sequence;

import net.exacode.spring.data.mongodb.utils.TypedMongoOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Provides simple sequence mechanism for MongoDB.
 * 
 * @see Sequence
 * @author mendlik
 * 
 */
@Service
public class SequenceService {

	private final TypedMongoOperations<Sequence, String> mongo;

	public SequenceService(TypedMongoOperations<Sequence, String> mongo) {
		this.mongo = mongo;
	}

	@Autowired
	public SequenceService(MongoOperations mongo) {
		this.mongo = new TypedMongoOperations<Sequence, String>(mongo,
				Sequence.class);
	}

	/**
	 * Increments sequence identified by name.
	 * <p>
	 * If there is no sequence with given name then new sequence is created and
	 * returned value is equal to 1.
	 * 
	 * @param name
	 * @return
	 */
	public long getNextSequence(String name) {
		Query query = Query.query(Criteria.where(Sequence_.name).is(name));
		FindAndModifyOptions options = new FindAndModifyOptions().upsert(true)
				.returnNew(true);
		Update update = new Update().inc(Sequence_.value, 1);
		Sequence sequence = mongo.findAndModify(query, update, options);
		return sequence.getValue();
	}

}
