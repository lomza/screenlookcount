package com.totemsoft.screenlookcount.fragment.about

import android.content.Context
import com.totemsoft.screenlookcount.BaseMvpPresenter
import com.totemsoft.screenlookcount.BaseView

/**
 * View - Presenter contract for About fragment
 *
 * @author Antonina
 */
interface AboutContract {
    interface Presenter : BaseMvpPresenter<AboutContract.View> {
        fun contactMe(context: Context)
    }

    interface View : BaseView
}