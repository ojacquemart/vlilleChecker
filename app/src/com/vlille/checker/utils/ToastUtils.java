package com.vlille.checker.utils;

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
	public static void show(final Context context, final String message) {
		if (context == null) {
			return;
		}

		if (TextUtils.isEmpty(message)) {
			return;
		}

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		}).run();
	}

	/**
	 * Show the message with the given resource id in a {@link Toast}
	 * <p>
	 * This method may be called from any thread
	 */
	public static void show(final Context context, final int resId) {
		if (context == null) {
			return;
        }

		show(context, context.getString(resId));
	}

}
