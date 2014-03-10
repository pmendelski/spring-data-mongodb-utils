package net.exacode.spring.data.mongodb.utils.atomic.unique;

import java.util.Arrays;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Document that represents unique value or tuple.
 * <p>
 * Every {@link UniqueValue} document is identified by the value or the tuple.
 * It contains also a token that decides which claimer was first.
 * 
 * @author mendlik
 * 
 */
@Document(collection = "util.unique")
public class UniqueValue {

	public static class UniqueValueId {

		private final Object[] values;

		public UniqueValueId(Object[] values) {
			this.values = values;
		}

		public Object[] getValues() {
			return values;
		}

		@Override
		public String toString() {
			return "UniqueValueId [values=" + Arrays.toString(values) + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(values);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UniqueValueId other = (UniqueValueId) obj;
			if (!Arrays.equals(values, other.values))
				return false;
			return true;
		}

	}

	@Id
	private final UniqueValueId uniqueValueId;

	private final Object token;

	@PersistenceConstructor
	public UniqueValue(Object token, UniqueValueId uniqueValueId) {
		this.uniqueValueId = uniqueValueId;
		this.token = token;
	}

	public UniqueValue(Object ownerId, Object... values) {
		this(ownerId, new UniqueValueId(values));
	}

	public Object getToken() {
		return token;
	}

	public Object[] getValues() {
		return uniqueValueId.getValues();
	}

	public boolean isClaimedWithToken(Object token) {
		return (this.token == null) ? false : this.token.equals(token);
	}

	@Override
	public String toString() {
		return "UniqueValue [values="
				+ Arrays.toString(uniqueValueId.getValues()) + ", token="
				+ token + "]";
	}

}
