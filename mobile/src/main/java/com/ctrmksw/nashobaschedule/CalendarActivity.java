package com.ctrmksw.nashobaschedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.ctrmksw.nashobaschedule.ScheduleUtils.ClassType;
import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDayManager;
import com.ctrmksw.nashobaschedule.ScheduleUtils.nr.NRDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.nr.NRSchedule;
import com.ctrmksw.nashobaschedule.ScheduleUtils.special.DayType;
import com.ctrmksw.nashobaschedule.ScheduleUtils.special.SpecialDay;
import com.ctrmksw.nashobaschedule.database.DatabaseNRClass;
import com.ctrmksw.nashobaschedule.database.DatabasePeriodName;
import com.ctrmksw.nashobaschedule.database.MySchedule;
import com.ctrmksw.nashobaschedule.database.ScheduleDbHelper;

import java.util.ArrayList;
import java.util.Calendar;


public class CalendarActivity extends Activity
{

    private MySchedule mySchedule;

    private ScheduleDbHelper database;

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ScheduleAdapter adapter;

    public static final String EXTRA_CLICKED_INDEX = "ClickedIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_calendar);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.calendar_recycler_view);
        recyclerView.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        adapter = new ScheduleAdapter(SchoolDayManager.getCurrentList());
        recyclerView.setAdapter(adapter);

        database = new ScheduleDbHelper(this);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        database.getClassInfo(new ScheduleDbHelper.QueryRunnable()
        {
            @Override
            public void run(MySchedule map)
            {

                CalendarActivity.this.mySchedule = map;

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        adapter = new ScheduleAdapter(SchoolDayManager.getCurrentList());
                        recyclerView.setAdapter(adapter);

                        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(300);
                        anim.setAnimationListener(new Animation.AnimationListener()
                        {
                            @Override
                            public void onAnimationStart(Animation animation)
                            {
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation)
                            {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation)
                            {

                            }
                        });
                        recyclerView.startAnimation(anim);
                    }
                });

