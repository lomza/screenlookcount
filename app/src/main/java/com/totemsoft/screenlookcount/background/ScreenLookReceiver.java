package com.totemsoft.screenlookcount.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.totemsoft.screenlookcount.AnalyticsApplication;
import com.totemsoft.screenlookcount.db.DayLook;
import com.totemsoft.screenlookcount.db.LookCountDbManager;
import com.totemsoft.screenlookcount.utils.C;
import com.totemsoft.screenlookcount.utils.Utils;

import java.util.Date;

/**
 * Listens to SCREEN_ON, SCREEN_OFF, and USER_PRESENT events, and takes care of updating counters.
 *
 * @author antonina.tkachuk
 */
public class ScreenLookReceiver extends BroadcastReceiver {

    /**
     * Increase look count for the day. Create or update item based on Date
     * and increase the appropriate counter
     *
     * @param context this BroadcastReceiver's context
     * @param intent  incoming intent
     */
    private void increaseScreenLookCountForTheDay(final Context context, final Intent intent) {
        String action = intent.getAction();
        Log.d(C.TAG, "Action: " + action);

        final Date nowDate = new Date();
        final String nowDateString = Utils.dateToString(nowDate);

        DayLook dayLook = LookCountDbManager.getManager(context).getDayLookByDate(nowDateString);
        if (dayLook == null) {
            dayLook = new DayLook();
            dayLook.setDate(nowDateString);
            dayLook.setScreenon(0);
            dayLook.setScreenoff(0);
            dayLook.setScreenunlock(0);
        }

        switch (action) {
            case Intent.ACTION_SCREEN_ON:
                dayLook.setScreenon(dayLook.getScreenon() + 1);
                ((AnalyticsApplication) context.getApplicationContext()).getDefaultTracker()
                        .send(new HitBuilders.EventBuilder()
                                .setCategory("Receiver")
                                .setAction("Screen look")
                                .build());
                break;
            case Intent.ACTION_SCREEN_OFF:
                dayLook.setScreenoff(dayLook.getScreenoff() + 1);
                break;
            case Intent.ACTION_USER_PRESENT:
                dayLook.setScreenunlock(dayLook.getScreenunlock() + 1);
                ((AnalyticsApplication) context.getApplicationContext()).getDefaultTracker()
                        .send(new HitBuilders.EventBuilder()
                                .setCategory("Receiver")
                                .setAction("Screen unlock")
                                .build());
                break;
            default:
                break;
        }

        final DayLook dayToInsert = dayLook;
        new Thread() {
            @Override
            public void run() {
                Log.d(C.TAG, "Inserting new values into the database.");

                LookCountDbManager.getManager(context).insertDayLook(dayToInsert);

            }
        }.start();
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (context == null || intent == null) {
            return;
        }

        increaseScreenLookCountForTheDay(context, intent);
    }
}
