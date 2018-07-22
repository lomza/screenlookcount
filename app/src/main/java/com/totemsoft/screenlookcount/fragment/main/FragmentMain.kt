package com.totemsoft.screenlookcount.fragment.main

import android.os.Bundle
import android.view.View
import com.totemsoft.screenlookcount.BaseFragment
import com.totemsoft.screenlookcount.R
import kotlinx.android.synthetic.main.fragment_main.view.*

/**
 * Fragment which contains screen view and screen unlock data.
 *
 * @author Antonina
 */
class FragmentMain : BaseFragment(), MainContract.View, View.OnClickListener {

    private lateinit var currentView: View
    private lateinit var presenter: MainPresenter

    override fun getContentResource() = R.layout.fragment_main

    override fun init(view: View, state: Bundle?) {
        currentView = view
        presenter = MainPresenter()
        presenter.attach(this)
        currentView.b_main_look.setOnClickListener(this)
        currentView.b_unlock_look.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            presenter.setCountersValues(it)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.b_main_look -> {
                context?.let {
                    presenter.showCalendarWithLooks(it)
                }
            }
            R.id.b_unlock_look -> {
                context?.let {
                    presenter.showCalendarWithUnlocks(it)
                }
            }
        }
    }

    override fun setCountersView(looks: Int?, unlocks: Int?) {
        currentView.b_main_look.text = (looks ?: 0).toString()
        currentView.b_unlock_look.text = (unlocks ?: 0).toString()
    }

    fun getMainPresenter() = presenter
}