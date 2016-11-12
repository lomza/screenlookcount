package com.totemsoft.screenlookcount.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.totemsoft.screenlookcount.AnalyticsApplication;
import com.totemsoft.screenlookcount.R;
import com.totemsoft.screenlookcount.calendar.CalendarAdapter;
import com.totemsoft.screenlookcount.calendar.CalendarSwipeListener;
import com.totemsoft.screenlookcount.utils.C;
import com.totemsoft.screenlookcount.utils.ScreenLookCategory;
import com.totemsoft.screenlookcount.utils.Utils;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Shows a calendar grid of days with screen look and unlock counts.
 *
 * @author antonina.tkachuk
 */
public class FragmentCalendar extends DialogFragment implements CalendarSwipeListener {

    @Bind(R.id.calendarDayLabel1)
    TextView day1LabelTextView;

    @Bind(R.id.calendarDayLabel2)
    TextView day2LabelTextView;

    @Bind(R.id.calendarDayLabel3)
    TextView day3LabelTextView;

    @Bind(R.id.calendarDayLabel4)
    TextView day4LabelTextView;

    @Bind(R.id.calendarDayLabel5)
    TextView day5LabelTextView;

    @Bind(R.id.calendarDayLabel6)
    TextView day6LabelTextView;

    @Bind(R.id.calendarDayLabel7)
    TextView day7LabelTextView;

    @Bind(R.id.calendarDaysGridView)
    RecyclerView gridView;

    private CalendarAdapter adapter;
    private Context context;
    private int firstDayOfTheWeekSetting;
    private ScreenLookCategory screenLookCategory;
    private final LinkedList<TextView> weekLabels = new LinkedList<>();
    private Bundle bundle;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        Tracker tracker = ((AnalyticsApplication) context.getApplicationContext()).getDefaultTracker();

        // Compatibility styling
        Dialog dialog = getDialog();
        if (dialog != null) {
            final int dividerId = dialog.getContext().getResources()
                    .getIdentifier("android:id/titleDivider", null, null);
            View divider = dialog.findViewById(dividerId);
            if (divider != null) {
                divider.setBackground(null);
            }
        }

        // Styling based on Dialog category
        if (bundle != null && bundle.containsKey(C.DIALOG_TAG_STAT_CAT)) {
            final String category = bundle.getString(C.DIALOG_TAG_STAT_CAT);
            if (category != null) {
                switch (category) {
                    case C.BUNDLE_ARG_SCREEN_LOOK:
                        screenLookCategory = ScreenLookCategory.SCREEN_LOOK;
                        setStyle(STYLE_NORMAL, R.style.DialogMainWithTitle);
                        adapter = new CalendarAdapter(context, getCalendarDays(Calendar.getInstance()), Calendar.getInstance(), this, ScreenLookCategory.SCREEN_LOOK);
                        tracker.setScreenName("Calendar Looks");
                        break;

                    case C.BUNDLE_ARG_SCREEN_UNLOCK:
                        screenLookCategory = ScreenLookCategory.SCREEN_UNLOCK;
                        setStyle(STYLE_NORMAL, R.style.DialogSecondaryWithTitle);
                        adapter = new CalendarAdapter(context, getCalendarDays(Calendar.getInstance()), Calendar.getInstance(), this, ScreenLookCategory.SCREEN_UNLOCK);
                        tracker.setScreenName("Calendar Unlocks");
                        break;

                    default:
                        screenLookCategory = ScreenLookCategory.SCREEN_LOOK;
                        adapter = new CalendarAdapter(context, getCalendarDays(Calendar.getInstance()), Calendar.getInstance(), this);
                        break;
                }
            }
        }

