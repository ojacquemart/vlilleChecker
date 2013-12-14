package com.vlille.checker.ui.async;

public interface AsyncTaskResultListener<R> {

    void onAsyncTaskPreExecute();

    void onAsyncTaskPostExecute(R result);
}