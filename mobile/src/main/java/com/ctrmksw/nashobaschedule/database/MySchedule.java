package com.ctrmksw.nashobaschedule.database;

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
}
