package com.vlille.checker.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public abstract class Table {

	private final String tableName;
	private final List<Field> fields = new ArrayList<Field>();

	public Table(String tableName) {
		this.tableName = tableName;
	}

	protected void add(Field field) {
		fields.add(field);
	}
	
	public String getName() {
		return this.tableName;
	}
	
	/**
	 * @throws IllegalStateException When no fields renseigned.
	 */
	@Override
	public String toString() {
		if (fields.isEmpty()) {
			throw new IllegalStateException("Fields are empty");
		}
		
		SqlStringBuilder sqlBuilder = new SqlStringBuilder();
		sqlBuilder.append("CREATE TABLE")
			.space()
			.append(tableName)
			.space()
			.append("(")
			.append(StringUtils.join(fields, ","))
			.append(");");
		
		return sqlBuilder.toString();
	}

}
