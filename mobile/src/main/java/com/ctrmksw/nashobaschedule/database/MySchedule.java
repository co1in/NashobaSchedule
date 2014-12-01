package com.ctrmksw.nashobaschedule.database;

import com.ctrmksw.nashobaschedule.ScheduleUtils.ClassType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Colin on 11/26/2014.
 */
public class MySchedule extends ArrayList<DatabaseNRClass>
{
    public DatabaseNRClass get(DatabasePeriodName period, int day)
    {
        for(int i = 0; i < size(); i++)
        {
            if(get(i).getPeriodName() == period && get(i).getIsActivated(day))
                return get(i);
        }
        return null;
    }

    public DatabaseNRClass get(ClassType period, int day)
    {
        if(period == ClassType.AP)
        {
            return new DatabaseNRClass(null, "Activity Period", -1, new boolean[]{true, true, true, true, true, true, true, true});
        }
        for(int i = 0; i < size(); i++)
        {
            if(get(i).getPeriodName() == DatabasePeriodName.valueOf(period.name()) && get(i).getIsActivated(day))
                return get(i);
        }
        return null;
    }

    public DatabaseNRClass getMainClass(DatabasePeriodName period)
    {
        for(int i = 0; i < size(); i++)
        {
            if(get(i).getPeriodName() == period)
                return get(i);
        }

        return null;
    }

    public DatabaseNRClass getAltClass(DatabasePeriodName period)
    {
        boolean goodToGo = false;
        DatabaseNRClass temp = null;
        for(int i = size()-1; i >=0; i--)
        {
            if(get(i).getPeriodName() == period)
            {
                if(temp == null)
                    temp = get(i);
                else
                    goodToGo = true;
            }
        }
        if(goodToGo)
            return temp;
        else
            return null;
    }
}
