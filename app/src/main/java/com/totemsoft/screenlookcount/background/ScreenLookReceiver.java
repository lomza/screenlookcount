package com.totemsoft.screenlookcount.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

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
    private void increaseScreenLookCountForTheDay(@NonNull final Context context, @NonNull final Intent intent) {
        final String action = intent.getAction();
        Log.d(C.TAG, "Action: " + action);

        new Thread() {
            @Override
            public void run() {
                final String nowDateString = Utils.dateToString(new Date());

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
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        dayLook.setScreenoff(dayLook.getScreenoff() + 1);
                        break;
                    case Intent.ACTION_USER_PRESENT:
                        dayLook.setScreenunlock(dayLook.getScreenunlock() + 1);
                        break;
                    default:
                        break;
                }

                Log.d(C.TAG, "Inserting new values into the database.");
                LookCountDbManager.getManager(context).insertDayLook(dayLook);

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
