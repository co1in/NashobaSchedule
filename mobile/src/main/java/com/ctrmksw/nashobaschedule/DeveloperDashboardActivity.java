package com.ctrmksw.nashobaschedule;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ctrmksw.nashobaschedule.ScheduleUtils.special.DayType;
import com.ctrmksw.nashobaschedule.network.DeveloperActionRequest;

import java.util.ArrayList;
import java.util.Calendar;


public class DeveloperDashboardActivity extends Activity
{

    private final String FRAG_TAG_TIME_PICKER = "fragmentTimePicker";
    private final String FRAM_CALENDAR_PICKER = "fragmentCalendarPicker";

    Button dayTypeButton;
    String[] dayOptions = {"Snow Day", "2 Hr. Delay", "Other", "Delete"};

    private EditText dayLabel, lunchField;
    private Button addButton, deleteButton, rotationButton;
    private TextView dateLabel;
    private ViewGroup classesLayout;

    private ArrayList<DeveloperClassView> classViews;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_dashboard);
        if(getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        classViews = new ArrayList<>();
        lunchField = (EditText) findViewById(R.id.dev_dash_lunch_field);
        dayLabel = (EditText)findViewById(R.id.dev_dash_day_label);
        addButton = (Button)findViewById(R.id.dev_add_btn);
        deleteButton = (Button)findViewById(R.id.dev_delete_btn);
        dateLabel = (TextView)findViewById(R.id.dev_dash_date_text);
        rotationButton = (Button)findViewById(R.id.dev_rotation_btn);
        classesLayout = (ViewGroup)findViewById(R.id.dev_classes_layout);

        findViewById(R.id.dev_dash_date_btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        dayTypeButton = (Button)findViewById(R.id.dev_dash_day_type_btn);
        dayTypeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String s = dayTypeButton.getText().toString();
                for(int i = 0; i < dayOptions.length; i++)
                {
                    if(s.equals(dayOptions[i]))
                    {
                        dayTypeButton.setText(dayOptions[(i+1)%dayOptions.length]);
                        break;
                    }
                }

                if(dayTypeButton.getText().toString().equals(dayOptions[2]))
                {
                    findViewById(R.id.dev_other_configuration_layout).setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.dev_other_configuration_layout).setVisibility(View.GONE);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                classViews.add(new DeveloperClassView(getLayoutInflater(), classViews.size(), new DeveloperClassView.OnTimeClickListener()
                {
                    @Override
                    public void onTimeClick(int index, boolean isStartTime)
                    {
                        currentTimeIndex = index;
                        currentTimeIsStart = isStartTime;

                        DialogFragment newFragment = new TimePickerFragment();
                        newFragment.show(getFragmentManager(), "timePicker");
                    }
                }));
                classesLayout.addView(classViews.get(classViews.size()-1).getView());
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(classViews.size() != 0)
                {
                    View removing = classViews.get(classViews.size()-1).getView();
                    classesLayout.removeView(removing);
                    classViews.remove(classViews.size()-1);
                }
            }
        });

        rotationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Integer current = Integer.parseInt(rotationButton.getText().toString());
                rotationButton.setText("" + ((current%8)+1));
            }
        });
    }

    private int currentTimeIndex;
    private boolean currentTimeIsStart;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_developer_dashboard, menu);
        return true;
    }


    private final String PASSCODE = "4@+y3qf=";

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_send_dev:
            {
                String action = "";

                if(month == -1 || day == -1 || year == -1)
                {
                    showToast("Please enter a date");
                    return true;
                }
                String extras = month + "/" + day + "/" + year + " ";

                String option = dayTypeButton.getText().toString();
                if(option.equals(dayOptions[0]))
                {
                    action = "daymod";
                    extras += DayType.SnowDay.name();
                }
                else if(option.equals(dayOptions[1]))
                {
                    action = "daymod";
                    extras += DayType.TwoHrDelay.name();
                }
                else if(option.equals(dayOptions[2]))
                {
                    action = "daymod";
                    if(dayLabel.getText().toString().trim().equals(""))
                        showToast("Fill in the day label field");
                    else if(dayLabel.getText().toString().contains("~"))
                        showToast("Please do not use '~' in the day label");
                    else
                    {
                        extras += DayType.Other.name() + " " + dayLabel.getText().toString().replace(" ", "~") + " ";

                        for(int i = 0; i < classViews.size(); i++)
                        {
                            extras += classViews.get(i).getClassType().name() + "-" + classViews.get(i).getStartTime() + "-" + classViews.get(i).getEndTime() + " ";
                        }

                        int lunchPeriod = -1;
                        try
                        {
                            lunchPeriod = Integer.parseInt(lunchField.getText().toString()) - 1;
                        }
                        catch (NumberFormatException e)
                        {
                        }
                        int rotationPeriod = Integer.parseInt(rotationButton.getText().toString());

                        extras += rotationPeriod + " " + lunchPeriod;
                    }

                }
                else if(option.equals(dayOptions[3]))
                {
                    action = "deleteDay";
                    extras = extras.trim();
                }

                new DeveloperActionRequest(this).execute(action, extras, PASSCODE);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private int year = -1, month = -1, day = -1;

    public void onDateSet(int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day = day;

        dateLabel.setText("Date: " + (month+1) + "/" + day + "/" + year);
    }

    public void onTimeSet(int i, int i2)
    {
        if(currentTimeIsStart)
            classViews.get(currentTimeIndex).setStartTime(i, i2);
        else
            classViews.get(currentTimeIndex).setEndTime(i, i2);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            ((DeveloperDashboardActivity)getActivity()).onDateSet(year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            ((DeveloperDashboardActivity)getActivity()).onTimeSet(hourOfDay, minute);
        }
    }
}