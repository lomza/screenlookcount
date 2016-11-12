package com.totemsoft.screenlookcount.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.totemsoft.screenlookcount.AnalyticsApplication;
import com.totemsoft.screenlookcount.R;
import com.totemsoft.screenlookcount.db.DayLook;
import com.totemsoft.screenlookcount.db.LookCountDbManager;
import com.totemsoft.screenlookcount.utils.C;
import com.totemsoft.screenlookcount.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Contains screen view and screen unlock data.
 *
 * @author antonina.tkachuk
 */
public class FragmentMain extends Fragment {
    @Bind(R.id.b_main_look)
    Button lookCountButton;
    @Bind(R.id.b_unlock_look)
    Button unlockCountButton;

    private FragmentActivity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (FragmentActivity) getContext();
        Tracker tracker = ((AnalyticsApplication) context.getApplication()).getDefaultTracker();
        tracker.setScreenName("Main");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onResume() {
        super.onResume();

        setCountersValues();
    }

    @OnClick(R.id.b_main_look)
    public void showScreenLookCount() {
        Bundle bundle = new Bundle();
        bundle.putString(C.DIALOG_TAG_STAT_CAT, C.BUNDLE_ARG_SCREEN_LOOK);
        Utils.showDialog(context, FragmentCalendar.class, C.DIALOG_TAG_STAT, bundle);
    }

    @OnClick(R.id.b_unlock_look)
    public void showScreenUnlockCount() {
        Bundle bundle = new Bundle();
        bundle.putString(C.DIALOG_TAG_STAT_CAT, C.BUNDLE_ARG_SCREEN_UNLOCK);
        Utils.showDialog(context, FragmentCalendar.class, C.DIALOG_TAG_STAT, bundle);
    }

    /**
     * Sets screen view and screen unlock numbers, taken from the database.
     */
    private void setCountersValues() {
        final DayLook dayLook = LookCountDbManager.getManager(context).getDayLookByDate(Utils.getTodayDateAsString());
        int lookCount = 0;
        int unlockCount = 0;
        if (dayLook != null) {
            if (dayLook.getScreenon() != null)
                lookCount = dayLook.getScreenon();

            if (dayLook.getScreenunlock() != null)
                unlockCount = dayLook.getScreenunlock();
        }

        lookCountButton.setText(String.valueOf(lookCount));
        unlockCountButton.setText(String.valueOf(unlockCount));
    }

    public void updateViewAfterDBOperation() {
        setCountersValues();
    }
}