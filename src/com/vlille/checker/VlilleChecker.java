package com.vlille.checker;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.vlille.checker.R;

@ReportsCrashes(
	formKey = "dE42S2d6NkhjU2tSNFI0dFZ3NGVjSnc6MQ",
    mode = ReportingInteractionMode.TOAST,
    forceCloseDialogAfterToast = false,
    resToastText = R.string.crash_toast_text
)
public class VlilleChecker extends Application {
	
	@Override
	public void onCreate() {
		ACRA.init(this);
		
		super.onCreate();
	}
	
}
