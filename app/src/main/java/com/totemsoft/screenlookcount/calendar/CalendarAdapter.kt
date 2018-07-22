package com.totemsoft.screenlookcount.calendar

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import com.totemsoft.screenlookcount.R
import com.totemsoft.screenlookcount.utils.ScreenLookCategory
import com.totemsoft.screenlookcount.utils.setCompatTextAppearance
import com.totemsoft.screenlookcount.utils.toPatternString
import kotlinx.android.synthetic.main.view_cell.view.*
import java.util.*

/**
 * Custom Calendar adapter which holds either screen look or screen unlock values per day.
 *
 * @author Antonina
 */
private const val SWIPE_MIN_DISTANCE = 120
private const val SWIPE_MAX_OFF_PATH = 250
private const val SWIPE_THRESHOLD_VELOCITY = 200

class CalendarAdapter(val context: Context?, val currentMonth: Calendar, val swipeListener: CalendarSwipeListener,
                      private var screenLookCategory: ScreenLookCategory = ScreenLookCategory.SCREEN_LOOK)
    : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    private var dates =  mutableListOf<Pair<Calendar, Int?>>()
    private val todayDate: Calendar = Calendar.getInstance()
    var gestureListener: CalendarGestureListener = CalendarGestureListener()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_cell, null)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dayItem = dates[position]
        with(holder) {
            dayItem.first.let {
                date.text = it.get(Calendar.DAY_OF_MONTH).toString()
                if (dayItem.second != null) {
                    lookCount.text = dayItem.second.toString()
                } else if (DateUtils.isToday(it.timeInMillis)) {
                    lookCount.text = "0"
                }

                setTextStyle(this, dayItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val day: FrameLayout = view.fl_day
        val date: TextView = view.tv_date
        val lookCount: TextView = view.tv_look_count
    }

    private fun setTextStyle(holder: ViewHolder, dayItem: Pair<Calendar, Int?>) {
        dayItem.first.let {
            val textStyleCellCount: Int
            val textStyleCellDate: Int
            val bgRes: Int

            if (currentMonth.get(Calendar.MONTH) == it.get(Calendar.MONTH)) {
                if (todayDate.time.toPatternString(context) == it.time.toPatternString(context)) {
                    textStyleCellCount = if (screenLookCategory === ScreenLookCategory.SCREEN_LOOK) R.style.CellCountToday else R.style.CellCountTodaySecondary
                    bgRes = R.drawable.view_cell_today
                } else {
                    textStyleCellCount = if (screenLookCategory === ScreenLookCategory.SCREEN_LOOK) R.style.CellCountSelectedMonth else R.style.CellCountSelectedMonthSecondary
                    bgRes = R.drawable.view_cell_selected_month
                }
                textStyleCellDate = if (screenLookCategory === ScreenLookCategory.SCREEN_LOOK) R.style.CellDateSelectedMonth else R.style.CellDateSelectedMonthSecondary
            } else {
                textStyleCellCount = if (screenLookCategory === ScreenLookCategory.SCREEN_LOOK) R.style.CellCount else R.style.CellCountSecondary
                textStyleCellDate = if (screenLookCategory === ScreenLookCategory.SCREEN_LOOK) R.style.CellDate else R.style.CellDateSecondary
                bgRes = R.drawable.view_cell
            }

            holder.lookCount.setCompatTextAppearance(context, textStyleCellCount)
            holder.date.setCompatTextAppearance(context, textStyleCellDate)
            holder.day.setBackgroundResource(bgRes)
        }
    }

    fun setDatesList(dates: List<Pair<Calendar, Int?>>) {
        this.dates = dates.toMutableList()
        notifyDataSetChanged()
    }

    inner class CalendarGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (Math.abs(e1.x - e2.x) > SWIPE_MAX_OFF_PATH) {
                // left-right/right-left swipe is not supported
                return false
            }

            // down to up swipe
            if (e1.y - e2.y > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                currentMonth.add(Calendar.MONTH, 1)
                swipeListener.onCalendarSwipe(currentMonth)
            } else if (e2.y - e1.y > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                currentMonth.add(Calendar.MONTH, -1)
                swipeListener.onCalendarSwipe(currentMonth)
            }// up to down swipe

            return true
        }
    }
}