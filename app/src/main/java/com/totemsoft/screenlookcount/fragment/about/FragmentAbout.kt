package com.totemsoft.screenlookcount.fragment.about

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import com.totemsoft.screenlookcount.BaseFragment
import com.totemsoft.screenlookcount.R
import com.totemsoft.screenlookcount.utils.formatHtmlCompat
import kotlinx.android.synthetic.main.fragment_about.view.*

/**
 * Fragment which holds info about app's functionality and the app's author.
 * Contains a Contact button as well.
 *
 * @author Antonina
 */
class FragmentAbout : BaseFragment(), AboutContract.View {

    private lateinit var presenter: AboutPresenter

    override fun getContentResource() = R.layout.fragment_about

    override fun init(view: View, state: Bundle?) {
        presenter = AboutPresenter()
        presenter.attach(this)

        view.tv_about_text.text = (getString(R.string.about_text)).formatHtmlCompat()
        view.tv_about_looks.text = (getString(R.string.about_looks)).formatHtmlCompat()
        view.tv_about_unlocks.text = (getString(R.string.about_unlocks)).formatHtmlCompat()
        view.tv_about_me.text = (getString(R.string.about_me)).formatHtmlCompat()
        view.tv_about_source_code.movementMethod = LinkMovementMethod.getInstance()
        view.tv_about_source_code.text = (getString(R.string.about_source_code)).formatHtmlCompat()
        view.b_contact_me.setOnClickListener {
            context?.let {
                if (presenter.isAttached()) {
                    presenter.contactMe(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detach()
    }
}