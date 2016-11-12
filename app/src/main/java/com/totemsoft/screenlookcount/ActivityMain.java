package com.totemsoft.screenlookcount;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.totemsoft.screenlookcount.background.UpdateService;
import com.totemsoft.screenlookcount.db.LookCountDbManager;
import com.totemsoft.screenlookcount.db.OnDatabaseListener;
import com.totemsoft.screenlookcount.fragment.FragmentAbout;
import com.totemsoft.screenlookcount.fragment.FragmentMain;
import com.totemsoft.screenlookcount.utils.C;
import com.totemsoft.screenlookcount.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This is the main activity which acts as a holder for {@link FragmentMain} and {@link FragmentAbout} fragments.
 * Handles Toolbar options as well.
 *
 * @author antonina.tkachuk
 */
public class ActivityMain extends AppCompatActivity {
    @Bind(R.id.sv_container)
    ScrollView fragmentContainer;
    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Utils.addFragment(this, FragmentMain.class, C.FRAGMENT_TAG_MAIN, fragmentContainer, false, null);
        saveFirstRunDate();
        tracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // start the service and let it work ever after (with a "sticky" flag)
        startService(new Intent(this, UpdateService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_data:
                clearDataAction();
                return true;
            case R.id.action_share:
                shareWithFriendsAction();
                return true;
            case R.id.action_rate_app:
                rateTheAppActionAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.b_about)
    public void showAbout() {
        if (Utils.isFragmentVisible(this, C.FRAGMENT_TAG_MAIN)) {
            Utils.addFragment(this, FragmentAbout.class, C.FRAGMENT_TAG_ABOUT, fragmentContainer.getId(), true, null);
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void saveFirstRunDate() {
        if (Utils.getPreference(this, C.PREFERENCE_FIRST_RUN_DATE) == 0) {
            Utils.savePreference(this, C.PREFERENCE_FIRST_RUN_DATE, Calendar.getInstance().getTimeInMillis());
        }
    }

    private void showClearAllDataUndoSnackbar() {
        Snackbar undoSnackbar = Snackbar.make(fragmentContainer, getString(R.string.snack_bar_clear_text), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.snack_bar_clear_action_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LookCountDbManager.getManager(ActivityMain.this).setDbClearedFlag(false);
                        updateMainView();
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Buttons")
                                .setAction("UNDO")
                                .build());
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            clearAllData();
                        }
                    }
                });

        // styling
        undoSnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.snack_bar_action));
        View snackbarView = undoSnackbar.getView();
        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(ContextCompat.getColor(this, R.color.snack_bar_text));
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snack_bar_background));

        undoSnackbar.show();
    }

    private void clearAllData() {
        LookCountDbManager.getManager(this).setDbListener(new ClearDBListener(this));
        LookCountDbManager.getManager(this).dropDatabase();
    }

    private void updateMainView() {
        Fragment mainFragment = Utils.getFragmentByTag(this, C.FRAGMENT_TAG_MAIN);
        if (mainFragment instanceof FragmentMain) {
            ((FragmentMain) mainFragment).updateViewAfterDBOperation();
        }
    }

    private void clearDataAction() {
        LookCountDbManager.getManager(this).setDbClearedFlag(true);
        updateMainView();
        showClearAllDataUndoSnackbar();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Options")
                .setAction("Clear all Data")
                .build());
    }

    private void rateTheAppActionAction() {
        final String appPackageName = getPackageName();
        try {
            final Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.rate_google_play_uri, appPackageName)));
            startActivity(googlePlayIntent);
        } catch (ActivityNotFoundException exception) {
            final Intent googlePlayWebPageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.rate_google_play_uri_browser, appPackageName)));
            startActivity(googlePlayWebPageIntent);
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Options")
                .setAction("Rate the app")
                .build());
    }

    private void shareWithFriendsAction() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text, getPackageName()));
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_chooser)));
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Options")
                .setAction("Share with Friends")
                .build());
    }

    private static final class ClearDBListener implements OnDatabaseListener {
        private final WeakReference context;

        private ClearDBListener(FragmentActivity context) {
            this.context = new WeakReference<>(context);
        }

        @Override
        public void onDatabaseDataCleared() {
            FragmentActivity activity = (FragmentActivity) this.context.get();
            if (activity != null && activity instanceof ActivityMain) {
                ((ActivityMain) activity).updateMainView();
            }
        }
    }
}