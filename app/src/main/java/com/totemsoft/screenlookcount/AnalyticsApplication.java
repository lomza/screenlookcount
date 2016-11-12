package com.totemsoft.screenlookcount;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class AnalyticsApplication extends Application {
    private Tracker tracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(R.xml.global_tracker); // NOTICE: use your version of google-services.json in order to generate global_tracker.xml
            GoogleAnalytics.getInstance(this).setDryRun(BuildConfig.GOOGLE_ANALYTICS_DRY_RUN);
        }
        return tracker;
    }
}