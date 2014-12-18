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
    private static final String PREF_SETTINGS_DEVELOPER = "prefSettingsDeveloperPage";
    private static final String PREF_SAVED_DEVELOPER_PASSWORD = "prefSavedDeveloperPassword";
    private static final String PREF_HIGHLIGHT_LONGBLOCK_STUDY = "prefHighlightLongBlockStudy";

    public static boolean getHasSavedSchedule(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREF_SETUP, false);
    }

    public static void didSetupSchedule(Context context)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        edit.putBoolean(PREF_SETUP, true);
        edit.apply();
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
        edit.apply();
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
        edit.apply();
    }

    public static boolean getHasActivatedDeveloperPage(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREF_SETTINGS_DEVELOPER, false);
    }

    public static void didActivateDeveloperPage(Context context)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        edit.putBoolean(PREF_SETTINGS_DEVELOPER, true);
        edit.apply();
    }

    public static String getSavedDeveloperPassword(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_SAVED_DEVELOPER_PASSWORD, "");
    }

    public static void setSavedDeveloperPassword(Context context, String s)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        edit.putString(PREF_SAVED_DEVELOPER_PASSWORD, s);
        edit.apply();
    }

    public static boolean getHighlightLongblockStudy(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREF_HIGHLIGHT_LONGBLOCK_STUDY, false);
    }

    public static void setHighlightLongBlockStudy(Context context, boolean value)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        edit.putBoolean(PREF_HIGHLIGHT_LONGBLOCK_STUDY, value);
        edit.apply();
    }
}
