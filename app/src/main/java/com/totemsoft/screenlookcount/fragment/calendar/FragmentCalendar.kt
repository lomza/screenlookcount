package com.totemsoft.screenlookcount.fragment.calendar

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.TextView
import com.totemsoft.screenlookcount.BaseDialogFragment
import com.totemsoft.screenlookcount.R
import com.totemsoft.screenlookcount.calendar.CalendarAdapter
import com.totemsoft.screenlookcount.calendar.CalendarSwipeListener
import com.totemsoft.screenlookcount.utils.C
import com.totemsoft.screenlookcount.utils.ScreenLookCategory
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import java.util.*

/**
 * Fragment which shows a calendar grid of days with screen look and unlock counts.
 *
 * @author Antonina
 */
class FragmentCalendar : BaseDialogFragment(), CalendarContract.View, CalendarSwipeListener {
    private lateinit var adapter: CalendarAdapter
    private var screenLookCategory: ScreenLookCategory = ScreenLookCategory.SCREEN_LOOK
    private val weekLabelTextViews = LinkedList<TextView>()
    private lateinit var currentView: View
    private lateinit var presenter: CalendarPresenter

    override fun getContentResource(): Int {
        return R.layout.fragment_calendar
    }

    override fun init(view: View, state: Bundle?) {
        currentView = view
        presenter = CalendarPresenter()
        presenter.attach(this)

        weekLabelTextViews.addAll(listOf<TextView>(
                currentView.calendarDayLabel1,
                currentView.calendarDayLabel2,
                currentView.calendarDayLabel3,
                currentView.calendarDayLabel4,
                currentView.calendarDayLabel5,
                currentView.calendarDayLabel6,
                currentView.calendarDayLabel7))

        setCalendarView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        styleDialog()
    }

    private fun styleDialog() {
        // IMPORTANT: Dialog style MUST be set in onCreate()
        // Styling based on Dialog category
        arguments?.let {
            val category = it.getString(C.DIALOG_TAG_STAT_CAT, "")
            when (category) {
                C.BUNDLE_ARG_SCREEN_LOOK -> {
                    screenLookCategory = ScreenLookCategory.SCREEN_LOOK
                    setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogMainWithTitle)
                }

                C.BUNDLE_ARG_SCREEN_UNLOCK -> {
                    screenLookCategory = ScreenLookCategory.SCREEN_UNLOCK
                    setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogSecondaryWithTitle)
                }

                else -> {
                    screenLookCategory = ScreenLookCategory.SCREEN_LOOK
                }
            }
        }
    }

    override fun setViewTitle(titleMonth: Calendar) {
        setTitle(titleMonth)
    }

    override fun setDatesToCalendar(dates: List<Pair<Calendar, Int?>>) {
        adapter.setDatesList(dates)
    }

    override fun setWeekLabels(weekLabels: List<String>) {
        if (weekLabelTextViews.size != weekLabels.size) {
            return
        }

        weekLabelTextViews.forEachIndexed { index, label ->
            label.text = weekLabels[index]
        }
    }

    private fun setCalendarView() {
        adapter = CalendarAdapter(context, Calendar.getInstance(), this, screenLookCategory)
        presenter.populateList(context, Calendar.getInstance(), screenLookCategory)

        currentView.calendarDaysRecyclerView.setHasFixedSize(true)
        currentView.calendarDaysRecyclerView.layoutManager = GridLayoutManager(context, 7)
        currentView.calendarDaysRecyclerView.adapter = adapter
        // add swipe gesture detection to RecyclerView
        val gestureDetectorCompat = GestureDetectorCompat(context, adapter.gestureListener)
        currentView.calendarDaysRecyclerView.setOnTouchListener { _, event ->
            gestureDetectorCompat.onTouchEvent(event)
            true
        }
    }

    override fun onCalendarSwipe(destinationMonth: Calendar) {
        presenter.populateList(context, destinationMonth, screenLookCategory)
    }
}