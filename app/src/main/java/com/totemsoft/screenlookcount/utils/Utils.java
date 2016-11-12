package com.totemsoft.screenlookcount.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.totemsoft.screenlookcount.R;
import com.totemsoft.screenlookcount.db.DayLook;
import com.totemsoft.screenlookcount.db.LookCountDbManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This class holds helper methods.
 *
 * @author antonina.tkachuk
 */
public abstract class Utils {
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String DATE_PATTERN_DIALOG_TITLE = "MMMM, yyyy";

    public static String getDialogTitle(@NonNull final Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN_DIALOG_TITLE, Locale.getDefault());
        return dateFormat.format(date.getTime());
    }

    public static int getFirstDayOfTheWeek(@NonNull final Context context) {
        if (context.getResources() == null) {
            Log.e(C.TAG, "Context's resources is null in getFirstDayOfTheWeek()");
        } else {
            final String country = Utils.getUserCountry(context);
            if (country != null) {
                final String[] countriesWithSundayAsFirstDay = context.getResources().getStringArray(R.array.countries_with_sun_as_first_day);
                for (String c : countriesWithSundayAsFirstDay) {
                    if (c.equals(country.toLowerCase()))
                        return Calendar.SUNDAY;
                }
            }
        }

        return Calendar.MONDAY;
    }

    public static int[] getScreenSize(@NonNull final Context context) {
        int[] size = new int[2];
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        size[0] = point.x;
        size[1] = point.y;

        return size;
    }

    public static String getTodayDateAsString() {
        final Date dateTime = new Date();
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        return dateTimeFormatter.format(dateTime);
    }

    public static String dateToString(@NonNull final Date dateTime) {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        return dateTimeFormatter.format(dateTime);
    }

    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     *
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    private static String getUserCountry(@NonNull final Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                } else {
                    // get the locale's country of the language set on the device
                    return getCurrentLocale(context).getISO3Country();
                }
            } else {
                // get the locale's country of the language set on the device
                return getCurrentLocale(context).getISO3Country();
            }
        } catch (Exception e) {
            Log.e(C.TAG, "Exception getting user's country code. ", e);
        }

        return null;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressWarnings("deprecation")
    private static Locale getCurrentLocale(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }

    public static long getPreference(@NonNull final Context context, @NonNull String key) {
        SharedPreferences settings = context.getSharedPreferences(C.SP_NAME, 0);

        return settings.getLong(key, 0);
    }

    public static Integer getScreenLooksByDay(@NonNull final Context context, @NonNull final Date date, @NonNull final ScreenLookCategory lookCategory) {
        Integer screenLooks = null;
        final DayLook dayLook = LookCountDbManager.getManager(context).getDayLookByDate(Utils.dateToString(date));
        if (dayLook != null) {
            screenLooks = lookCategory == ScreenLookCategory.SCREEN_LOOK ? dayLook.getScreenon() : dayLook.getScreenunlock();
        }

        return screenLooks;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    public static void setTextAppearance(@NonNull final Context context, @NonNull final TextView view, final int styleRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setTextAppearance(styleRes);
        } else {
            view.setTextAppearance(context, styleRes);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressWarnings("deprecation")
    public static Spanned getHtmlFormattedText(@NonNull final String string) {
        final Spanned spannedText;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spannedText = Html.fromHtml(string, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            spannedText = Html.fromHtml(string);
        }

        return spannedText;
    }

    public static void savePreference(@NonNull final Context context, @NonNull final String key, final long value) {
        SharedPreferences settings = context.getSharedPreferences(C.SP_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);

        editor.apply();
    }

    public static void showDialog(@NonNull final FragmentActivity context, @NonNull final Class<? extends DialogFragment> clazz,
                                  @NonNull final String tag, @NonNull final Bundle args) {
        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        try {
            DialogFragment fragment = clazz.newInstance();
            fragment.setArguments(args);
            ft.add(fragment, tag);
            ft.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isFragmentVisible(@NonNull final FragmentActivity context, @NonNull final String tag) {
        Fragment fragment = context.getSupportFragmentManager().findFragmentByTag(tag);
        return (fragment != null && fragment.isVisible());
    }

    public static Fragment getFragmentByTag(@NonNull final FragmentActivity context, @NonNull final String tag) {
        return context.getSupportFragmentManager().findFragmentByTag(tag);
    }

    public static void addFragment(@NonNull final FragmentActivity context, @NonNull final Class<? extends Fragment> clazz,
                                   @NonNull final String tag, final int parentViewId, final boolean addToBackStack, final Bundle args) {
        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        try {
            Fragment fragment = clazz.newInstance();
            fragment.setArguments(args);
            ft.replace(parentViewId, fragment, tag);
            if (addToBackStack) {
                ft.addToBackStack(tag);
            }
            ft.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addFragment(@NonNull final FragmentActivity context, @NonNull final Class<? extends Fragment> clazz,
                                   @NonNull final String tag, final View parentView, final boolean addToBackStack, final Bundle args) {
        addFragment(context, clazz, tag, parentView.getId(), addToBackStack, args);
    }

    public static Calendar stringToDate(@NonNull final String stringDate) {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        Calendar dateTime = Calendar.getInstance();
        try {
            dateTime.setTime(dateTimeFormatter.parse(stringDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateTime;
    }
}
