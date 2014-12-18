package com.ctrmksw.nashobaschedule.ScheduleUtils.special;

import android.util.Log;

import com.ctrmksw.nashobaschedule.ScheduleUtils.nr.NRDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.time.ClassTime;
import com.ctrmksw.nashobaschedule.ScheduleUtils.ClassType;
import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.time.ClassTimeManager;
import com.ctrmksw.nashobaschedule.ScheduleUtils.time.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Colin on 12/11/2014.
 */
public class SpecialDay extends SchoolDay
{
    private DayType dayType;
    public String dayLabel = null;
    private ArrayList<SpecialClass> classList;
    public int rotationDay = -1;
    private String todayNotes = "";

    public SpecialDay(DayType type, Calendar date)
    {
        super(date);
        this.dayType = type;
        classList = new ArrayList<>();
    }

    public DayType getSpecialType()
    {
        return dayType;
    }

    public void thisIsReplacing(NRDay day)
    {
        if(dayType == DayType.TwoHrDelay)
        {
            classList.clear();
            ArrayList<ClassType> originalSchedule = day.classes;
            int longblock = 3;
            int[] classOrder = {0,1,2,4,3,5,6};
            for(int i = 0; i < classOrder.length; i++)
            {
                classList.add( new SpecialClass(originalSchedule.get(classOrder[i]), ClassTimeManager.twoHourDelay[i], i == longblock));
            }
        }
        if(dayType != DayType.SnowDay)
        {
            rotationDay = day.getRotationDay();
        }

        this.todayNotes = day.getTodayNotes();
    }

    public static SpecialDay fromString(String s)
    {
        String[] split = s.split(" ");
        try
        {
            String[] dateSplit = split[0].split("\\Q/\\E");
            int month = Integer.parseInt(dateSplit[0]);
            int date = Integer.parseInt(dateSplit[1]);
            int year = Integer.parseInt(dateSplit[2]);
            Calendar cal = new GregorianCalendar(year, month, date);

            SpecialDay day = new SpecialDay(DayType.valueOf(split[1]), cal);

            if(day.getSpecialType() == DayType.Other)
            {
                split[2] = split[2].replace('~', ' ');
                day.dayLabel = split[2];

                //Period of longblock (starting at index 0)
                int offset = 0;
                try
                {

                    day.rotationDay = Integer.parseInt(split[split.length-2]);
                    offset = 1;
                }
                catch (NumberFormatException e)
                {
                }
                int longblock = Integer.parseInt(split[split.length-1]);

                for(int i = 3; i < split.length-1 - offset; i++)
                {
                    String[] comps = split[i].split("\\Q-\\E");
                    ClassType dayType = ClassType.valueOf(comps[0]);
                    ClassTime time = new ClassTime(new Time(comps[1]), new Time(comps[2]));
                    SpecialClass specialClass = new SpecialClass(dayType, time, ((i-3) == longblock));

                    day.addSpecialClass(specialClass);
                }
            }

            return day;
        }
        catch(Throwable t)
        {
            Log.e("Special Day Parse", t.getMessage());
            return null;
        }
    }

    public void addSpecialClass(SpecialClass specialClass)
    {
        classList.add(specialClass);
    }

    public SpecialClass getSpecialClass(int i)
    {
        return classList.get(i);
    }

    public int getSpecialClassesSize()
    {
        return classList.size();
    }

    public boolean hasLongBlock()
    {
        for(SpecialClass spClass : classList)
        {
            if(spClass.longblock)
                return true;
        }
        return false;
    }

    @Override
    public String getTodayNotes()
    {
        return todayNotes;
    }

    public int getLongBlockPeriod()
    {
        for(int i = 0; i < classList.size(); i++)
        {
            if(classList.get(i).longblock)
                return i;
        }
        return -1;
    }

    public ClassType getLongBlockClass()
    {
        int period = getLongBlockPeriod();
        return classList.get(period).classType;
    }

    @Override
    public boolean dayHasEnded()
    {
        Calendar now = Calendar.getInstance();
        int comparison = compareTo(now);
        if(comparison < 0)
        {
            return true;
        }
        else if(comparison == 0)
        {
            if(dayType == DayType.SnowDay || dayType == DayType.TwoHrDelay)
                return false;
            else
            {
                if(classList.size() > 0)
                {
                    Time endTime = classList.get(classList.size()-1).classTime.getFinish();
                    return (endTime.getHours() > now.get(Calendar.HOUR_OF_DAY) || (endTime.getHours() == now.get(Calendar.HOUR_OF_DAY) && endTime.getMinutes() > now.get(Calendar.MINUTE)));
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        String strCal = getDate().get(Calendar.MONTH) + "/" + getDate().get(Calendar.DATE) + "/" + getDate().get(Calendar.YEAR);

        if(dayType == DayType.Other)
        {
            String retr = strCal + " " + dayType.name() + " " + dayLabel;
            for(SpecialClass specialClass : classList)
            {
                retr += " " + specialClass.classType.name() + "-" + specialClass.classTime.getStart().get24HrString() + "-" + specialClass.classTime.getFinish().get24HrString();
            }

            int longblock = -1;
            for(int i = 0; i < classList.size(); i++)
            {
                if(classList.get(i).longblock)
                {
                    longblock = i;
                    break;
                }
            }

            if(rotationDay != -1)
                retr += " " + longblock;
            else
                retr += " " + rotationDay + " " + longblock;

            return retr;
        }
        else
        {
            return strCal + " " + dayType.name();
        }
    }

    @Override
    public int getRotationDay()
    {
        return rotationDay;
    }
}
