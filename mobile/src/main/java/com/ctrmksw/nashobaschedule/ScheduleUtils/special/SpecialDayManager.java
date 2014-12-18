package com.ctrmksw.nashobaschedule.ScheduleUtils.special;

import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDayManager;

import java.util.ArrayList;

/**
 * Created by Colin on 12/11/2014.
 */
public class SpecialDayManager
{
    private static ArrayList<SpecialDay> specialDays = null;
    public static void loadSpecialDays(ArrayList<SpecialDay> specialDays)
    {
        SpecialDayManager.specialDays = specialDays;
        SchoolDayManager.loadedSpecialDays(specialDays);
    }

    public static int getSpecialDaysSize()
    {
        if(specialDays == null)
            return -1;
        return specialDays.size();
    }

    public static ArrayList<SpecialDay> getSpecialDaysCopy()
    {
        if(specialDays == null)
            return null;

        ArrayList<SpecialDay> copy = new ArrayList<>();
        for(SpecialDay s : specialDays)
        {
            copy.add(s);
        }
        return copy;
    }

    public static void removeSpecialDays()
    {
        specialDays = null;
        SchoolDayManager.loadedSpecialDays(specialDays);
    }
}
