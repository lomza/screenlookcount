package com.totemsoft.screenlookcount.fragment.main

import android.content.Context
import com.totemsoft.screenlookcount.BaseMvpPresenter
import com.totemsoft.screenlookcount.BaseView

/**
 * View - Presenter contract for About fragment
 *
 * @author Antonina
 */
interface MainContract {
    interface Presenter : BaseMvpPresenter<MainContract.View> {
        fun showCalendarWithLooks(context: Context)
        fun showCalendarWithUnlocks(context: Context)
    }

    interface View : BaseView {
        fun setCountersView(looks: Int?, unlocks: Int?)
    }
}