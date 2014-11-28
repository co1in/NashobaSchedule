package com.ctrmksw.nashobaschedule.database;

/**
 * Created by Colin on 11/26/2014.
 */
public class DatabaseNRClass
{
    private String className;
    private int lunchPeriod;
    private boolean[] activatedPeriods;
    private DatabasePeriodName periodName;

    public DatabaseNRClass(DatabasePeriodName periodName, String className, int lunchPeriod, boolean[] activatedPeriods)
    {
        this.className = className;
        this.lunchPeriod = lunchPeriod;
        this.activatedPeriods = activatedPeriods;
        this.periodName = periodName;
    }

    public String getClassName() {
        return className;
    }

    public int getLunchPeriod() {
        return lunchPeriod;
    }

    public boolean getIsActivated(int schoolDay)
    {
        return activatedPeriods[schoolDay - 1];
    }

    public boolean[] getActivatedArray()
    {
        boolean[] array = new boolean[activatedPeriods.length];
        for(int i = 0; i < activatedPeriods.length; i++)
            array[i] = activatedPeriods[i];
        return array;
    }

    public DatabasePeriodName getPeriodName()
    {
        return periodName;
    }
}
