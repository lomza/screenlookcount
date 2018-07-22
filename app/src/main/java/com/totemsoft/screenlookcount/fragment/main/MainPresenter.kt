package com.totemsoft.screenlookcount.fragment.main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.totemsoft.screenlookcount.BasePresenter
import com.totemsoft.screenlookcount.db.ScreenCounterDb
import com.totemsoft.screenlookcount.fragment.calendar.FragmentCalendar
import com.totemsoft.screenlookcount.utils.C
import com.totemsoft.screenlookcount.utils.showDialog
import com.totemsoft.screenlookcount.utils.toPatternString
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Presenter for main (home) fragment
 *
 * @author Antonina
 */
class MainPresenter : BasePresenter<MainContract.View>(), MainContract.Presenter {
    override fun showCalendarWithLooks(context: Context) {
        val bundle = Bundle()
        bundle.putString(C.DIALOG_TAG_STAT_CAT, C.BUNDLE_ARG_SCREEN_LOOK)
        (context as FragmentActivity).showDialog(FragmentCalendar::class.java, C.DIALOG_TAG_STAT, bundle)
    }

    override fun showCalendarWithUnlocks(context: Context) {
        val bundle = Bundle()
        bundle.putString(C.DIALOG_TAG_STAT_CAT, C.BUNDLE_ARG_SCREEN_UNLOCK)
        (context as FragmentActivity).showDialog(FragmentCalendar::class.java, C.DIALOG_TAG_STAT, bundle)
    }

    /**
     * Sets screen view and screen unlock numbers, taken from the database.
     */
    fun setCountersValues(context: Context) {
        val nowDateString = Date().toPatternString(context)
        ScreenCounterDb.getDatabase(context as FragmentActivity)
                .getDayLook(nowDateString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy (
                        onSuccess = {
                            getCurrentView()?.setCountersView(it.screenon, it.screenunlock)
                        },
                        onComplete = {
                            getCurrentView()?.setCountersView(0, 0)
                        },
                        onError = {
                            Log.e(C.TAG, "setCountersValues() error: ${it.message}")
                        }
                )
    }
}