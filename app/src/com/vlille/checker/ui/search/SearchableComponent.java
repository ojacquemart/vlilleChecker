package com.vlille.checker.ui.search;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import com.vlille.checker.model.Station;

import java.util.ArrayList;
import java.util.List;

public class SearchableComponent {

    private static final String TAG = SearchableComponent.class.getSimpleName();

    private ViewParent viewParent;

    public SearchableComponent(ViewParent viewParent) {
        if (viewParent == null) {
            throw new IllegalArgumentException("View parent can not be null!");
        }

        this.viewParent = viewParent;
    }

    public void init() {
        this.initSearchFieldListeners();
    }

    public void hideInputMethodManager() {
        InputMethodManager imm = (InputMethodManager) viewParent.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(viewParent.getSearchField().getWindowToken(), 0);
    }

    private void initSearchFieldListeners() {
        initSearchTextListener();
        initClearTextListener();
    }

    private void initSearchTextListener() {
        final EditText searchField = viewParent.getSearchField();
        searchField.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKeyListener " + event);
                if (hasPressedOk(keyCode, event)) {
                    hideInputMethodManager();

                    final String keyword = searchField.getText().toString();
                    filterStationsByKeyword(keyword);
                }

                return false;
            }

            private boolean hasPressedOk(int keyCode, KeyEvent event) {
                return event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER;
            }
        });
    }

    private void initClearTextListener() {
        final ImageButton clearButton = viewParent.getClearButton();
        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clear search editText");
                EditText editText = viewParent.getSearchField();
                String oldText = editText.getText().toString();
                editText.setText(null);

                // Only reload stations if there was something in the input.
                if (!TextUtils.isEmpty(oldText)) {
                    filterStationsByKeyword(null);
                }
            }
        });
    }


    private void filterStationsByKeyword(final String keyword) {
        Log.d(TAG, "Text searched: " + keyword);

        final List<Station> filteredStations = filter(keyword);
        if (!filteredStations.isEmpty()) {
            viewParent.setStations(filteredStations);
        } else {
            viewParent.showNoResultMessage();
        }

        viewParent.afterFilterElements();
    }

    private List<Station> filter(String keyword) {
        if (keyword == null || keyword.length() == 0) {
            return viewParent.getOriginalStations();
        }

        keyword = keyword.toLowerCase();

        List<Station> result = new ArrayList<Station>();

        List<Station> stations = viewParent.getOriginalStations();
        for (Station eachStation : stations) {
            if (eachStation.getName().toLowerCase().contains(keyword)) {
                result.add(eachStation);
            }
        }

        return result;
    }

    public interface ViewParent {
        Activity getActivity();

        void afterFilterElements();

        List<Station> getOriginalStations();

        void setStations(List<Station> stations);

        EditText getSearchField();

        ImageButton getClearButton();

        void showNoResultMessage();

    }
}
