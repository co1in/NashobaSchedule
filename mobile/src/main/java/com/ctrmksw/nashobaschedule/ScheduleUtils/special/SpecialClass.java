package com.ctrmksw.nashobaschedule.ScheduleUtils.special;

import com.ctrmksw.nashobaschedule.ScheduleUtils.time.ClassTime;
import com.ctrmksw.nashobaschedule.ScheduleUtils.ClassType;

/**
 * Created by Colin on 12/11/2014.
 */
public class SpecialClass
{
    public SpecialClass(){}

    public SpecialClass(ClassType classType, ClassTime classTime, boolean longblock) {
        this.classType = classType;
        this.classTime = classTime;
        this.longblock = longblock;
    }

    public ClassType classType;
    public ClassTime classTime;
    public boolean longblock;
}
