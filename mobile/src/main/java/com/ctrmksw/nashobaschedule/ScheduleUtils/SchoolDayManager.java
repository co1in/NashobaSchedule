package com.ctrmksw.nashobaschedule.ScheduleUtils;

import com.ctrmksw.nashobaschedule.ScheduleUtils.nr.NRDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.special.SpecialDay;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Colin on 12/13/2014.
 */
public class SchoolDayManager
{
    private static ArrayList<NRDay> nrDays = null;
    private static ArrayList<SpecialDay> specialDays = null;
    private static ArrayList<SchoolDay> schoolDays;

    public static void loadedNRDays(ArrayList<NRDay> nrDays)
    {
        SchoolDayManager.nrDays = nrDays;
        syncCombinedList();
    }

    public static void loadedSpecialDays(ArrayList<SpecialDay> specialDays)
    {
        SchoolDayManager.specialDays = specialDays;
        syncCombinedList();
    }

    private static void syncCombinedList()
    {
        schoolDays = new ArrayList<>();
        if(nrDays != null)
            schoolDays.addAll(nrDays);
        if(specialDays != null)
        {
            for(int i = 0; i < specialDays.size(); i++)
            {
                boolean replaced = false;
                for(int p = 0; p < schoolDays.size(); p++)
                {
                    if(schoolDays.get(p).compareTo(specialDays.get(i)) == 0)
                    {
                        specialDays.get(i).thisIsReplacing((NRDay) schoolDays.get(p));
                        schoolDays.set(p, specialDays.get(i));
                        replaced = true;
                    }
                }
                if(!replaced)
                    schoolDays.add(specialDays.get(i));
            }
        }

        //sort the days
        schoolDays = sort(schoolDays);
    }

    public static ArrayList<SchoolDay> getFullList()
    {
        ArrayList<SchoolDay> temp = new ArrayList<>(schoolDays.size());
        for(SchoolDay day : schoolDays)
            temp.add(day);
        return temp;
    }

    public static ArrayList<SchoolDay> getCurrentList()
    {
        ArrayList<SchoolDay> temp = new ArrayList<>();
        for(SchoolDay day : schoolDays)
        {
            if(!day.dayHasEnded())
                temp.add(day);
        }
        return temp;
    }

    private static ArrayList<SchoolDay> sort(ArrayList<SchoolDay> list)
    {
        ArrayList<SchoolDay> sortedList = new ArrayList<>();
        for(SchoolDay day : list)
        {
            if(sortedList.size() == 0)
                sortedList.add(day);
            else
            {
                for(int i = 0; i <= sortedList.size(); i++)
                {
                    if(i == sortedList.size())
                    {
                        sortedList.add(i, day);
                        break;
                    }
                    else if(sortedList.get(i).compareTo(day.getDate()) > 0)
                    {
                        sortedList.add(i, day);
                        break;
                    }
                }
            }
        }
        if(sortedList.size() != list.size())
            throw new RuntimeException("Sorting Error");

        return sortedList;
    }

    public static int getIndexOf(SchoolDay day)
    {
        for(int i = 0; i < schoolDays.size(); i++)
        {
            if(schoolDays.get(i).compareTo(day) == 0)
                return i;
        }
        return -1;
    }

    public static SchoolDay getNextSchoolDay()
    {
        for(int i = 0; i < schoolDays.size(); i++)
        {
            if(!schoolDays.get(i).dayHasEnded())
                return schoolDays.get(i);
        }
        return null;
    }
}
