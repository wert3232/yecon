package com.autochips.weather;

public interface HttpTaskListener {

    void onSuccess(String response);

    void onException(int returnCode);
}
