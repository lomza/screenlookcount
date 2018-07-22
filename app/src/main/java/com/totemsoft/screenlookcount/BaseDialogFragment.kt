package com.totemsoft.screenlookcount

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.totemsoft.screenlookcount.utils.formatWithTimePattern
import java.text.SimpleDateFormat
import java.util.*

/**
 * Base fragment to use by all dialog fragments with calendars.
 *
 * @author Antonina
 */
abstract class BaseDialogFragment : DialogFragment(), BaseView {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setTitle(Calendar.getInstance())

        val view = inflater.inflate(getContentResource(), container, false)
        init(view, savedInstanceState)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Compatibility styling
        dialog?.let {
            val dividerId = it.context.resources
                    .getIdentifier("android:id/titleDivider", null, null)
            val divider = it.findViewById<View>(dividerId)
            if (divider != null) {
                divider.background = null
            }
        }
    }

    fun setTitle(titleMonth: Calendar) {
        dialog?.setTitle(SimpleDateFormat().formatWithTimePattern(titleMonth))
    }
}