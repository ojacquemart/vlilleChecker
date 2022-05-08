package com.vlille.checker;

import android.content.Context;
import android.content.pm.PackageManager;
import org.droidparts.AbstractApplication;

public class Application extends AbstractApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
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

}