package net.exacode.spring.data.mongodb.utils.atomic.sequence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Sequence document.
 * <p>
 * Every sequence is identified by name and has numerical value that can be
 * atomically incremented and retrieved.
 * 
 * @author mendlik
 * 
 */
@Document(collection = "util.sequence")
public class Sequence {

	@Id
	private String name;

	private long value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Sequence [name=" + name + ", value=" + value + "]";
	}

}
