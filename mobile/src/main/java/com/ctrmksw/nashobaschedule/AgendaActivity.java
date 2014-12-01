package com.ctrmksw.nashobaschedule;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.ctrmksw.nashobaschedule.ScheduleUtils.NRDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.NRSchedule;
import com.ctrmksw.nashobaschedule.database.MySchedule;
import com.ctrmksw.nashobaschedule.database.ScheduleDbHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class AgendaActivity extends FragmentActivity
{
    private static MySchedule mySchedule;

    private static ScheduleDbHelper database;

    private static ArrayList<NRDay> sched;

    private ViewPager rootPager;
    private AgendaPagerAdapter agendaPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(!SchedPrefs.getIsSetup(this))
        {
            Intent i = new Intent(this, ConfigureScheduleActivity.class);
            startActivity(i);
            finish();
        }

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_agenda);

        try
        {
            sched = NRSchedule.getSchedule(getAssets().open("sched.txt"), null);
        } catch (IOException e)
        {
            Log.e("Schedule read error", e.getCause().toString());
            finish();
        }

        database = new ScheduleDbHelper(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(rootPager != null)
            rootPager.setVisibility(View.INVISIBLE);

        updateMyClassList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agenda, menu);
        return true;
    }

    private final int CALENDAR_PICK_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == CALENDAR_PICK_REQUEST && resultCode == RESULT_OK)
        {
            NRDay clickedDay = NRDay.fromString(data.getExtras().getString(CalendarActivity.EXTRA_NRDAY));
            rootPager.setCurrentItem(getSchedIndexFor(clickedDay));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_calendar)
        {
            Intent i = new Intent(this, CalendarActivity.class);
            startActivityForResult(i, CALENDAR_PICK_REQUEST);
            return true;
        }
        else if(id == R.id.menu_edit_schedule)
        {
            Intent i = new Intent(this, ConfigureScheduleActivity.class);
            startActivity(i);
        }
        else if(id == R.id.menu_goto_today)
        {
            rootPager.setCurrentItem(getSchedIndexFor(getNextSchoolDay()));
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMyClassList()
    {
        database.getClassInfo(new ScheduleDbHelper.QueryRunnable()
        {
            @Override
            public void run(MySchedule map)
            {
                AgendaActivity.mySchedule = map;


                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAgenda();
                    }
                });
            }
        });
    }

    private void showAgenda()
    {
        if(rootPager == null)
        {
            rootPager = (ViewPager)findViewById(R.id.pager);
            agendaPagerAdapter = new AgendaPagerAdapter(getSupportFragmentManager());
            rootPager.setAdapter(agendaPagerAdapter);
            rootPager.setPageTransformer(true, new ZoomOutPageTransformer());

            rootPager.setCurrentItem(getSchedIndexFor(getNextSchoolDay()));
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        TranslateAnimation animation = new TranslateAnimation(0, 0, height, 0);
        animation.setDuration(600);
        animation.setInterpolator(new DecelerateInterpolator(1.5f));
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                rootPager.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rootPager.startAnimation(animation);
    }

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

            Bundle args = new Bundle();
            args.putString(AgendaFragment.ARG_NRDAY, getDay(i).toString());

            frag.setArguments(args);

            return frag;
        }

        @Override
        public int getCount()
        {
            return sched.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return "Schedule";
        }
    }

    public static NRDay getNextSchoolDay()
    {
        Calendar now = Calendar.getInstance();

        for(NRDay day : sched)
        {
            if(day.compareTo(now) >= 0)
            {
                if(day.compareTo(now) == 0)
                {
                    if(day.dayType.equals(NRSchedule.dayOptions[0]) || day.dayType.equals(NRSchedule.dayOptions[1]))
                    {
                        if(now.get(Calendar.HOUR_OF_DAY) > 14 || (now.get(Calendar.HOUR_OF_DAY) == 14 && now.get(Calendar.MINUTE) > 21))
                            continue;
                    }
                    else if(day.dayType.equals(NRSchedule.dayOptions[2]) || day.dayType.equals(NRSchedule.dayOptions[3]))
                    {
                        if(now.get(Calendar.HOUR_OF_DAY) > 11 || (now.get(Calendar.HOUR_OF_DAY) == 11 && now.get(Calendar.MINUTE) > 31))
                            continue;
                    }
                }

                return day;
            }
        }
        return null;
    }

    public static int getSchedIndexFor(NRDay day)
    {
        for(int i = 0; i < sched.size(); i++)
        {
            if(day.compareTo(sched.get(i).date) == 0)
                return i;
        }
        throw new RuntimeException("Error: NRDay passed to getIndexFor() was not found in the schedule");
    }

    public static NRDay getDay(int index)
    {
        return sched.get(index);
    }

    private void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public static MySchedule getMySchedule()
    {
        return mySchedule;
    }
}
