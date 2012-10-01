package com.vlille.checker.utils;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Utilities for displaying toast notifications
 * 
 * @see github.com/github/android
 */
public class ToastUtils {

	/**
	 * Show the given message in a {@link Toast}
	 * <p>
	 * This method may be called from any thread
	 * 
	 * @param activity
	 * @param message
	 */
	public static void show(final Activity activity, final String message) {
		if (activity == null) {
			return;
		}

		if (TextUtils.isEmpty(message)) {
			return;
		}

		final Application application = activity.getApplication();
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(application, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Show the message with the given resource id in a {@link Toast}
	 * <p>
	 * This method may be called from any thread
	 * 
	 * @param activi
	 *            ty
	 * @param resId
	 */
	public static void show(final Activity activity, final int resId) {
		if (activity == null)
			return;

		show(activity, activity.getString(resId));
	}

}