        gridView.setHasFixedSize(true);
        gridView.setLayoutManager(new GridLayoutManager(context, 7));
        gridView.setAdapter(adapter);
        // add swipe gesture detection to RecyclerView
        final GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(context, adapter.getGestureListener());
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetectorCompat.onTouchEvent(event);
                return true;
            }
        });

        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setTitle(Calendar.getInstance());
        firstDayOfTheWeekSetting = Utils.getFirstDayOfTheWeek(getActivity());

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);
        init();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.DialogSecondaryWithTitle);

        // IMPORTANT: Dialog style MUST be set in onCreate()
        bundle = getArguments();
        if (bundle != null && bundle.containsKey(C.DIALOG_TAG_STAT_CAT)) {
            final String category = bundle.getString(C.DIALOG_TAG_STAT_CAT);
            if (category != null) {
                if (category.equals(C.BUNDLE_ARG_SCREEN_LOOK)) {
                    setStyle(STYLE_NORMAL, R.style.DialogMainWithTitle);
                } else {
                    setStyle(STYLE_NORMAL, R.style.DialogSecondaryWithTitle);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void init() {
        weekLabels.add(day1LabelTextView);
        weekLabels.add(day2LabelTextView);
        weekLabels.add(day3LabelTextView);
        weekLabels.add(day4LabelTextView);
        weekLabels.add(day5LabelTextView);
        weekLabels.add(day6LabelTextView);
        weekLabels.add(day7LabelTextView);
    }

    private void setTitle(@NonNull final Calendar month) {
        getDialog().setTitle(Utils.getDialogTitle(month));
    }

    private List<Pair<Calendar, Integer>> getCalendarDays(@NonNull final Calendar currentMonth) {
        LinkedList<Pair<Calendar, Integer>> toShowDays = new LinkedList<>();
        LinkedList<Pair<Calendar, Integer>> monthNow = new LinkedList<>();

        // NOW
        Calendar monthNowCalendar = (Calendar) currentMonth.clone();
        monthNowCalendar.set(Calendar.DATE, 1);
        final int monthNowMax = monthNowCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= monthNowMax; i++) {
            monthNowCalendar = (Calendar) currentMonth.clone();
            monthNowCalendar.set(Calendar.DATE, i);
            monthNow.add(Pair.create(monthNowCalendar, Utils.getScreenLooksByDay(context, monthNowCalendar.getTime(), screenLookCategory)));
        }

        // add the days to fill all cells at the beginning
        final int daysInWeek = 7;
        final int firstDayOfCurrentMonth = monthNow.get(0).first.get(Calendar.DAY_OF_WEEK);
        int daysToFill;
        if (firstDayOfCurrentMonth != firstDayOfTheWeekSetting) {
            daysToFill = Math.abs(firstDayOfCurrentMonth - firstDayOfTheWeekSetting);
            final Calendar firstDayOfMonth = monthNow.getFirst().first;
            for (int i = daysToFill; i > 0; i--) {
                Calendar dayBefore = (Calendar) firstDayOfMonth.clone();
                dayBefore.add(Calendar.DAY_OF_YEAR, -i);
                toShowDays.add(Pair.create(dayBefore, Utils.getScreenLooksByDay(context, dayBefore.getTime(), screenLookCategory)));
            }
        }

        // add current month
        toShowDays.addAll(monthNow);

        // add the rest of the days to fill all cells
        daysToFill = daysInWeek - (toShowDays.size() % daysInWeek);
        if (daysToFill > 0 && daysToFill < daysInWeek) {
            final Calendar lastDayOfMonth = monthNow.getLast().first;
            for (int i = 1; i <= daysToFill; i++) {
                Calendar dayAfter = (Calendar) lastDayOfMonth.clone();
                dayAfter.add(Calendar.DAY_OF_YEAR, i);
                toShowDays.add(Pair.create(dayAfter, Utils.getScreenLooksByDay(context, dayAfter.getTime(), screenLookCategory)));
            }
        }

        // set labels for the week days
        Calendar weekDay = Calendar.getInstance();
        for (int i = 0; i < daysInWeek; i++) {
            weekDay.set(Calendar.DAY_OF_WEEK, firstDayOfTheWeekSetting);
            weekDay.add(Calendar.DAY_OF_WEEK, i);
            weekLabels.get(i).setText(weekDay.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
        }

        return toShowDays;
    }

    @Override
    public void onCalendarSwipe(Calendar currentMoth) {
        setTitle(currentMoth);
        adapter.setDatesList(getCalendarDays(currentMoth));
    }
}