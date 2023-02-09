package com.example.clearentsdkuidemo

import android.app.Application
import com.clearent.idtech.android.wrapper.ClearentWrapper

class App : Application() {

    private val clearentWrapper = ClearentWrapper.getInstance()

    override fun onCreate() {
        super.onCreate()

        // Initialize the sdk
        initSdk()
    }

    private fun initSdk() = clearentWrapper.initializeSDK(
        applicationContext,
        Constants.BASE_URL_SANDBOX,
        Constants.PUBLIC_KEY_SANDBOX,
        Constants.API_KEY_SANDBOX
    )
}