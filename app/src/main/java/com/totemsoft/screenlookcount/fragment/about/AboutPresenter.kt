package com.totemsoft.screenlookcount.fragment.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.totemsoft.screenlookcount.BasePresenter
import com.totemsoft.screenlookcount.R

/**
 * Presenter for About fragment
 *
 * @author Antonina
 */
class AboutPresenter : BasePresenter<AboutContract.View>(), AboutContract.Presenter {

    override fun contactMe(context: Context) {
        val contactIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", context.getString(R.string.about_email_to), null))
        contactIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.about_email_subject))
        context.startActivity(Intent.createChooser(contactIntent, context.getString(R.string.email_chooser)))
    }
}