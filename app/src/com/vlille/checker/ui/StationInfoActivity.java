package com.vlille.checker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vlille.checker.R;
import com.vlille.checker.model.Station;
import com.vlille.checker.model.StationHolder;
import com.vlille.checker.utils.MapsIntentChooser;
import com.vlille.checker.utils.ViewUtils;
import com.vlille.checker.utils.color.ColorSelector;

import org.droidparts.activity.support.v7.ActionBarActivity;

public class StationInfoActivity extends ActionBarActivity {

    private static final String TAG = StationInfoActivity.class.getSimpleName();

    private StationHolder holder;
    private ImageButton btnStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.station_info_layout);

        setDataFromIntent();
        initStationInfo();
    }

    private void setDataFromIntent() {
        Bundle extras = getIntent().getExtras();

        this.holder = (StationHolder) extras.get(IntentCommunication.STATION_DATA);
        this.holder.storeInitialStarValue();
    }

    private void initStationInfo() {
        Station station = holder.getStation();
        Log.d(TAG, "Setting initial station info values for " + station.getId());

        setTitle(station.getName());

        getTextView(R.id.station_name).setText(station.getName());
        getTextView(R.id.station_address).setText(station.getAdress());

        TextView nbBikes = getTextView(R.id.station_info_bikes);
        nbBikes.setText(station.getBikesAsString());
        nbBikes.setTextColor(ColorSelector.getColor(getApplicationContext(), station.getBikes()));

        TextView nbAttachs = getTextView(R.id.station_attachs);
        nbAttachs.setText(station.getAttachsAsString());
        nbAttachs.setTextColor(ColorSelector.getColor(getApplicationContext(), station.getAttachs()));

        ViewUtils.switchView(findViewById(R.id.station_cb), station.isCbPaiement());
        ViewUtils.switchView(findViewById(R.id.station_express), station.isExpress());

        handleBtnStar();
        handleBtnToLocate();
        handleBtnToItinerary();
    }

    private void handleBtnStar() {
        btnStar = (ImageButton) findViewById(R.id.station_star);
        setStarImageResource();

        btnStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStarClick();
            }
        });
    }

    private void handleStarClick() {
        Log.d(TAG, "Changing the star status");
        Station station = holder.getStation();
        station.setStarred(!station.isStarred());

        if (holder.isStarredChanged()) {
            setResult(IntentCommunication.STATION_STAR_RESULT_CODE, getIntentWithStationHolder());
        } else {
            setResult(IntentCommunication.NO_OP_RESULT_CODE, null);
        }

        setStarImageResource();
    }

    private void setStarImageResource() {
        Station station = holder.getStation();
        Log.d(TAG, "Changing the station star " + station.isStarred());
        if (station.isStarred()) {
            btnStar.setImageResource(R.drawable.btn_star_yellow);
        } else {
            btnStar.setImageResource(R.drawable.btn_star_grey);
        }
    }

    private void handleBtnToLocate() {
        ImageButton btnLocate = (ImageButton) findViewById(R.id.station_to_locate);
        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Setting result to move map to the station geoPoint");
                setResult(IntentCommunication.MAP_GEO_POINT_RESULT_CODE, getIntentWithStationHolder());

                finish();
            }
        });
    }

    private void handleBtnToItinerary() {
        findViewById(R.id.station_to_itinerary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Opening map intent");
                MapsIntentChooser.chooseIntent(getApplicationContext(), holder.getStation());
            }
        });
    }

    private TextView getTextView(int resourceId) {
        return (TextView) findViewById(resourceId);
    }

    private Intent getIntentWithStationHolder() {
        Intent intent = new Intent();
        intent.putExtra(IntentCommunication.STATION_DATA, holder);

        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

}
