package com.totemsoft.screenlookcount.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.util.Log
import com.totemsoft.screenlookcount.utils.AppPreferences

import com.totemsoft.screenlookcount.utils.C

/**
 * Receiver that listens to ACTION_BOOT_COMPLETED event.
 *
 * @author Antonina
 */
class RebootActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }

        val action = intent.action
        action?.let {
            if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
                Log.d(C.TAG, "Boot completed action received.")
                if (AppPreferences.shouldRunCountingService) {
                    val serviceIntent = Intent(context, LookCounterService::class.java)
                    ContextCompat.startForegroundService(context, serviceIntent)
                }
            }
        }
    }
}
