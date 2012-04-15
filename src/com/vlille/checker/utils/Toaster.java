package com.vlille.checker.utils;

import com.vlille.checker.R;

import android.content.Context;
import android.widget.Toast;

public class Toaster {

	private final Context context;

	private Toaster(Context context) {
		super();
		this.context = context;
	}
	
	public static Toaster withContext(Context context) {
		return new Toaster(context);
	}
	
	public void noConnection() {
		toast(context.getString(R.string.no_connection));
	}
	
	public void toast(String msg) {
		Toast.makeText(
				context,
				msg,
				Toast.LENGTH_LONG)
				.show();
		
	}
}
