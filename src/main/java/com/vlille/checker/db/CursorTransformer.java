package com.vlille.checker.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public abstract class CursorTransformer<T> {

	protected Cursor cursor;
	
	public CursorTransformer(Cursor cursor) {
		this.cursor = cursor;
	}
	
	public List<T> all() {
		List<T> result = new ArrayList<T>();
		while (cursor.moveToNext()) {
			result.add(single());
		}
		
		cursor.close();
		
		return result;
	}
	
	public T first() {
		return all().get(0);
	}
	
	public abstract T single();
	
}
