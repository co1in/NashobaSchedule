package com.ctrmksw.nashobaschedule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.SchoolDayManager;
import com.ctrmksw.nashobaschedule.ScheduleUtils.nr.NRDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.nr.NRSchedule;
import com.ctrmksw.nashobaschedule.ScheduleUtils.special.SpecialDay;
import com.ctrmksw.nashobaschedule.ScheduleUtils.special.SpecialDayManager;
import com.ctrmksw.nashobaschedule.database.MySchedule;
import com.ctrmksw.nashobaschedule.database.ScheduleDbHelper;
import com.ctrmksw.nashobaschedule.network.RemoteFile;
import com.ctrmksw.nashobaschedule.network.ServerRequest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;


public class AgendaActivity extends FragmentActivity
{
    private static final String SPECIAL_DAYS_FILE_NAME = "specialdays";

    private static MySchedule mySchedule;

    private static ScheduleDbHelper database;

    private ViewPager rootPager;
    private AgendaPagerAdapter agendaPagerAdapter;

    private boolean databaseCheck = false;

    private View updatesCard = null;

    private ArrayList<String> localSpecialsCopy = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(!SchedPrefs.getHasSavedSchedule(this))
        {
            Intent i = new Intent(this, ConfigureScheduleActivity.class);
            startActivity(i);
            finish();
            return;
        }

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_agenda);

        try
        {
            SchoolDayManager.loadedNRDays(NRSchedule.getSchedule(getAssets().open("sched.txt")));
        } catch (IOException e)
        {
            Log.e("Schedule read error", e.getCause().toString());
            finish();
            return;
        }

        database = new ScheduleDbHelper(this);

        updatesCard = findViewById(R.id.main_updates_card);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(rootPager != null)
        {
            rootPager.setVisibility(View.GONE);
        }

        loadSpecialDaysFile();

        new ServerRequest(new ServerRequest.ServerResult()
        {
            @Override
            public void onResult(RemoteFile result)
            {
                if(result.error == null)
                {
                    loadedRemoteFile(result);
                }
                else
                {
                    Log.d(AgendaActivity.class.getName(), "Specials Network Error", result.error);
                }
            }
        }).execute();

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
             int index = data.getIntExtra(CalendarActivity.EXTRA_CLICKED_INDEX, -1);

            rootPager.setCurrentItem(SchoolDayManager.getIndexOf(SchoolDayManager.getCurrentList().get(index)));
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
        else if(id == R.id.action_settings)
        {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        /*else if(id == R.id.menu_goto_today)
        {
            rootPager.setCurrentItem(getSchedIndexFor(getNextSchoolDay()));
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void loadSpecialDaysFile()
    {
        ArrayList<SpecialDay> specialDays = new ArrayList<>();
        localSpecialsCopy.clear();
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(openFileInput(SPECIAL_DAYS_FILE_NAME)));
            String line;
            while((line = in.readLine()) != null)
            {
                localSpecialsCopy.add(line);
                specialDays.add(SpecialDay.fromString(line));
            }

            SpecialDayManager.loadSpecialDays(specialDays);
        }
        catch (IOException e)
        {
            SpecialDayManager.removeSpecialDays();
        }
    }

    private void overwriteSpecialDaysFile(ArrayList<String> list)
    {
        deleteFile(SPECIAL_DAYS_FILE_NAME);
        try
        {
            PrintWriter out = new PrintWriter(openFileOutput(SPECIAL_DAYS_FILE_NAME, Context.MODE_PRIVATE), true);
            for(String line : list)
            {
                out.println(line);
            }
            out.flush();
            out.close();
        }
        catch (FileNotFoundException e)
        {
            //Toast.makeText(AgendaActivity.this, "Error saving new file from server", Toast.LENGTH_SHORT).show();
        }
    }

    //Checks to see if the remote file is different than the local one.
    //If it is, then an app reload occurs
    private void loadedRemoteFile(RemoteFile file)
    {
        boolean isDifferent = file.size() != SpecialDayManager.getSpecialDaysSize();

        //Do an actual content comparison
        if(!isDifferent)
        {
            for(int i = 0; i < file.size(); i++)
            {
                if(file.get(i).trim().equals(""))
                {
                    file.remove(i);
                    i--;
                }
            }

            for(int i = 0; i < file.size(); i++)
            {
                boolean matchFound = false;
                for(int p = 0; p < localSpecialsCopy.size(); p++)
                {
                    if( file.get(i).trim().equals(localSpecialsCopy.get(p).trim()) )
                    {
                        localSpecialsCopy.remove(p);
                        matchFound = true;

                        //Breaks out of the inner loop
                        break;
                    }
                }
                if(!matchFound)
                {
                    isDifferent = true;
                    break;
                }
            }
        }

        if(isDifferent)
        {
            overwriteSpecialDaysFile(file);

            synchronized (this)
            {
                TranslateAnimation slideOut = new TranslateAnimation(0, 0, 0, getScreenHeight());
                slideOut.setDuration(300);
                slideOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rootPager.setVisibility(View.GONE);

                        loadSpecialDaysFile();
                        agendaPagerAdapter.notifyDataSetChanged();

                        TranslateAnimation translate = new TranslateAnimation(0, 0, getScreenHeight(), 0);
                        translate.setInterpolator(new DecelerateInterpolator(2f));
                        translate.setDuration(300);
                        translate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                updatesCard.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                TranslateAnimation translate = new TranslateAnimation(0, 0, 0, getScreenHeight());
                                                translate.setInterpolator(new AccelerateInterpolator(2f));
                                                translate.setDuration(300);
                                                translate.setAnimationListener(new Animation.AnimationListener() {
                                                    @Override
                                                    public void onAnimationStart(Animation animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animation animation) {
                                                        updatesCard.setVisibility(View.GONE);
                                                        rootPager = null;
                                                        showAgenda();
                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animation animation) {

                                                    }
                                                });
                                                updatesCard.startAnimation(translate);
                                            }
                                        });
                                    }
                                }).start();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        updatesCard.startAnimation(translate);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                if(inSwipeUp || !didSwipeUp)
                {
                    afterSwipeUpFadeOut = slideOut;
                }
                else
                {
                    rootPager.startAnimation(slideOut);
                }
            }
        }
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
                    public void run()
                    {
                        synchronized (this)
                        {
                            showAgenda();
                        }
                    }
                });

            }
        });
    }

    private boolean didSwipeUp = false;
    private boolean inSwipeUp = false;
    private Animation afterSwipeUpFadeOut = null;

    private void showAgenda()
    {
        if(rootPager == null)
        {
            rootPager = (ViewPager)findViewById(R.id.pager);
            agendaPagerAdapter = new AgendaPagerAdapter(getSupportFragmentManager());
            rootPager.setAdapter(agendaPagerAdapter);
            rootPager.setPageTransformer(true, new ZoomOutPageTransformer());

            rootPager.setCurrentItem(SchoolDayManager.getIndexOf(SchoolDayManager.getNextSchoolDay()));
        }

        int index = rootPager.getCurrentItem();
        rootPager.setAdapter(agendaPagerAdapter);
        rootPager.setCurrentItem(index);

        inSwipeUp = true;
        TranslateAnimation animation = new TranslateAnimation(0, 0, getScreenHeight(), 0);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator(2f));
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                rootPager.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                if(afterSwipeUpFadeOut != null)
                {
                    rootPager.startAnimation(afterSwipeUpFadeOut);
                    afterSwipeUpFadeOut = null;
                }
                else if(!SchedPrefs.getHasViewedSlideyHint(AgendaActivity.this))
                {
                    View tip = findViewById(R.id.slidey_tip_root);
                    //If it's already visible, probably coming from and onResume()
                    if(tip.getVisibility() != View.VISIBLE)
                    {
                        tip.setVisibility(View.VISIBLE);
                        findViewById(R.id.slidey_tip_root).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                findViewById(R.id.slidey_tip_root).setVisibility(View.GONE);
                                SchedPrefs.didViewSlidey(AgendaActivity.this);
                            }
                        });

                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(350);
                        tip.startAnimation(anim);
                    }
                }

                inSwipeUp = false;
                didSwipeUp = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rootPager.startAnimation(animation);
    }

    private int getScreenHeight()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
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
