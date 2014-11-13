package com.vlille.checker.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.vlille.checker.Application;
import com.vlille.checker.R;

/**
 * Show informations about vlille checker.
 */
public class AboutActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.about);

        String versionNumber = Application.getVersionNumber();

        TextView tvAboutText = (TextView) findViewById(R.id.about_text);
        tvAboutText.setText(getString(R.string.about_text, versionNumber));
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

}
