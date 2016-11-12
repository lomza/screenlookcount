package com.totemsoft.screenlookcount.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.totemsoft.screenlookcount.R;
import com.totemsoft.screenlookcount.utils.C;
import com.totemsoft.screenlookcount.utils.ScreenLookCategory;
import com.totemsoft.screenlookcount.utils.Utils;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Custom Calendar adapter with either screen look or screen unlock values per day.
 *
 * @author antonina.tkachuk
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private List<Pair<Calendar, Integer>> datesList;
    private Calendar todayDate;
    private Context context;
    private Calendar currentMonth;
    private CalendarSwipeListener swipeListener;
    private CalendarGestureListener gestureListener;
    private ScreenLookCategory screenLookCategory;

    public CalendarAdapter(@NonNull final Context context, @NonNull final List<Pair<Calendar, Integer>> dates, @NonNull final Calendar currentMonth, CalendarSwipeListener swipeListener) {
        new CalendarAdapter(context, dates, currentMonth, swipeListener, ScreenLookCategory.SCREEN_LOOK);
    }

    public CalendarAdapter(@NonNull final Context context, @NonNull final List<Pair<Calendar, Integer>> dates, @NonNull final Calendar currentMonth, CalendarSwipeListener swipeListener, ScreenLookCategory screenLookCategory) {
        this.context = context;
        this.datesList = dates;
        this.todayDate = Calendar.getInstance();
        this.currentMonth = currentMonth;
        this.swipeListener = swipeListener;
        this.gestureListener = new CalendarGestureListener();
        this.screenLookCategory = screenLookCategory;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_cell, null);

        return new ViewHolder(view);
    }

    @Override
    @Deprecated
    public void onBindViewHolder(ViewHolder holder, int position) {
        final long today = Calendar.getInstance().getTimeInMillis();
        final Pair<Calendar, Integer> dayItem = datesList.get(position);
        holder.tvDate.setText(String.valueOf(dayItem.first.get(Calendar.DAY_OF_MONTH)));
        if (dayItem.second != null) {
            holder.tvLookCount.setText(String.valueOf(dayItem.second));
        } else if (dayItem.first.getTimeInMillis() > Utils.getPreference(context, C.PREFERENCE_FIRST_RUN_DATE) && dayItem.first.getTimeInMillis() < today) {
            holder.tvLookCount.setText("0");
        }

        setTextStyle(holder, dayItem);
    }

    private void setTextStyle(@NonNull final ViewHolder holder, @NonNull final Pair<Calendar, Integer> dayItem) {
        int textStyleCellCount, textStyleCellDate, bgRes;
        if (currentMonth.get(Calendar.MONTH) == dayItem.first.get(Calendar.MONTH)) {
            if (Utils.dateToString(todayDate.getTime()).equals(Utils.dateToString(dayItem.first.getTime()))) {
                textStyleCellCount = screenLookCategory == ScreenLookCategory.SCREEN_LOOK ? R.style.CellCountToday : R.style.CellCountTodaySecondary;
                bgRes = R.drawable.view_cell_today;
            } else {
                textStyleCellCount = screenLookCategory == ScreenLookCategory.SCREEN_LOOK ? R.style.CellCountSelectedMonth : R.style.CellCountSelectedMonthSecondary;
                bgRes = R.drawable.view_cell_selected_month;
            }
            textStyleCellDate = screenLookCategory == ScreenLookCategory.SCREEN_LOOK ? R.style.CellDateSelectedMonth : R.style.CellDateSelectedMonthSecondary;
        } else {
            textStyleCellCount = screenLookCategory == ScreenLookCategory.SCREEN_LOOK ? R.style.CellCount : R.style.CellCountSecondary;
            textStyleCellDate = screenLookCategory == ScreenLookCategory.SCREEN_LOOK ? R.style.CellDate : R.style.CellDateSecondary;
            bgRes = R.drawable.view_cell;
        }

        Utils.setTextAppearance(context, holder.tvLookCount, textStyleCellCount);
        Utils.setTextAppearance(context, holder.tvDate, textStyleCellDate);
        holder.rlDay.setBackgroundResource(bgRes);
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rl_day)
        RelativeLayout rlDay;

        @Bind(R.id.tv_date)
        TextView tvDate;

        @Bind(R.id.tv_look_count)
        TextView tvLookCount;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public CalendarGestureListener getGestureListener() {
        if (gestureListener == null)
            gestureListener = new CalendarGestureListener();

        return gestureListener;
    }

    public void setDatesList(final List<Pair<Calendar, Integer>> dates) {
        datesList = dates;
        notifyDataSetChanged();
    }

    private class CalendarGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH) {
                // left-right/right-left swipe is not supported
                return false;
            }

            // down to up swipe
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                currentMonth.add(Calendar.MONTH, 1);
                swipeListener.onCalendarSwipe(currentMonth);
            }
            // up to down swipe
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                currentMonth.add(Calendar.MONTH, -1);
                swipeListener.onCalendarSwipe(currentMonth);
            }


            return true;
        }
    }
}