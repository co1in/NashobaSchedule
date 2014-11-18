package com.ctrmksw.nashobaschedule.ScheduleUtils;

import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Colin on 9/28/2014.
 */
public class NRSchedule
{
    private static ArrayList<NRDay> sched = new ArrayList<NRDay>();

    /*public static void setDay(NRDay updateDate)
    {
        loadFromFile();

        boolean set = false;
        for(int i = 0; i < sched.size(); i++)
        {
            if(sched.get(i).compareTo(updateDate.date) == 0)
            {
                sched.set(i, updateDate);
                set = true;
                break;
            }
        }
        if(!set)
            sched.add(updateDate);

        sort();
        saveToFile();
    }

    public static void removeDay(Calendar date)
    {
        loadFromFile();

        for(int i = 0; i < sched.size(); i++)
        {
            if(sched.get(i).compareTo(date) == 0)
                sched.remove(i);
        }

        saveToFile();
    }*/

    /*public static NRDay getDay(Calendar date)
    {
        loadFromFile();

        for(NRDay day : sched)
        {
            if(day.compareTo(date) == 0)
                return day;
        }
        return null;
    }*/

    public static ArrayList<NRDay> getSchedule(InputStream stream, Calendar fromDate)
    {
        loadFromFile(stream);
        sort();
        if(fromDate != null)
        {
            ArrayList<NRDay> temp = new ArrayList<NRDay>();
            for(NRDay day : sched)
            {
                if(day.date.compareTo(fromDate) >= 0)
                    temp.add(day);
            }
            sched = temp;
        }
        return sched;
    }

    private static void sort()
    {
        ArrayList<NRDay> sortedList = new ArrayList<NRDay>();
        for(NRDay day : sched)
        {
            if(sortedList.size() == 0)
                sortedList.add(day);
            else
            {
                for(int i = 0; i <= sortedList.size(); i++)
                {
                    if(i == sortedList.size())
                    {
                        sortedList.add(i, day);
                        break;
                    }
                    else if(sortedList.get(i).compareTo(day.date) > 0)
                    {
                        sortedList.add(i, day);
                        break;
                    }
                }
            }
        }
        if(sortedList.size() != sched.size())
            throw new RuntimeException("Sorting Error");

        sched = sortedList;
    }

    private static void loadFromFile(InputStream stream)
    {
        sched.clear();

        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            String line;

            while((line = in.readLine()) != null)
            {
                if(!(line.trim().equals("")))
                    sched.add(NRDay.fromString(line));
            }
            in.close();
        }
        catch (IOException e)
        {
            Log.e("IO Exception", e.getStackTrace().toString());
        }
    }

    /*private static void saveToFile()
    {
        String strSched = "";
        for(NRDay nrDay : sched)
        {
            strSched += nrDay.toString() + "\n";
        }

        try
        {
            File f = new File("sched.txt");
            f.createNewFile();
            PrintWriter out = new PrintWriter(f);
            out.print(strSched);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }*/
}
