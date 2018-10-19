package com.vlille.checker.manager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.vlille.checker.Application;

public class AnalyticsManager {

    public static void trackScreenView(String screenName) {
        Tracker tracker = Application.getDefaultTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder()
                .build());
    }

    public static void trackEvent(String screenName, String actionName, String actionCategory) {
        Tracker tracker = Application.getDefaultTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.EventBuilder()
                .setAction(actionName)
                .setCategory(actionCategory)
                .build());
    }
}
