package com.vlille.checker.activity;

import com.vlille.checker.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class HomeSettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
	
}
