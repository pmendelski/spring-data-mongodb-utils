package net.exacode.spring.data.mongodb.utils.atomic.unique;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Arrays;
import java.util.UUID;

import net.exacode.spring.data.mongodb.utils.TypedMongoOperations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Provides a simple mechanism to keep all unique constraints in one MongoDB
 * collection.
 * <p>
 * It's especially useful in sharded environment.
 * 
 * @see UniqueValue
 * @author mendlik
 * 
 */
@Service
public class UniqueValueService {

	private static final String TOKEN = UniqueValue_.token._path;

	private static final String VALUES = UniqueValue_.uniqueValueId.values._path;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final TypedMongoOperations<UniqueValue, Object[]> mongoOperations;

	private static Object createToken() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	@Autowired
	public UniqueValueService(MongoOperations mongoOperations) {
		this.mongoOperations = new TypedMongoOperations<UniqueValue, Object[]>(
				mongoOperations, UniqueValue.class);
	}

	/**
	 * Reserves a value or a tuple.
	 * 
	 * @param values
	 * @return true if claimed value or claimed tuple was not already taken
	 */
	public boolean claim(Object... values) {
		Object token = createToken();
		return claimWithTokenAndRetrieve(token, values).isClaimedWithToken(
				token);
	}

	/**
	 * Reserves a value or a tuple with concrete token.
	 * 
	 * @param token
	 *            - every claim is associated with a token. That token is used
	 *            to examine which claim was first.
	 * @param values
	 * @return true if claimed value or claimed tuple was not already taken
	 */
	public boolean claimWithToken(Object token, Object... values) {
		UniqueValue uniqueValue = claimAndRetrieve(token, values);
		return uniqueValue.isClaimedWithToken(token);
	}

	/**
	 * Reserves a value or a tuple.
	 * 
	 * @param values
	 * @return if claim was successful new instance of {@link UniqueValue} is
	 *         returned, otherwise {@code null}.
	 */
	public UniqueValue claimAndRetrieve(Object... values) {
		String ownerId = UUID.randomUUID().toString().replace("-", "");
		return claimWithTokenAndRetrieve(ownerId, values);
	}

	/**
	 * Reserves a value or a tuple with concrete token.
	 * 
	 * @param token
	 *            - every claim is associated with a token. That token is used
	 *            to examine which claim was first.
	 * @param values
	 * @return if claim was successful new instance of {@link UniqueValue} is
	 *         returned, otherwise {@code null}.
	 */
	public UniqueValue claimWithTokenAndRetrieve(Object token, Object... values) {
		FindAndModifyOptions options = new FindAndModifyOptions().upsert(true)
				.returnNew(true);
		Query query = query(where(VALUES).is(values));
		Update update = new Update().setOnInsert(TOKEN, token);
		return mongoOperations.findAndModify(query, update, options);
	}

	/**
	 * Makes a value or a tuple available to claim.
	 * <p>
	 * This operation is successful only if the tuple was claimed with same
	 * token as the one passed as argument.
	 * 
	 * @param token
	 * @param values
	 */
	public void unclaim(String token, Object[] values) {
		Query query = query(where(TOKEN).is(token).andOperator(
				where(VALUES).is(values)));
		mongoOperations.remove(query);
		logger.debug("Removed unique constraint: {}:{}", token,
				Arrays.toString(values));
	}

	/**
	 * Makes a value or a tuple available to claim.
	 * 
	 * @param values
	 */
	public void unclaim(Object[] values) {
		Query query = query(where(VALUES).is(values));
		mongoOperations.remove(query);
		logger.debug("Removed unique constraint: {}", Arrays.toString(values));
	}

	/**
	 * Retrieves a claim token for a given value or a tuple.
	 * 
	 * @param values
	 * @return claim token if value (or tuple) was found, {@code null}
	 *         otherwise.
	 */
	public Object findToken(Object... values) {
		UniqueValue uniqueValue = mongoOperations.findById(values);
		return (uniqueValue == null) ? null : uniqueValue.getToken();
	}

	/**
	 * Checks if a value or a tuple was already claimed.
	 * 
	 * @param values
	 * @return {@code true} if a value or a tuple was already claimed
	 */
	public boolean isClaimed(Object... values) {
		Query query = query(where(VALUES).is(values));
		return mongoOperations.findOne(query) != null;
	}

	/**
	 * Checks if a value or a tuple was already claimed with a given token.
	 * 
	 * @param values
	 * @return {@code true} if a value or a tuple was already claimed with a
	 *         given token
	 */
	public boolean isClaimedWithToken(Object token, Object... values) {
		Query query = query(where(VALUES).is(values));
		UniqueValue uniqueValue = mongoOperations.findOne(query);
		return (uniqueValue == null) ? null : uniqueValue
				.isClaimedWithToken(token);
	}

}
