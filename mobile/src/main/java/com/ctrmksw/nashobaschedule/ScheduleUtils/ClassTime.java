package com.ctrmksw.nashobaschedule.ScheduleUtils;

import java.util.Calendar;

/**
 * Created by Colin on 11/30/2014.
 */
public class ClassTime
{
    private Time start, finish;
    public ClassTime(Time start, Time finish)
    {
        this.start = start;
        this.finish = finish;
    }

    public boolean isWithinThisTimePeriod(Calendar time)
    {
        int hours = time.get(Calendar.HOUR_OF_DAY);
        int minutes = time.get(Calendar.MINUTE);
        Time cal = new Time(hours, minutes);

        return (cal.compareTo(start) >= 0) && (cal.compareTo(finish) <= 0);
    }

    public Time getStart()
    {
        return start;
    }

    public Time getFinish()
    {
        return finish;
    }

    public int getMinutesUntilClassEnds(Calendar now)
    {
        return finish.getMinutesUntil(new Time(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)));
    }
}
