package com.vlille.checker.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class StationResultReceiver extends ResultReceiver {

	private Receiver receiver;
	
	public StationResultReceiver(Handler handler) {
		super(handler);
	}
	
	public interface Receiver {
		
		public static final String RECEIVER = "receiver";
		public int RUNNING = 1;
		public int FINISHED = 0;
		public int ERROR = -1;
		
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if (receiver != null) {
			receiver.onReceiveResult(resultCode, resultData);
		}
	}

	public void setReceiver(Receiver homeActivity) {
		this.receiver = homeActivity;
		
	}
	

}