                if(!SchedPrefs.getHasViewedCalendarHint(CalendarActivity.this))
                {
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    View tipRoot = findViewById(R.id.calendar_tip_root);
                                    tipRoot.setVisibility(View.VISIBLE);
                                    findViewById(R.id.calendar_tip_root).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            findViewById(R.id.calendar_tip_root).setVisibility(View.GONE);
                                            SchedPrefs.didViewCalendarHint(CalendarActivity.this);
                                        }
                                    });
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void rowClicked(int index)
    {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CLICKED_INDEX, index);

        setResult(RESULT_OK, intent);
        finish();
    }

    public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder>
    {
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            // each data item is just a string in this case
            public TextView letter, dayNum, title, earlyRelease, activityPeriod;
            View snowIcon, starIcon;
            public DayRowListener listener;
            public ViewHolder(View rowView, DayRowListener listener)
            {
                super(rowView);
                this.letter = (TextView)rowView.findViewById(R.id.row_letter);
                this.dayNum = (TextView)rowView.findViewById(R.id.row_number);
                this.title = (TextView)rowView.findViewById(R.id.row_title);
                this.earlyRelease = (TextView) rowView.findViewById(R.id.row_early_release);
                this.activityPeriod = (TextView) rowView.findViewById(R.id.row_activity_period);
                this.snowIcon = rowView.findViewById(R.id.row_snow_icon);
                this.listener = listener;
                this.starIcon = rowView.findViewById(R.id.row_star_icon);

                rowView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v)
            {
                listener.onClick(v);
            }
        }

        private ArrayList<SchoolDay> sched;
        public ScheduleAdapter(ArrayList<SchoolDay> sched)
        {
            this.sched = sched;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i)
        {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_calendar_day_row, parent, false);

            SchoolDay day = sched.get(i);
            ViewHolder holder = new ViewHolder(row, new DayRowListener(i));
            dayToHolder(holder, day);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i)
        {
            SchoolDay day = sched.get(i);

            //update the day for this item's onclick listener
            viewHolder.listener.listeningDayIndex = i;

            dayToHolder(viewHolder, day);
        }

        private void dayToHolder(ViewHolder holder, SchoolDay day)
        {
            //Title
            String dayOfWeek = dayOfWeek(day.getDate());
            if(dayOfWeek.equals("Thursday"))
                dayOfWeek = "Thurs";
            else if(dayOfWeek.equals("Tuesday"))
                dayOfWeek = "Tues";
            else
                dayOfWeek = dayOfWeek.substring(0, 3);
            holder.title.setText(dayOfWeek + "- " + (day.getDate().get(Calendar.MONTH)+1) + "/" + day.getDate().get(Calendar.DATE));

            if(day instanceof NRDay || (((SpecialDay)day).getSpecialType() != DayType.SnowDay))
            {
                if(day instanceof NRDay)
                {
                    String dayType = ((NRDay)day).dayType;
                    String firstClass = ((NRDay)day).classes.get(0).name();
                    //Indicators
                    holder.letter.setText(firstClass);
                    holder.dayNum.setText(String.valueOf((day.getRotationDay())));
                    holder.snowIcon.setVisibility(View.GONE);

                    holder.letter.setVisibility(View.VISIBLE);
                    holder.dayNum.setVisibility(View.VISIBLE);

                    if(dayType.equals(NRSchedule.DAY_OPTION_NORMAL))
                    {
                        holder.activityPeriod.setVisibility(View.GONE);
                        holder.earlyRelease.setVisibility(View.GONE);
                    }
                    else if(dayType.equals(NRSchedule.DAY_OPTION_AP))
                    {
                        holder.activityPeriod.setVisibility(View.VISIBLE);
                        holder.earlyRelease.setVisibility(View.GONE);
                    }
                    else if(dayType.equals(NRSchedule.DAY_OPTION_ER))
                    {
                        holder.activityPeriod.setVisibility(View.GONE);
                        holder.earlyRelease.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        holder.activityPeriod.setVisibility(View.VISIBLE);
                        holder.earlyRelease.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    if(((SpecialDay) day).getSpecialClassesSize() > 0)
                        holder.letter.setText(((SpecialDay)day).getSpecialClass(0).classType.name());
                    holder.dayNum.setText(String.valueOf(((SpecialDay)day).rotationDay));
                    holder.letter.setVisibility(View.VISIBLE);
                    holder.dayNum.setVisibility(View.VISIBLE);

                    holder.activityPeriod.setVisibility(View.GONE);
                    holder.earlyRelease.setVisibility(View.GONE);
                    if(((SpecialDay)day).getSpecialType() == DayType.TwoHrDelay)
                        holder.snowIcon.setVisibility(View.VISIBLE);
                    else
                    {
                        holder.snowIcon.setVisibility(View.GONE);
                    }
                }

                boolean showStar = false;
                //Check to see if long block study star should be shown
                if(SchedPrefs.getHighlightLongblockStudy(CalendarActivity.this))
                {
                    if(day.hasLongBlock())
                    {
                        ClassType temp =  day.getLongBlockClass();
                        if(temp != ClassType.AP)
                        {
                            DatabaseNRClass databaseNRClass = mySchedule.get(temp, day.getRotationDay());
                            if(databaseNRClass.getClassName().toLowerCase().equals("study"))
                            {
                                showStar = true;

                            }
                        }
                    }
                }

                if(showStar)
                    holder.starIcon.setVisibility(View.VISIBLE);
                else
                    holder.starIcon.setVisibility(View.GONE);
            }
            else
            {
                holder.letter.setVisibility(View.GONE);
                holder.dayNum.setVisibility(View.GONE);
                holder.activityPeriod.setVisibility(View.GONE);
                holder.earlyRelease.setVisibility(View.GONE);
                holder.snowIcon.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount()
        {
            return sched.size();
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

    public class DayRowListener
    {
        public int listeningDayIndex;

        public DayRowListener(int dayIndex)
        {
            listeningDayIndex = dayIndex;
        }

        public void onClick(View v)
        {
            rowClicked(listeningDayIndex);
        }
    }
}
