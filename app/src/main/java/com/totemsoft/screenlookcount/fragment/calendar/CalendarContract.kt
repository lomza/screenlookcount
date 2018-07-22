package com.totemsoft.screenlookcount.fragment.calendar

import android.content.Context
import com.totemsoft.screenlookcount.BaseMvpPresenter
import com.totemsoft.screenlookcount.BaseView
import com.totemsoft.screenlookcount.db.DayLookEntity
import com.totemsoft.screenlookcount.utils.ScreenLookCategory
import io.reactivex.Maybe
import java.util.*

/**
 * View - Presenter contract for Calendar fragment
 *
 * @author Antonina
 */
interface CalendarContract {
    interface Presenter : BaseMvpPresenter<CalendarContract.View> {
        fun populateList(context: Context?, date: Calendar, screenLookCategory: ScreenLookCategory)
    }

    interface View : BaseView {
        fun setViewTitle(titleMonth: Calendar)

        fun setWeekLabels(weekLabels: List<String>)

        fun setDatesToCalendar(dates: List<Pair<Calendar, Int?>>)
    }
}