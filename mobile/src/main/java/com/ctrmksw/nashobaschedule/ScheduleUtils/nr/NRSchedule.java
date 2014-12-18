package com.ctrmksw.nashobaschedule.ScheduleUtils.nr;

import android.util.Log;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Colin on 9/28/2014.
 */
public class NRSchedule
{
    private static ArrayList<NRDay> sched = new ArrayList<NRDay>();

    public static String[] dayOptions = {"Normal", "Activity Period", "Early Release", "ER / AP"};

    public static String DAY_OPTION_NORMAL = dayOptions[0];
    public static String DAY_OPTION_AP = dayOptions[1];
    public static String DAY_OPTION_ER = dayOptions[2];
    public static String DAY_OPTION_ERAP = dayOptions[3];

    public static ArrayList<NRDay> getSchedule(InputStream stream)
    {
        loadFromFile(stream);
        return sched;
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
}
