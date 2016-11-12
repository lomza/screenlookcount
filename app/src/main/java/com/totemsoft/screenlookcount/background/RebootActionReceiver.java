package com.totemsoft.screenlookcount.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.totemsoft.screenlookcount.utils.C;

/**
 * Listens to ACTION_BOOT_COMPLETED event.
 *
 * @author antonina.tkachuk
 */
public class RebootActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }

        String action = intent.getAction();
        Log.d(C.TAG, "Boot completed action received.");

        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // start the service and let it work ever after (with a "sticky" flag)
            context.startService(new Intent(context, UpdateService.class));
        }
    }
}
