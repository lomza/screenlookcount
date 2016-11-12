package com.totemsoft.screenlookcount.background;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.totemsoft.screenlookcount.utils.C;

/**
 * Service for updating and catching SCREEN_ON, SCREEN_OFF, and USER_PRESENT events.
 *
 * @author antonina.tkachuk
 */
public class UpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(C.TAG, "Update service onCreate()");

        registerScreenLookReceiver();
    }

    @Override
    public void onDestroy() {
        Log.d(C.TAG, "Update service onDestroy()");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void registerScreenLookReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        BroadcastReceiver receiver = new ScreenLookReceiver();
        registerReceiver(receiver, filter);
    }
}
