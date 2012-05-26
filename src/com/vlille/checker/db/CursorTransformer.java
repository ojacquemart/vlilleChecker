package com.vlille.checker.db;

import java.util.List;

public interface CursorTransformer<T> {

	List<T> all();
	T single();
	
}
