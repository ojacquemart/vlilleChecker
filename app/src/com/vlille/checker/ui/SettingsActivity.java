package com.vlille.checker.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.vlille.checker.R;
import com.vlille.checker.manager.AnalyticsManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.settings_main);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsManager.trackScreenView("Settings Screen");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
