package com.vlille.checker.utils;

import android.view.View;

public final class ViewUtils {

	private ViewUtils() {}
	
	public static void switchView(View view, boolean show) {
		if (show) show(view);
		else hide(view);
	}
	
	public static void hide(View view) {
		view.setVisibility(View.GONE);
	}
	
	public static void show(View view) {
		view.setVisibility(View.VISIBLE);
	}
}
