package com.totemsoft.screenlookcount.utils

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.telephony.TelephonyManager
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Container for helper top-level functions.
 *
 * @author Antonina
 */
private const val DATE_PATTERN = "dd-MM-yyyy"
private const val DATE_PATTERN_DIALOG_TITLE = "MMMM, yyyy"

fun SimpleDateFormat.formatWithTimePattern(date: Calendar): String {
    applyLocalizedPattern(DATE_PATTERN_DIALOG_TITLE)
    return format(date.time)
}

fun Date.toPatternString(context: Context?): String {
    val dateTimeFormatter = SimpleDateFormat(DATE_PATTERN, context?.getCurrentLocale() ?: Locale.getDefault())
    return dateTimeFormatter.format(this)
}

fun String.toCalendar(context: Context?): Calendar {
    val date = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(DATE_PATTERN, context?.getCurrentLocale() ?: Locale.getDefault())
    date.time = dateFormat.parse(this)

    return date
}

fun FragmentActivity.isFragmentInBackStack(tag: String): Boolean {
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    return fragment != null
}

fun FragmentActivity.isFragmentVisible(tag: String): Boolean {
    val fragment = supportFragmentManager.findFragmentByTag(tag)
    return fragment != null && fragment.isVisible
}

fun FragmentActivity.getFragmentByTag(tag: String): Fragment {
    return supportFragmentManager.findFragmentByTag(tag)
}

fun FragmentActivity.addFragment(clazz: Class<out Fragment>,
                                 tag: String, parentViewId: Int, addToBackStack: Boolean, args: Bundle?) {
    val ft = supportFragmentManager.beginTransaction()
    try {
        val fragment = clazz.newInstance()
        fragment.arguments = args
        ft.replace(parentViewId, fragment, tag)
        if (addToBackStack) {
            ft.addToBackStack(tag)
        }
        ft.commit()

    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun FragmentActivity.addFragment(clazz: Class<out Fragment>,
                                 tag: String, parentView: View, addToBackStack: Boolean, args: Bundle?) {
    addFragment(clazz, tag, parentView.id, addToBackStack, args)
}

fun FragmentActivity.showDialog(clazz: Class<out DialogFragment>, tag: String, args: Bundle) {
    val ft = supportFragmentManager.beginTransaction()
    try {
        val fragment = clazz.newInstance()
        fragment.arguments = args
        ft.add(fragment, tag)
        ft.commit()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.getScreenSize(): IntArray {
    val size = IntArray(2)
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val point = Point()
    display.getSize(point)
    size[0] = point.x
    size[1] = point.y

    return size
}

@Suppress("DEPRECATION")
@TargetApi(Build.VERSION_CODES.N)
fun Context.getCurrentLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales.get(0)
    } else {
        resources.configuration.locale
    }
}

/**
 * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
 *
 * @return country code or null
 */
fun Context.getCurrentCountry(): String? {
    try {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = tm.simCountryIso
        return if (simCountry != null && simCountry.length == 2) { // SIM country code is available
            simCountry.toLowerCase(Locale.US)
        } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
            val networkCountry = tm.networkCountryIso
            if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                networkCountry.toLowerCase(Locale.US)
            } else {
                // get the locale's country of the language set on the device
                getCurrentLocale().isO3Country
            }
        } else {
            // get the locale's country of the language set on the device
            getCurrentLocale().isO3Country
        }
    } catch (e: Exception) {
        Log.e(C.TAG, "Exception getting user's country code. ", e)
    }

    return null
}

@Suppress("DEPRECATION")
@TargetApi(Build.VERSION_CODES.M)
fun TextView.setCompatTextAppearance(context: Context?, styleRes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(styleRes)
    } else {
        context?.let {
            setTextAppearance(it, styleRes)
        }
    }
}

@Suppress("DEPRECATION")
@TargetApi(Build.VERSION_CODES.N)
fun String.formatHtmlCompat(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
    } else {
        Html.fromHtml(this)
    }
}