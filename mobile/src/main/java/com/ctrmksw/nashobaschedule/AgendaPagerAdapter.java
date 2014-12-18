package com.ctrmksw.nashobaschedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDayManager;
import com.ctrmksw.nashobaschedule.ScheduleUtils.nr.NRDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDay;

import java.util.ArrayList;

/**
 * Created by Colin on 12/13/2014.
 */
public class AgendaPagerAdapter extends FragmentStatePagerAdapter
{
    public AgendaPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        AgendaFragment frag = new AgendaFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(AgendaFragment.ARG_DAY_INDEX, i);

        frag.setArguments(arguments);

        return frag;
    }

    @Override
    public int getCount()
    {
        return SchoolDayManager.getFullList().size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return "Schedule";
    }

    public int getScheduleIndexFor(SchoolDay day)
    {
        ArrayList<SchoolDay> temp = SchoolDayManager.getFullList();
        for(int i = 0; i < temp.size(); i++)
        {
            if(day.compareTo(temp.get(i).getDate()) == 0)
                return i;
        }
        throw new RuntimeException("Error: NRDay passed to getIndexFor() was not found in the schedule");
    }
}
