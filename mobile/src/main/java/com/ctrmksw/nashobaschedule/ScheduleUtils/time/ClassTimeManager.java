package com.ctrmksw.nashobaschedule.ScheduleUtils.time;

import com.ctrmksw.nashobaschedule.ScheduleUtils.nr.NRSchedule;

/**
 * Created by Colin on 11/30/2014.
 */
public class ClassTimeManager
{
    private static final ClassTime[] normalDay =
            {new ClassTime(new Time(7, 40), new Time(8, 26)),
            new ClassTime(new Time(8, 30), new Time(9, 16)),
            new ClassTime(new Time(9, 20), new Time(10, 6)),
            new ClassTime(new Time(10, 10), new Time(10, 56)),
            new ClassTime(new Time(11, 0), new Time(12, 38)),
            new ClassTime(new Time(12, 44), new Time(13, 30)),
            new ClassTime(new Time(13, 34), new Time(14, 20))};

    private static final ClassTime[] activityPeriodDay =
            {new ClassTime(new Time(7, 40), new Time(8, 18)),
            new ClassTime(new Time(8, 22), new Time(9, 0)),
            new ClassTime(new Time(9, 4), new Time(9, 44)),
            new ClassTime(new Time(9, 48), new Time(10, 26)),
            new ClassTime(new Time(10, 30), new Time(11, 8)),
            new ClassTime(new Time(11, 12), new Time(12, 56)),
            new ClassTime(new Time(13, 0), new Time(13, 38)),
            new ClassTime(new Time(13, 42), new Time(14, 20))};

    private static final ClassTime[] earlyReleaseDay =
            {new ClassTime(new Time(7, 40), new Time(8, 23)),
            new ClassTime(new Time(8, 27), new Time(9, 10)),
            new ClassTime(new Time(9, 14), new Time(9, 57)),
            new ClassTime(new Time(10, 2), new Time(10, 45)),
            new ClassTime(new Time(10, 49), new Time(11, 30))};

    public static final ClassTime[] twoHourDelay =
            {
            new ClassTime(new Time(9, 40), new Time(10, 06)),
            new ClassTime(new Time(10, 10), new Time(10, 36)),
            new ClassTime(new Time(10, 40), new Time(11, 6)),
            new ClassTime(new Time(11, 10), new Time(12, 48)),
            new ClassTime(new Time(12, 52), new Time(13, 18)),
            new ClassTime(new Time(13, 22), new Time(13, 48)),
            new ClassTime(new Time(13, 52), new Time(14, 20))
            };

    public static ClassTime getClassTime(int period, String dayType)
    {
        if(dayType.equals(NRSchedule.dayOptions[0]))
            return normalDay[period-1];
        else if(dayType.equals(NRSchedule.dayOptions[1]))
            return activityPeriodDay[period-1];
        else if(dayType.equals(NRSchedule.dayOptions[2]) || dayType.equals(NRSchedule.dayOptions[3]))
            return earlyReleaseDay[period-1];

        return null;
    }
}
