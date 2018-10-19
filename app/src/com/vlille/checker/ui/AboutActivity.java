package com.vlille.checker.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.vlille.checker.Application;
import com.vlille.checker.R;

/**
 * Show informations about vlille checker.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.about);

        setVersionNumber();
        setClickableLinks();
    }

    private void setVersionNumber() {
        String versionNumber = Application.getVersionNumber();

        TextView aboutVersion = (TextView) findViewById(R.id.about_version);
        aboutVersion.setText(versionNumber);
    }

    private void setClickableLinks() {
        setClickableLink(R.id.about_twitter);
        setClickableLink(R.id.about_github);
    }

    private void setClickableLink(int textId) {
        TextView textView = findViewById(textId);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();

        return true;
    }

}
