package com.totemsoft.screenlookcount.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.totemsoft.screenlookcount.db.DayLookEntity
import com.totemsoft.screenlookcount.db.ScreenCounterDb
import com.totemsoft.screenlookcount.utils.C
import com.totemsoft.screenlookcount.utils.toPatternString
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * Listens to SCREEN_ON, SCREEN_OFF, and USER_PRESENT events, and takes care of updating counters.
 *
 * @author Antonina
 */
class ScreenLookReceiver : BroadcastReceiver() {

    /**
     * Increase look count for the day. Create or update item based on Date
     * and increase the appropriate counter
     *
     * @param context this BroadcastReceiver's context
     * @param intent  incoming intent
     */
    private fun increaseScreenLookCountForTheDay(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(C.TAG, "Action: $action")

        var screenOnCount = 0
        var screenOffCount = 0
        var screenUnlockCount = 0

        when (action) {
            Intent.ACTION_SCREEN_ON -> {
                screenOnCount += 1
            }
            Intent.ACTION_SCREEN_OFF -> {
                screenOffCount += 1
            }

            Intent.ACTION_USER_PRESENT -> {
                screenUnlockCount += 1
            }
            else -> {
            }
        }

        action?.let {
            val nowDateString = Date().toPatternString(context)
            ScreenCounterDb.getDatabase(context)
                    .getDayLook(nowDateString)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribeBy(
                            onSuccess = {
                                // update existing daylook
                                with(it) {
                                    screenon = if (screenon != null) screenon!!.plus(screenOnCount) else 0
                                    screenoff = if (screenoff != null) screenoff!!.plus(screenOffCount) else 0
                                    screenunlock = if (screenunlock != null) screenunlock!!.plus(screenUnlockCount) else 0
                                    addLooks(context, this)
                                }
                            },
                            onComplete = {
                                // create a new daylook
                                val dayLook = DayLookEntity()
                                with(dayLook) {
                                    date = nowDateString
                                    screenon = screenOnCount
                                    screenoff = screenOffCount
                                    screenunlock = screenUnlockCount
                                    addLooks(context, this)
                                }
                            },
                            onError = {
                                Log.e(C.TAG, "getDayLook() error: ${it.printStackTrace()}")
                            }
                    )
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }

        increaseScreenLookCountForTheDay(context, intent)
    }

    private fun addLooks(context: Context, dayLook: DayLookEntity) {
        ScreenCounterDb.getDatabase(context).addDayLook(dayLook)
    }
}
