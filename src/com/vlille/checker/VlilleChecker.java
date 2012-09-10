package com.vlille.checker;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.util.Log;

import com.vlille.checker.db.DbAdapter;
import com.vlille.checker.utils.Constants;

@ReportsCrashes(
	formKey = Constants.GOOGLE_DOCS_FORM_KEY,
    mode = ReportingInteractionMode.TOAST,
    forceCloseDialogAfterToast = false,
    resToastText = R.string.crash_toast_text
)
public class VlilleChecker extends Application {
	
	
	private final String LOG_TAG = getClass().getSimpleName();
	
	private static DbAdapter dbAdapter;
	
	@Override
	public void onCreate() {
		ACRA.init(this);
		
		super.onCreate();
		
		Log.d(LOG_TAG, "DbAdapter initialization");
		dbAdapter = new DbAdapter(this);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		
		dbAdapter.close();
		dbAdapter = null;
	}

	/**
	 * Get the db adapter to make queries.
	 * @return The db adapter.
	 */
	public static DbAdapter getDbAdapter() {
		return dbAdapter;
	}
	
}
