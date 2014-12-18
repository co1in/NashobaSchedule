package com.ctrmksw.nashobaschedule;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDayManager;
import com.ctrmksw.nashobaschedule.ScheduleUtils.special.DayType;
import com.ctrmksw.nashobaschedule.ScheduleUtils.special.SpecialDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.time.ClassTime;
import com.ctrmksw.nashobaschedule.ScheduleUtils.time.ClassTimeManager;
import com.ctrmksw.nashobaschedule.ScheduleUtils.nr.NRDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.nr.NRSchedule;
import com.ctrmksw.nashobaschedule.database.DatabaseNRClass;
import com.ctrmksw.nashobaschedule.database.MySchedule;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * Created by Colin on 11/20/2014.
 */
public class AgendaFragment extends Fragment
{
    public static final String ARG_DAY_INDEX = "argument_day";
    String[] dayOptions = {"Normal", "Activity Period", "Early Release", "ER / AP"};

    private MySchedule mySchedule;
    private SchoolDay today;
    private LayoutInflater inflater;
    private LinearLayout root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        int index = args.getInt(ARG_DAY_INDEX);

        today = SchoolDayManager.getFullList().get(index);

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
        try
        {
            CardView mainCard = populateMainCard();
            CardView scheduleCard = populateScheduleCard();
            CardView specialLunchCard = checkSpecialLunch();
            CardView todayNotesCard = checkTodayNotes();
            setupPdfCard();

            if(specialLunchCard != null)
                root.addView(specialLunchCard, 0);
            if(scheduleCard != null)
                root.addView(scheduleCard, 0);
            if(todayNotesCard != null)
                root.addView(todayNotesCard, 0);
            root.addView(mainCard, 0);
        }
        catch (Exception e)
        {
            Log.e("Population Error", "Population Error", e);
        }
    }

    private CardView checkTodayNotes()
    {
        if(!today.getTodayNotes().equals(""))
        {
            CardView root = (CardView) inflater.inflate(R.layout.view_main_happening_today, this.root, false);
            ((TextView)root.findViewById(R.id.main_happening_today_text)).setText(today.getTodayNotes());
            return root;
        }
        return null;
    }

    private CardView populateMainCard()
    {
        CardView root = (CardView) inflater.inflate(R.layout.view_main_main_card, this.root, false);

        TextView title = (TextView)root.findViewById(R.id.main_card_title);
        String dayText = dayOfWeek(today.getDate()) + ", " + getMonthForInt(today.getDate().get(Calendar.MONTH)) + " " + today.getDate().get(Calendar.DATE);
        title.setText(dayText);

        //Update Indicator Views
        TextView activityPeriod = (TextView)root.findViewById(R.id.main_card_row_activity_period);
        TextView earlyRelease = (TextView)root.findViewById(R.id.main_card_row_early_release);
        TextView letter = (TextView)root.findViewById(R.id.main_card_row_letter);
        TextView dayNum = (TextView)root.findViewById(R.id.main_card_row_number);
        View snowIcon = root.findViewById(R.id.main_card_row_snow_icon);
        TextView dayTypeText = (TextView)root.findViewById(R.id.main_card_day_type_text);
        TextView longBlockClass = (TextView)root.findViewById(R.id.main_card_longblock_class_text);

        String[] lunchNames = {"first", "second", "third", "fourth"};
        TextView lunchTv = (TextView)root.findViewById(R.id.main_card_lunch_message_text);

        if(today instanceof NRDay || ((SpecialDay)today).getSpecialType() != DayType.SnowDay)
        {
            if(today instanceof NRDay)
            {
                NRDay tempToday = (NRDay)today;
                String dayType = tempToday.dayType;
                String firstClass = tempToday.classes.get(0).name();
                //Indicators
                letter.setText(firstClass);
                dayNum.setText(String.valueOf(tempToday.getRotationDay()));
                if(dayType.equals(NRSchedule.DAY_OPTION_NORMAL))
                {
                    activityPeriod.setVisibility(View.GONE);
                    earlyRelease.setVisibility(View.GONE);
                }
                else if(dayType.equals(NRSchedule.DAY_OPTION_AP))
                {
                    activityPeriod.setVisibility(View.VISIBLE);
                    earlyRelease.setVisibility(View.GONE);
                }
                else if(dayType.equals(NRSchedule.DAY_OPTION_ER))
                {
                    activityPeriod.setVisibility(View.GONE);
                    earlyRelease.setVisibility(View.VISIBLE);
                }
                else
                {
                    activityPeriod.setVisibility(View.VISIBLE);
                    earlyRelease.setVisibility(View.VISIBLE);
                }

                if(tempToday.dayType.equals(NRSchedule.dayOptions[0]))
                    dayTypeText.setText("Normal Day");
                else
                    dayTypeText.setText(tempToday.dayType);

                if(tempToday.dayType.equals(NRSchedule.DAY_OPTION_NORMAL) || tempToday.dayType.equals(NRSchedule.DAY_OPTION_AP))
                {
                    DatabaseNRClass lunchNRClass = mySchedule.get(tempToday.getLongBlockClass(), tempToday.getRotationDay());
                    String todaysLunch = "\u2022 You have " + lunchNames[lunchNRClass.getLunchPeriod()-1] + " lunch";
                    if(tempToday.compareTo(Calendar.getInstance()) == 0)
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
            }
            else
            {
                SpecialDay tempToday = (SpecialDay)today;
                letter.setText(tempToday.getSpecialClass(0).classType.name());
                dayNum.setText(String.valueOf(tempToday.rotationDay));
                activityPeriod.setVisibility(View.GONE);
                earlyRelease.setVisibility(View.GONE);

                if(tempToday.getSpecialType() == DayType.TwoHrDelay)
                {
                    snowIcon.setVisibility(View.VISIBLE);
                    dayTypeText.setText("2 Hour Delay");
                }
                else //Day is OTHER
                {
                    dayTypeText.setText(tempToday.dayLabel);
                }

                if(tempToday.hasLongBlock())
                {
                    DatabaseNRClass databaseNRClass = mySchedule.get(tempToday.getSpecialClass(tempToday.getLongBlockPeriod()).classType, tempToday.rotationDay);

                    String todaysLunch = "\u2022 You have " + lunchNames[databaseNRClass.getLunchPeriod()-1] + " lunch";
                    if(tempToday.compareTo(Calendar.getInstance()) == 0)
                        todaysLunch += " today";
                    lunchTv.setText(todaysLunch);

                    longBlockClass.setText(databaseNRClass.getClassName());
                }
                else
                {
                    lunchTv.setVisibility(View.GONE);
                    longBlockClass.setVisibility(View.GONE);

                    TextView literalLongBlockText = (TextView)root.findViewById(R.id.main_card_long_block_literal_text);
                    literalLongBlockText.setText("\u2022 There is no lunch today");
                }
            }
        }
        else
        {
            letter.setVisibility(View.GONE);
            dayNum.setVisibility(View.GONE);
            activityPeriod.setVisibility(View.GONE);
            earlyRelease.setVisibility(View.GONE);
            snowIcon.setVisibility(View.VISIBLE);

            lunchTv.setVisibility(View.GONE);
            longBlockClass.setVisibility(View.GONE);

            dayTypeText.setText("Snow Day");

            TextView literalLongBlockText = (TextView)root.findViewById(R.id.main_card_long_block_literal_text);
            literalLongBlockText.setText("\u2022 Have a fun filled day!");
        }

        return root;
    }

    private CardView populateScheduleCard()
    {
        if(today instanceof SpecialDay && ((SpecialDay)today).getSpecialType() == DayType.SnowDay)
            return null;

        CardView root = (CardView) inflater.inflate(R.layout.view_main_schedule_card, this.root, false);

        LinearLayout timesLayout = (LinearLayout) root.findViewById(R.id.schedule_card_times_layout);
        LinearLayout classesLayout = (LinearLayout) root.findViewById(R.id.schedule_card_classes_layout);

        int limit;
        if(today instanceof NRDay)
            limit = ((NRDay)today).classes.size();
        else
            limit = ((SpecialDay)today).getSpecialClassesSize();

        for(int i = 0; i < limit; i++)
        {
            TextView timeView = new TextView(root.getContext());
            timeView.setTextSize(18);

            ClassTime classTime;
            if(today instanceof NRDay)
                classTime = ClassTimeManager.getClassTime(i+1, ((NRDay)today).dayType);
            else
                classTime = ((SpecialDay)today).getSpecialClass(i).classTime;
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
            if(today instanceof NRDay)
                classView.setText(mySchedule.get(((NRDay) today).classes.get(i), today.getRotationDay()).getClassName());
            else
            {
                SpecialDay tempToday = (SpecialDay)today;
                classView.setText(mySchedule.get(tempToday.getSpecialClass(i).classType, tempToday.rotationDay).getClassName());
            }
            classesLayout.addView(classView);

            LinearLayout.LayoutParams classParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            classParams.bottomMargin = px;
            classView.setLayoutParams(classParams);
        }

        return root;
    }

    private CardView checkSpecialLunch()
    {
        if(today instanceof NRDay && ((NRDay)today).dayType.equals(NRSchedule.dayOptions[1]))
            return (CardView) inflater.inflate(R.layout.view_main_special_lunch_card, root, false);
        else if(today instanceof SpecialDay && ((SpecialDay)today).getSpecialType() == DayType.TwoHrDelay)
        {
            if(((SpecialDay)today).hasLongBlock())
                return (CardView) inflater.inflate(R.layout.view_main_delay_lunch_card, root, false);
        }
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
