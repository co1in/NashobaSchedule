package com.ctrmksw.nashobaschedule.ScheduleUtils;

import java.util.Calendar;

/**
 * Created by Colin on 12/13/2014.
 */
public abstract class SchoolDay
{
    private Calendar date;
    public SchoolDay(Calendar date)
    {
        this.date = date;
    }

    public Calendar getDate()
    {
        return date;
    }

    public int compareTo(Calendar date)
    {
        int y1 = date.get(Calendar.YEAR), y2 = this.date.get(Calendar.YEAR);
        int m1 = date.get(Calendar.MONTH), m2 = this.date.get(Calendar.MONTH);
        int d1 = date.get(Calendar.DATE), d2 = this.date.get(Calendar.DATE);

        if(y1 != y2)
            return sign(y2 - y1);
        else
        {
            if(m1 != m2)
                return sign(m2 - m1);
            else
            {
                if(d1 != d2)
                    return sign(d2 - d1);
                else
                    return 0;
            }
        }
    }

    private int sign(int input)
    {
        return (input/Math.abs(input));
    }

    public int compareTo(SchoolDay day)
    {
        return compareTo(day.getDate());
    }

    public abstract boolean dayHasEnded();

    public abstract String toString();

    public abstract int getRotationDay();
    public abstract ClassType getLongBlockClass();
    public abstract boolean hasLongBlock();
    public abstract String getTodayNotes();
}
