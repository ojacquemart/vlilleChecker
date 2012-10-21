package com.vlille.checker.db;

public class Field {
	
	/**
	 * TODO: maybe add a list<attribute> to replace the properties.
	 */

	private String name;
	private Type type;
	private boolean primaryKey;
	private boolean nullable;
	public boolean autoIncrement;

	private Field(String name) {
		this.name = name;
	}

	public static Field newField(Object name) {
		return new Field(name.toString());
	}
	
	public Field type(Type type) {
		this.type = type;

		return this;
	}	

	public Field primaryKey() {
		this.primaryKey = true;

		return this;
	}
	
	public Field nullable() {
		this.nullable = true;
		
		return this;
	}
	
	public Field notNullable() {
		this.nullable = false;
		
		return this;
	}

	public Field autoIncrement() {
		this.autoIncrement = true;

		return this;
	}

	@Override
	public String toString() {
		final SqlStringBuilder sqlBuilder = new SqlStringBuilder()
			.append(name)
			.space()
			.append(type)
			.space()
			.appendIfTheBooleanIsTrue(primaryKey, "primary key")
			.appendIfTheBooleanIsTrue(autoIncrement, "autoincrement")
			.appendIfTheBooleanIsTrue(!nullable, "not null");
		
		return sqlBuilder.toString();
	}

}
