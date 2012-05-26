package com.vlille.checker.db;

import android.content.ContentValues;

public interface GetContentValues {

	ContentValues getInsertableContentValues();
	ContentValues getUpdatableContentValues();
	
}
