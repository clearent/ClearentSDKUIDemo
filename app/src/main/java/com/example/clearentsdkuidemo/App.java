package com.example.clearentsdkuidemo;

import android.app.Application;

import com.clearent.idtech.android.wrapper.ClearentWrapper;

public class App extends Application {

    private final ClearentWrapper cw = ClearentWrapper.Companion.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();

        initSdkWrapper();
    }

    private void initSdkWrapper() {
        cw.initializeSDK(
                getApplicationContext(),
                Constants.BASE_URL_SANDBOX,
                Constants.PUBLIC_KEY_SANDBOX,
                Constants.API_KEY_SANDBOX,
                null,
                true
        );
    }
}
