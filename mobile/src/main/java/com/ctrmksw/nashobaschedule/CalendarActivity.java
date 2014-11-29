package com.ctrmksw.nashobaschedule;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ctrmksw.nashobaschedule.ScheduleUtils.NRDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.NRSchedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class CalendarActivity extends Activity
{

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ScheduleAdapter adapter;

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

        try
        {
            adapter = new ScheduleAdapter(NRSchedule.getSchedule(getAssets().open("sched.txt"), Calendar.getInstance()));
        }
        catch (IOException e)
        {
            Log.e("IO Exception", e.getCause().getMessage());
            Toast.makeText(this, "Internal Error 1", Toast.LENGTH_SHORT).show();
            finish();
        }
        recyclerView.setAdapter(adapter);
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

    public void rowClicked(NRDay day, View v)
    {
        Toast.makeText(this, "Day " + day.date.get(Calendar.DATE) + " clicked", Toast.LENGTH_SHORT).show();
    }

    public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder>
    {
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            // each data item is just a string in this case
            public TextView letter, dayNum, title, earlyRelease, activityPeriod;
            public DayRowListener listener;
            public ViewHolder(View rowView, DayRowListener listener)
            {
                super(rowView);
                this.letter = (TextView)rowView.findViewById(R.id.row_letter);
                this.dayNum = (TextView)rowView.findViewById(R.id.row_number);
                this.title = (TextView)rowView.findViewById(R.id.row_title);
                this.earlyRelease = (TextView) rowView.findViewById(R.id.row_early_release);
                this.activityPeriod = (TextView) rowView.findViewById(R.id.row_activity_period);
                this.listener = listener;

                rowView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v)
            {
                listener.onClick(v);
            }
        }

        private ArrayList<NRDay> sched;
        public ScheduleAdapter(ArrayList<NRDay> sched)
        {
            this.sched = sched;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i)
        {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_row, parent, false);

            NRDay day = sched.get(i);
            ViewHolder holder = new ViewHolder(row, new DayRowListener(day));
            dayToHolder(holder, day);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i)
        {
            NRDay day = sched.get(i);

            //update the day for this item's onclick listener
            viewHolder.listener.listeningDay = day;

            dayToHolder(viewHolder, day);
        }

        String[] dayOptions = {"Normal", "Activity Period", "Early Release", "ER / AP"};
        private void dayToHolder(ViewHolder holder, NRDay day)
        {
            //Title
            String dayOfWeek = dayOfWeek(day.date);
            if(dayOfWeek.equals("Thursday"))
                dayOfWeek = "Thurs";
            else if(dayOfWeek.equals("Tuesday"))
                dayOfWeek = "Tues";
            else
                dayOfWeek = dayOfWeek.substring(0, 3);
            holder.title.setText(dayOfWeek + "- " + (day.date.get(Calendar.MONTH)+1) + "/" + day.date.get(Calendar.DATE));

            //Indicators
            holder.letter.setText(day.classes.get(0).name());
            holder.dayNum.setText(String.valueOf(day.day));
            if(day.dayType.equals(dayOptions[0]))
            {
                holder.activityPeriod.setVisibility(View.GONE);
                holder.earlyRelease.setVisibility(View.GONE);
            }
            else if(day.dayType.equals(dayOptions[1]))
            {
                holder.activityPeriod.setVisibility(View.VISIBLE);
                holder.earlyRelease.setVisibility(View.GONE);
            }
            else if(day.dayType.equals(dayOptions[2]))
            {
                holder.activityPeriod.setVisibility(View.GONE);
                holder.activityPeriod.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.activityPeriod.setVisibility(View.VISIBLE);
                holder.earlyRelease.setVisibility(View.VISIBLE);
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
        public NRDay listeningDay;

        public DayRowListener(NRDay day)
        {
            listeningDay = day;
        }

        public void onClick(View v)
        {
            rowClicked(listeningDay, v);
        }
    }
}
