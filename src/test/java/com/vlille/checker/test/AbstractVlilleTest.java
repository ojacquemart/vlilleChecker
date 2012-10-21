package com.vlille.checker.test;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.util.Log;

public abstract class AbstractVlilleTest<T extends Activity> extends ActivityUnitTestCase<T> {

	protected final String LOG_TAG = getClass().getSimpleName();
	
	public AbstractVlilleTest(Class<T> activityClass) {
		super(activityClass);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		startActivity(new Intent(Intent.ACTION_MAIN), null, null);
	}
	
	public void error(Exception e) {
		Log.e(LOG_TAG, "Error", e);
	}

}
