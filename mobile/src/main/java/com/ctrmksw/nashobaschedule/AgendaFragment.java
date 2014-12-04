package com.ctrmksw.nashobaschedule;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ctrmksw.nashobaschedule.ScheduleUtils.ClassTime;
import com.ctrmksw.nashobaschedule.ScheduleUtils.ClassTimeManager;
import com.ctrmksw.nashobaschedule.ScheduleUtils.NRDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.NRSchedule;
import com.ctrmksw.nashobaschedule.database.DatabaseNRClass;
import com.ctrmksw.nashobaschedule.database.MySchedule;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Created by Colin on 11/20/2014.
 */
public class AgendaFragment extends Fragment
{
    public static final String ARG_NRDAY = "argument_nrday";
    String[] dayOptions = {"Normal", "Activity Period", "Early Release", "ER / AP"};

    private MySchedule mySchedule;
    private NRDay nrDay;
    private LayoutInflater inflater;
    private LinearLayout root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        nrDay = NRDay.fromString(args.getString(ARG_NRDAY));
        mySchedule = AgendaActivity.getMySchedule();

        this.inflater = inflater;

        ScrollView scrollRoot = (ScrollView) inflater.inflate(R.layout.fragment_agenda, container, false);
        root = (LinearLayout) scrollRoot.findViewById(R.id.agenda_root_layout);

        if(mySchedule != null)
        {
            populateCards();
        }

        return scrollRoot;
    }

    public void populateCards()
    {
        CardView mainCard = populateMainCard();
        CardView scheduleCard = populateScheduleCard();
        CardView specialLunchCard = checkSpecialLunch();
        setupPdfCard();

        if(specialLunchCard != null)
            root.addView(specialLunchCard, 0);
        root.addView(scheduleCard, 0);
        root.addView(mainCard, 0);
    }

    private CardView populateMainCard()
    {
        CardView root = (CardView) inflater.inflate(R.layout.view_main_main_card, this.root, false);

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
            earlyRelease.setVisibility(View.VISIBLE);
        }
        else
        {
            activityPeriod.setVisibility(View.VISIBLE);
            earlyRelease.setVisibility(View.VISIBLE);
        }

        TextView dayTypeText = (TextView)root.findViewById(R.id.main_card_day_type_text);
        if(nrDay.dayType.equals(NRSchedule.dayOptions[0]))
            dayTypeText.setText("Normal Day");
        else
            dayTypeText.setText(nrDay.dayType);

        TextView longBlockClass = (TextView)root.findViewById(R.id.main_card_longblock_class_text);
        String[] lunchNames = {"first", "second", "third", "fourth"};
        TextView lunchTv = (TextView)root.findViewById(R.id.main_card_lunch_message_text);
        if(nrDay.dayType.equals(dayOptions[0]))
        {
            DatabaseNRClass lunchNRClass = mySchedule.get(nrDay.classes.get(4), nrDay.day);
            String todaysLunch = "\u2022 You have " + lunchNames[lunchNRClass.getLunchPeriod()-1] + " lunch";
            if(nrDay.compareTo(Calendar.getInstance()) == 0)
                todaysLunch += " today";
            lunchTv.setText(todaysLunch);

            longBlockClass.setText(lunchNRClass.getClassName());
        }
        else if(nrDay.dayType.equals(dayOptions[1]))
        {
            DatabaseNRClass lunchNRClass = mySchedule.get(nrDay.classes.get(5), nrDay.day);
            String todaysLunch = "\u2022 You have " + lunchNames[lunchNRClass.getLunchPeriod()-1] + " lunch";
            if(nrDay.compareTo(Calendar.getInstance()) == 0)
                todaysLunch += " today";
            lunchTv.setText(todaysLunch);

            longBlockClass.setText(lunchNRClass.getClassName());
        }
        else
        {
            lunchTv.setVisibility(View.GONE);
            longBlockClass.setVisibility(View.GONE);

            TextView literalLongBlockText = (TextView)root.findViewById(R.id.main_card_long_block_literal_text);
            literalLongBlockText.setText("\u2022 There is no lunch today");
        }

        return root;
    }

    private CardView populateScheduleCard()
    {
        CardView root = (CardView) inflater.inflate(R.layout.view_main_schedule_card, this.root, false);

        LinearLayout timesLayout = (LinearLayout) root.findViewById(R.id.schedule_card_times_layout);
        LinearLayout classesLayout = (LinearLayout) root.findViewById(R.id.schedule_card_classes_layout);

        for(int i = 0; i < nrDay.classes.size(); i++)
        {
            TextView timeView = new TextView(root.getContext());
            timeView.setTextSize(18);
            ClassTime classTime = ClassTimeManager.getClassTime(i+1, nrDay.dayType);
            timeView.setText(classTime.getStart().get12HrString(false) + " - " + classTime.getFinish().get12HrString(false));
            timesLayout.addView(timeView);

            Resources r = getResources();
            int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics()));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            params.weight = 1.0f;
            params.gravity = Gravity.RIGHT;
            params.bottomMargin = px;
            timeView.setLayoutParams(params);

            TextView classView = new TextView(root.getContext());
            classView.setTextSize(18);
            classView.setText(mySchedule.get(nrDay.classes.get(i), nrDay.day).getClassName());
            classesLayout.addView(classView);

            LinearLayout.LayoutParams classParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            classParams.bottomMargin = px;
            classView.setLayoutParams(classParams);
        }

        return root;
    }

    private CardView checkSpecialLunch()
    {
        if(nrDay.dayType.equals(NRSchedule.dayOptions[1]))
            return (CardView) inflater.inflate(R.layout.view_main_special_lunch_card, root, false);
        return null;
    }

    private void setupPdfCard()
    {
        root.findViewById(R.id.view_pdf_card).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String url = "http://docs.google.com/viewer?url=http%3A%2F%2Fnrhs.nrsd.net%2Fassets%2Ffiles%2FRotation%2520Schedule%2520-%25202014-2015%2520-%2520Aug%252026.pdf";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getActivity().startActivity(i);
            }
        });
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
