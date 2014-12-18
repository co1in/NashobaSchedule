package com.ctrmksw.nashobaschedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ctrmksw.nashobaschedule.database.DatabaseNRClass;
import com.ctrmksw.nashobaschedule.database.DatabasePeriodName;
import com.ctrmksw.nashobaschedule.database.MySchedule;
import com.ctrmksw.nashobaschedule.database.ScheduleDbHelper;

import java.util.ArrayList;


public class ConfigureScheduleActivity extends Activity
{
    public static String DATA_INVALID_KEY = "DataInvalidBoolean";

    private ScheduleDbHelper database;
    private LinearLayout root;

    private ArrayList<ConfigureClassField> configureClassFields;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(SchedPrefs.getHasSavedSchedule(this));

        setContentView(R.layout.activity_configure_schedule);

        root = (LinearLayout)findViewById(R.id.configure_root);

        database = new ScheduleDbHelper(this);
        database.getClassInfo(new ScheduleDbHelper.QueryRunnable()
        {
            @Override
            public void run(MySchedule map)
            {
                runOnUiThread(new ObjectRunnable(map)
                {
                    @Override
                    public void run()
                    {
                        MySchedule map = (MySchedule)obj[0];

                        configureClassFields = new ArrayList<ConfigureClassField>();

                        for (DatabasePeriodName period : DatabasePeriodName.values())
                        {
                            ConfigureClassField configureField;

                            DatabaseNRClass mainClass = map.getMainClass(period);
                            if(mainClass != null)
                            {
                                DatabaseNRClass altClass = map.getAltClass(period);
                                configureField = ConfigureClassField.load(ConfigureScheduleActivity.this, mainClass, altClass);
                            }
                            else
                            {
                                configureField = new ConfigureClassField(ConfigureScheduleActivity.this, period);
                            }

                            configureClassFields.add(configureField);
                        }


                        for(ConfigureClassField classField : configureClassFields)
                        {
                            root.addView(classField.getRootLayout());

                            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                            anim.setDuration(400);
                            classField.getRootLayout().startAnimation(anim);
                        }
                    }
                });
            }
        });
    }

    abstract class ObjectRunnable implements Runnable
    {
        protected Object[] obj;
        public ObjectRunnable(Object... obj)
        {
            this.obj = obj;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem cancel = menu.findItem(R.id.menu_configure_cancel);
        cancel.setVisible(SchedPrefs.getHasSavedSchedule(this));

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configure_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(!isSaving)
        {
            if(id == android.R.id.home || id == R.id.menu_configure_save)
            {
                save();
            }
            else if(id == R.id.menu_configure_cancel)
            {
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private volatile boolean isSaving = false;

    private void save()
    {
        MySchedule schedule = new MySchedule();
        for(int i = 0; i < configureClassFields.size(); i++)
        {
            String s = configureClassFields.get(i).validate();
            if(s != null)
            {
                showToast(s);
                return;
            }
            schedule.add(configureClassFields.get(i).getMainClass());
            if(configureClassFields.get(i).hasAlternateClass())
                schedule.add(configureClassFields.get(i).getAlternateClass());
        }

        database.updateDatabase(schedule, new Runnable()
        {
            @Override
            public void run()
            {
                if(!SchedPrefs.getHasSavedSchedule(ConfigureScheduleActivity.this))
                {
                    Intent i = new Intent(ConfigureScheduleActivity.this, AgendaActivity.class);
                    startActivity(i);
                }

                SchedPrefs.didSetupSchedule(ConfigureScheduleActivity.this);

                finish();
            }
        });
    }

    private void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
