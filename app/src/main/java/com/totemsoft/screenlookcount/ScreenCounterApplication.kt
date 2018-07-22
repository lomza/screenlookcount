package com.totemsoft.screenlookcount

import android.app.Application
import com.totemsoft.screenlookcount.utils.AppPreferences

/**
 * Application extension class for initializing Shared Preferences instance.
 *
 * @author Antonina
 */
class ScreenCounterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}