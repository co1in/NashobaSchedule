package com.ctrmksw.nashobaschedule.ScheduleUtils.nr;

import com.ctrmksw.nashobaschedule.ScheduleUtils.ClassType;
import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Colin on 9/28/2014.
 */
public class NRDay extends SchoolDay
{
    public NRDay(Calendar date, ArrayList<ClassType> classes, String dayType, int day, String notes)
    {
        super(date);
        this.classes = classes;
        this.dayType = dayType;
        this.day = day;
        this.notes = notes;
    }

    public ArrayList<ClassType> classes;
    public String dayType;
    private int day;
    private String notes = "";

    public static NRDay fromString(String str)
    {
        String[] comps = str.split("\\Q|\\E");

        int day = Integer.parseInt(comps[0]);

        String[] calComps = comps[1].split("\\/");
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.MONTH, Integer.parseInt(calComps[0]));
        cal.set(Calendar.DATE, Integer.parseInt(calComps[1]));
        cal.set(Calendar.YEAR, Integer.parseInt(calComps[2]));

        String dayType = comps[2];

        ArrayList<ClassType> classes = new ArrayList<ClassType>();
        String[] classComps = comps[3].split("\\$");
        for(String strClass : classComps)
        {
            ClassType classType = ClassType.valueOf(strClass);
            classes.add(classType);
        }

        String notes = "";
        if(comps.length == 5)
            notes = comps[4];

        return new NRDay(cal, classes, dayType, day, notes);
    }

    @Override
    public boolean dayHasEnded()
    {
        Calendar now = Calendar.getInstance();
        int comparison = compareTo(now);
        if(comparison < 0)
            return true;
        else if(comparison == 0)
        {
            if(dayType.equals(NRSchedule.DAY_OPTION_NORMAL) || dayType.equals(NRSchedule.DAY_OPTION_AP))
            {
                return (now.get(Calendar.HOUR_OF_DAY) > 14 || (now.get(Calendar.HOUR_OF_DAY) == 14 && now.get(Calendar.MINUTE) > 21));
            }
            else if(dayType.equals(NRSchedule.DAY_OPTION_ER) || dayType.equals(NRSchedule.DAY_OPTION_ERAP))
            {
                return (now.get(Calendar.HOUR_OF_DAY) > 11 || (now.get(Calendar.HOUR_OF_DAY) == 11 && now.get(Calendar.MINUTE) > 31));
            }
            else
            {
                throw new RuntimeException("Error: unexpected day type");
            }
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        String date = getDate().get(Calendar.MONTH) + "/" + getDate().get(Calendar.DATE) + "/" + getDate().get(Calendar.YEAR);
        String strClasses = "";
        for(ClassType ClassType : classes)
        {
            strClasses += ClassType.name() + "$";
        }
        return day + "|" + date + "|" + dayType + "|" + strClasses + "|" + notes;
    }

    @Override
    public int getRotationDay()
    {
        return day;
    }

    @Override
    public ClassType getLongBlockClass()
    {
        if(dayType.equals(NRSchedule.DAY_OPTION_NORMAL))
            return classes.get(4);
        else if(dayType.equals(NRSchedule.DAY_OPTION_AP))
            return classes.get(5);
        else
            return null;
    }

    @Override
    public boolean hasLongBlock()
    {
        return (dayType.equals(NRSchedule.DAY_OPTION_NORMAL) || dayType.equals(NRSchedule.DAY_OPTION_AP));
    }

    @Override
    public String getTodayNotes()
    {
        return notes;
    }
}
