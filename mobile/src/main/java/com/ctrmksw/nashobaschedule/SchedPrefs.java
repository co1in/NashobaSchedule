package com.ctrmksw.nashobaschedule;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Colin on 11/26/2014.
 */
public class SchedPrefs
{
    private static final String PREFS_NAME = "NashobaScheduleSharedPrefs";
    private static final String PREF_SETUP = "isSetup";
    private static final String PREF_SLIDEY = "slideyViewed";
    private static final String PREF_CALENDAR = "prefCalendar";

    public static boolean getHasSavedSchedule(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREF_SETUP, false);
    }

    public static void didSetupSchedule(Context context)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        edit.putBoolean(PREF_SETUP, true);
        edit.commit();
    }

    public static boolean getHasViewedSlideyHint(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREF_SLIDEY, false);
    }

    public static void didViewSlidey(Context context)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        edit.putBoolean(PREF_SLIDEY, true);
        edit.commit();
    }

    public static boolean getHasViewedCalendarHint(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREF_CALENDAR, false);
    }

    public static void didViewCalendarHint(Context context)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        edit.putBoolean(PREF_CALENDAR, true);
        edit.commit();
    }
}
