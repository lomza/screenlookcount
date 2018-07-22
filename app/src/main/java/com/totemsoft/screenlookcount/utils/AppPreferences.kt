package com.totemsoft.screenlookcount.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Object for managing app's Shared Preferences
 *
 * @author Antonina
 */
object AppPreferences {
    private const val NAME = "SLCPreferenceFile"
    private const val IS_SERVICE_RUNNING = "service_running_bool"
    private lateinit var preferences: SharedPreferences

    fun init(ctx: Context) {
        preferences = ctx.getSharedPreferences(NAME, 0)
    }

    var shouldRunCountingService: Boolean
        get() = preferences.getBoolean(IS_SERVICE_RUNNING, true)
        set(value) = preferences.edit().putBoolean(IS_SERVICE_RUNNING, value).apply()
}