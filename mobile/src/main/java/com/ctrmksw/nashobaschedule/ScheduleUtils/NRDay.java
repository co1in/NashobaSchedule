package com.ctrmksw.nashobaschedule.ScheduleUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Colin on 9/28/2014.
 */
public class NRDay
{
    public NRDay(){}
    public NRDay(Calendar date, ArrayList<ClassType> classes, String dayType, int day, String notes)
    {
        this.date = date;
        this.classes = classes;
        this.dayType = dayType;
        this.day = day;
        this.notes = notes;
    }
    public Calendar date;
    public ArrayList<ClassType> classes;
    public String dayType;
    public int day;
    public String notes;

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

    public String toString()
    {
        String date = this.date.get(Calendar.MONTH) + "/" + this.date.get(Calendar.DATE) + "/" + this.date.get(Calendar.YEAR);
        String strClasses = "";
        for(ClassType ClassType : classes)
        {
            strClasses += ClassType.name() + "$";
        }
        return day + "|" + date + "|" + dayType + "|" + strClasses + "|" + notes;
    }

    public int compareTo(Calendar day)
    {
        int y1 = day.get(Calendar.YEAR), y2 = this.date.get(Calendar.YEAR);
        int m1 = day.get(Calendar.MONTH), m2 = this.date.get(Calendar.MONTH);
        int d1 = day.get(Calendar.DATE), d2 = this.date.get(Calendar.DATE);

        if(y1 != y2)
            return y2 - y1;
        else
        {
            if(m1 != m2)
                return m2 - m1;
            else
            {
                if(d1 != d2)
                    return d2 - d1;
                else
                    return 0;
            }
        }
    }
}
