package com.vlille.checker.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Utilities for displaying toast notifications
 * 
 * @see <a href="https://github.com/github/android">Github android app</a>
 */
public final class ToastUtils {
	
	private ToastUtils() {}

	/**
	 * Show the given message in a {@link Toast}
	 * <p>
	 * This method may be called from any thread
	 */
	public static void show(final Activity activity, final String message) {
		if (activity == null) {
			return;
		}

		if (TextUtils.isEmpty(message)) {
			return;
		}

		activity.runOnUiThread(
				new Thread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
					}
				})
		);
	}

	/**
	 * Show the message with the given resource id in a {@link Toast}
	 * <p>
	 * This method may be called from any thread
	 */
	public static void show(final Activity activity, final int resId) {
		if (activity == null) {
			return;
        }

		show(activity, activity.getString(resId));
	}

}
