package com.vlille.checker;

import android.content.Context;
import android.util.Log;

import com.vlille.checker.utils.Constants;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.droidparts.AbstractApplication;

@ReportsCrashes(
	formKey = Constants.GOOGLE_DOCS_FORM_KEY,
    mode = ReportingInteractionMode.TOAST,
    forceCloseDialogAfterToast = false,
    resToastText = R.string.crash_toast_text
)
public class Application extends AbstractApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}