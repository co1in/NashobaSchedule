package com.ctrmksw.nashobaschedule;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.ctrmksw.nashobaschedule.ScheduleUtils.ClassType;

/**
 * Created by Colin on 12/14/2014.
 */
public class DeveloperClassView
{
    private Button letter, t1, t2;
    private int index;
    private OnTimeClickListener listener;
    View root;

    public DeveloperClassView(LayoutInflater inflater, final int index, OnTimeClickListener listener)
    {
        root = inflater.inflate(R.layout.view_developer_class, null);

        letter = (Button)root.findViewById(R.id.dev_class_letter);

        this.listener = listener;

        letter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String currentText = letter.getText().toString();
                for(int i = 0; i < ClassType.values().length; i++)
                {
                    if(ClassType.values()[i].name().equals(currentText))
                    {
                        letter.setText(ClassType.values()[(i+1)%ClassType.values().length].name());
                    }
                }
            }
        });

        t1 = (Button)root.findViewById(R.id.dev_class_start_time);
        t1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DeveloperClassView.this.listener.onTimeClick(index, true);
            }
        });

        t2 = (Button)root.findViewById(R.id.dev_class_end_time);
        t2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DeveloperClassView.this.listener.onTimeClick(index, false);
            }
        });
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }

    public interface OnTimeClickListener
    {
        public void onTimeClick(int index, boolean isStartTime);
    }

    public ClassType getClassType()
    {
        return ClassType.valueOf(letter.getText().toString());
    }

    public String getStartTime()
    {
        return t1.getText().toString();
    }

    public String getEndTime()
    {
        return t2.getText().toString();
    }

    public void setStartTime(int hours, int minutes)
    {
        t1.setText(hours + ":" + String.format("%02d", minutes));
    }

    public void setEndTime(int hours, int minutes)
    {
        t2.setText(hours + ":" + String.format("%02d", minutes));
    }

    public View getView()
    {
        return root;
    }
}
