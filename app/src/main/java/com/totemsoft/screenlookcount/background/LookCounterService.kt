package com.totemsoft.screenlookcount.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.totemsoft.screenlookcount.ActivityMain
import com.totemsoft.screenlookcount.R

/**
 * Service which starts a receiver for catching SCREEN_ON, SCREEN_OFF, and USER_PRESENT events.
 *
 * @author Antonina
 */
class LookCounterService : Service() {

    private val SERVICE_ID = 1
    private lateinit var screenLookReceiver: ScreenLookReceiver

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        registerScreenLookReceiver()
    }

    override fun onDestroy() {
        unregisterReceiver(screenLookReceiver)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundServiceWithNotification()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun registerScreenLookReceiver() {
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        with(filter) {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }

        screenLookReceiver = ScreenLookReceiver()
        registerReceiver(screenLookReceiver, filter)
    }

    private fun startForegroundServiceWithNotification() {
        val intent = Intent(this, ActivityMain::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.notification_channel_screen_looks)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channelId = "LOOK_COUNTS_CHANNEL_ID"
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.setShowBadge(false)

            builder = NotificationCompat.Builder(this, channelId)

            val notificatioManager = getSystemService(NotificationManager::class.java)
            notificatioManager.createNotificationChannel(channel)
        } else {
            builder = NotificationCompat.Builder(this)
        }

        val icon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) R.mipmap.ic_notification else R.mipmap.ic_launcher

        with(builder) {
            setContentTitle(getString(R.string.app_name))
            setContentText(getString(R.string.notification_service_description))
            setWhen(System.currentTimeMillis())
            setSmallIcon(icon)
            setContentIntent(pendingIntent)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setVisibility(NotificationCompat.VISIBILITY_SECRET)
        }

        val notification = builder.build()

        startForeground(SERVICE_ID, notification)
    }
}
