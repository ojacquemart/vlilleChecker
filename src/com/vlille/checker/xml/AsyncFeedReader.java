package com.vlille.checker.xml;

import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncFeedReader.
 *
 * @param <T> The type to parse.
 */
public class AsyncFeedReader<T> extends AsyncTask<String, Void, T> {
	
	private final String TAG = getClass().getSimpleName();
	
	private BaseSAXParser<T> parser;

	public AsyncFeedReader(BaseSAXParser<T> parser) {
		this.parser = parser;
	}

	@Override
	protected T doInBackground(String... params) {
		try {
			return parser.parse(new XMLReader().getInputStream(params[0]));
			
		} catch (Exception e) {
			Log.e(TAG, "Error during parsing", e);
			
			throw new IllegalStateException("Error during parsing");
		}
	}

}
