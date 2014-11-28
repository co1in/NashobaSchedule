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

    public static boolean getIsSetup(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(PREF_SETUP, false);
    }

    public static void didSetup(Context context)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        edit.putBoolean(PREF_SETUP, true);
        edit.commit();
    }
}
