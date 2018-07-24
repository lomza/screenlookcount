package com.totemsoft.screenlookcount.fragment.calendar

import android.content.Context
import android.util.Log
import com.totemsoft.screenlookcount.BasePresenter
import com.totemsoft.screenlookcount.R
import com.totemsoft.screenlookcount.db.DayLookEntity
import com.totemsoft.screenlookcount.db.ScreenCounterDb
import com.totemsoft.screenlookcount.utils.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Presenter for Calendar fragment
 *
 * @author Antonina
 */
class CalendarPresenter : BasePresenter<CalendarContract.View>(), CalendarContract.Presenter {

    private fun getScreenLooksByDay(context: Context, date: Date): Maybe<DayLookEntity> {
        return ScreenCounterDb.getDatabase(context)
                .getDayLook(date.toPatternString(context))
                .subscribeOn(Schedulers.io())
    }

    private fun getFirstDayOfTheWeek(context: Context?): Int {
        context?.let {
            if (it.resources == null) {
                Log.e(C.TAG, "Context's resources is null in getFirstDayOfTheWeek()")
            } else {
                val country = it.getCurrentCountry()
                if (country != null) {
                    val countriesWithSundayAsFirstDay = it.resources.getStringArray(R.array.countries_with_sun_as_first_day)
                    for (c in countriesWithSundayAsFirstDay) {
                        if (c == country.toLowerCase())
                            return Calendar.SUNDAY
                    }
                } else {
                    return Calendar.MONDAY
                }
            }
        }

        return Calendar.MONDAY
    }

    private fun getCalendarDays(context: Context?, currentMonth: Calendar): List<Pair<Calendar, Date>> {
        context?.let {
            val firstDayOfTheWeekSetting = getFirstDayOfTheWeek(it)

            val toShowDays = LinkedList<Pair<Calendar, Date>>()
            val monthNow = LinkedList<Pair<Calendar, Date>>()

            // NOW
            var monthNowCalendar = currentMonth.clone() as Calendar
            monthNowCalendar.set(Calendar.DATE, 1)
            val monthNowMax = monthNowCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in 1..monthNowMax) {
                monthNowCalendar = currentMonth.clone() as Calendar
                monthNowCalendar.set(Calendar.DATE, i)
                monthNow.add(Pair(monthNowCalendar, monthNowCalendar.time))
            }

            // add the days to fill all cells at the beginning
            val daysInWeek = 7
            val firstDayOfCurrentMonth = monthNow[0].first.get(Calendar.DAY_OF_WEEK)
            var daysToFill: Int
            if (firstDayOfCurrentMonth != firstDayOfTheWeekSetting) {
                daysToFill = if (firstDayOfTheWeekSetting == Calendar.MONDAY && firstDayOfCurrentMonth == Calendar.SUNDAY) {
                    // this is the case when first day of the month is Sunday (1)
                    // and first day of the week is Monday (2)
                    // Monday - Sunday would give us 1 as daysToFill value,
                    // which is wrong, so we need to subtract days in week,
                    // which is 7 from 1 (first day of this month),
                    // and then we'll get 6 as value for days to fill
                    daysInWeek - firstDayOfCurrentMonth
                } else {
                    Math.abs(firstDayOfCurrentMonth - firstDayOfTheWeekSetting)
                }
                val firstDayOfMonth = monthNow.first.first
                for (i in daysToFill downTo 1) {
                    val dayBefore = firstDayOfMonth.clone() as Calendar
                    dayBefore.let {
                        it.add(Calendar.DAY_OF_YEAR, -i)
                        toShowDays.add(Pair(it, dayBefore.time))
                    }
                }
            }

            // add current month
            toShowDays.addAll(monthNow)

            // add the rest of the days to fill all cells
            daysToFill = daysInWeek - toShowDays.size % daysInWeek
            if (daysToFill in 1..(daysInWeek - 1)) {
                val lastDayOfMonth = monthNow.last.first
                for (i in 1..daysToFill) {
                    val dayAfter = lastDayOfMonth.clone() as Calendar
                    dayAfter.let {
                        it.add(Calendar.DAY_OF_YEAR, i)
                        toShowDays.add(Pair(it, dayAfter.time))
                    }
                }
            }

            // set labels for the week days
            val weekDay = Calendar.getInstance()
            val weekLabels = mutableListOf<String>()
            for (i in 0 until daysInWeek) {
                weekDay.set(Calendar.DAY_OF_WEEK, firstDayOfTheWeekSetting)
                weekDay.add(Calendar.DAY_OF_WEEK, i)
                weekLabels.add(weekDay.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, it.getCurrentLocale()))
            }
            getCurrentView()?.setWeekLabels(weekLabels)

            return toShowDays
        }

        return emptyList()
    }

    override fun populateList(context: Context?, date: Calendar, screenLookCategory: ScreenLookCategory) {
        context?.let {
            val lookCountsHashMap = mutableMapOf<Calendar, Int?>()

            Flowable.fromIterable(getCalendarDays(context, date))
                    .subscribeOn(Schedulers.computation())
                    .flatMapMaybe { day ->
                        lookCountsHashMap[day.second.toPatternString(it).toCalendar(it)] = null

                        getScreenLooksByDay(it, day.second)
                                .map { dayWithLooks ->
                                    val screenLooks = if (screenLookCategory === com.totemsoft.screenlookcount.utils.ScreenLookCategory.SCREEN_LOOK) dayWithLooks.screenon else dayWithLooks.screenunlock
                                    lookCountsHashMap[dayWithLooks.date.toCalendar(it)] = screenLooks
                                }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onComplete = {
                                val toShowDaysWithLookCounts = lookCountsHashMap.toList()

                                getCurrentView()?.let {
                                    it.setViewTitle(date)
                                    it.setDatesToCalendar(toShowDaysWithLookCounts)
                                }
                            }
                            , onError = {
                        Log.e(C.TAG, "getCalendarDays() error: ${it.printStackTrace()}")
                    })
        }
    }
}