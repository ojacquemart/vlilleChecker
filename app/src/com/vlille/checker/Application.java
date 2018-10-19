package com.vlille.checker;

import android.content.Context;
import android.content.pm.PackageManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.vlille.checker.utils.Constants;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.droidparts.AbstractApplication;

@ReportsCrashes(
        formUri = Constants.TRACEPOT_REPORT_URl,
        mode = ReportingInteractionMode.TOAST,
        forceCloseDialogAfterToast = false,
        resToastText = R.string.crash_toast_text
)
public class Application extends AbstractApplication {

    private static Context context;
    private static GoogleAnalytics googleAnalytics;
    private static Tracker analyticsTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
        context = getApplicationContext();
        googleAnalytics = GoogleAnalytics.getInstance(this);
    }

    public static Context getContext() {
        return context;
    }

    public static String getVersionNumber() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "???";
        }
    }

    synchronized public static Tracker getDefaultTracker() {
        if (analyticsTracker == null) {
            analyticsTracker = googleAnalytics.newTracker(R.xml.global_tracker);
        }
        return analyticsTracker;
    }

}