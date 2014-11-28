package com.ctrmksw.nashobaschedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctrmksw.nashobaschedule.ScheduleUtils.NRDay;
import com.ctrmksw.nashobaschedule.database.DatabasePeriodName;
import com.ctrmksw.nashobaschedule.database.MySchedule;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Created by Colin on 11/20/2014.
 */
public class AgendaFragment extends Fragment
{
    public static final String ARG_NRDAY = "argument_nrday";
    String[] dayOptions = {"Normal", "Activity Period", "Early Release", "ER / AP"};


    private NRDay nrDay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        nrDay = NRDay.fromString(args.getString(ARG_NRDAY));

        RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.agenda_view, container, false);

        TextView title = (TextView)root.findViewById(R.id.main_card_title);
        String dayText = dayOfWeek(nrDay.date) + ", " + getMonthForInt(nrDay.date.get(Calendar.MONTH)) + " " + nrDay.date.get(Calendar.DATE);
        title.setText(dayText);

        //Update Indicator Views
        TextView activityPeriod = (TextView)root.findViewById(R.id.main_card_row_activity_period);
        TextView earlyRelease = (TextView)root.findViewById(R.id.main_card_row_early_release);
        TextView letter = (TextView)root.findViewById(R.id.main_card_row_letter);
        TextView dayNum = (TextView)root.findViewById(R.id.main_card_row_number);

        letter.setText(nrDay.classes.get(0).name());
        dayNum.setText(String.valueOf(nrDay.day));

        if(nrDay.dayType.equals(dayOptions[0]))
        {
            activityPeriod.setVisibility(View.GONE);
            earlyRelease.setVisibility(View.GONE);
        }
        else if(nrDay.dayType.equals(dayOptions[1]))
        {
            activityPeriod.setVisibility(View.VISIBLE);
            earlyRelease.setVisibility(View.GONE);
        }
        else if(nrDay.dayType.equals(dayOptions[2]))
        {
            activityPeriod.setVisibility(View.GONE);
            activityPeriod.setVisibility(View.VISIBLE);
        }
        else
        {
            activityPeriod.setVisibility(View.VISIBLE);
            earlyRelease.setVisibility(View.VISIBLE);
        }

        MySchedule mySchedule = AgendaActivity.getMySchedule();

        TextView firstPeriodClass = (TextView)root.findViewById(R.id.main_card_first_class_text);
        DatabasePeriodName firstPeriodName = DatabasePeriodName.valueOf(nrDay.classes.get(0).name());
        firstPeriodClass.setText(mySchedule.get(firstPeriodName, nrDay.day).getClassName());

        return root;
    }

    private String getMonthForInt(int num) {
        String month = "error";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    private String dayOfWeek(Calendar cal)
    {
        int day = cal.get(Calendar.DAY_OF_WEEK);
        switch (day)
        {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
        }
        return "Error";
    }
}
