package pro.buildmysoftware;

import org.bson.types.ObjectId;

import java.util.Objects;

public class Pojo {
	private ObjectId id;
	private String stringField;
	private int intField;
	private Pojo nestedPojoField;

	public Pojo() {
	}

	public Pojo(ObjectId id, String stringField, int intField,
		    Pojo nestedPojoField) {
		this.id = id;
		this.stringField = stringField;
		this.intField = intField;
		this.nestedPojoField = nestedPojoField;
	}

	@Override
	public String toString() {
		return "Pojo{" + "id='" + id + '\'' + ", stringField='" + stringField + '\'' + ", intField=" + intField + ", nestedPojoField=" + nestedPojoField + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Pojo pojo = (Pojo) o;
		return intField == pojo.intField && Objects
			.equals(id, pojo.id) && Objects
			.equals(stringField, pojo.stringField) && Objects
			.equals(nestedPojoField, pojo.nestedPojoField);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, stringField, intField,
			nestedPojoField);
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	public int getIntField() {
		return intField;
	}

	public void setIntField(int intField) {
		this.intField = intField;
	}

	public Pojo getNestedPojoField() {
		return nestedPojoField;
	}

	public void setNestedPojoField(Pojo nestedPojoField) {
		this.nestedPojoField = nestedPojoField;
	}
}
