package com.vlille.checker.db;

public class SqlStringBuilder {

	private final StringBuilder builder = new StringBuilder();

	public SqlStringBuilder space() {
		return append(" ");
	}

	public SqlStringBuilder appendIfTheBooleanIsTrue(boolean theBoolean, Object value) {
		if (theBoolean) {
			append(value).space();
		}

		return this;
	}

	public SqlStringBuilder append(Object value) {
		builder.append(value);

		return this;
	}

	@Override
	public String toString() {
		return builder.toString().trim();
	}
	
}
