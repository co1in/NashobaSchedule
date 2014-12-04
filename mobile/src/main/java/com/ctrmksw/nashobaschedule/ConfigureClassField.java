package com.ctrmksw.nashobaschedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ctrmksw.nashobaschedule.database.DatabaseNRClass;
import com.ctrmksw.nashobaschedule.database.DatabasePeriodName;

/**
 * Created by Colin on 11/28/2014.
 */
public class ConfigureClassField
{
    private ViewGroup root;
    private EditText mainClassField, altClassField;
    private TextView headerText;
    private View editDaysButton;
    private ViewGroup bottomLayout;
    private boolean[] activatedDays = {true, true, true, true, true, true, true, true};
    private boolean[] tempActivatedDays;

    private Button mainClassLunch, altClassLunch;

    private DatabasePeriodName periodName;

    public ConfigureClassField(final Context context, DatabasePeriodName period)
    {
        this.periodName = period;

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = (ViewGroup)inflater.inflate(R.layout.view_configure_class, null);

        mainClassField = (EditText) root.findViewById(R.id.configure_main_class_name_edit);
        altClassField = (EditText)root.findViewById(R.id.configure_alternate_class_name_edit);

        headerText = (TextView)root.findViewById(R.id.configure_view_period);
        headerText.setText(period.name() + " Period");

        editDaysButton = root.findViewById(R.id.configure_edit_days_img);
        editDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                tempActivatedDays = activatedDays.clone();

                AlertDialog.Builder build = new AlertDialog.Builder(context);
                build.setTitle("Primary Class Days");
                build.setMultiChoiceItems(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"}, activatedDays.clone(), new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked)
                    {
                        tempActivatedDays[which] = isChecked;
                    }
                });

                build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                build.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        activatedDays = tempActivatedDays;
                        checkDayActivatedChange();
                    }
                });

                build.show();
            }
        });

        bottomLayout = (ViewGroup)root.findViewById(R.id.configure_bottom_layout);

        mainClassLunch = (Button)root.findViewById(R.id.configure_main_class_lunch);
        mainClassLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(mainClassLunch.getText().toString());
                mainClassLunch.setText(String.valueOf((current%4) + 1));
            }
        });

        altClassLunch = (Button)root.findViewById(R.id.configure_alternate_class_lunch);
        altClassLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = Integer.parseInt(altClassLunch.getText().toString());
                altClassLunch.setText(String.valueOf((current%4) + 1));
            }
        });
    }

    private void checkDayActivatedChange()
    {
        if(contains(activatedDays, false))
        {
            bottomLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            bottomLayout.setVisibility(View.GONE);
        }
    }

    private boolean contains(boolean[] array, boolean value)
    {
        for(boolean b : array)
        {
            if(value == b)
                return true;
        }
        return false;
    }

    private boolean[] inverse(boolean[] array)
    {
        boolean[] temp = new boolean[array.length];
        for(int i = 0; i < array.length; i++)
            temp[i] = !array[i];
        return temp;
    }

    public ViewGroup getRootLayout()
    {
        return root;
    }

    public boolean hasAlternateClass()
    {
        return bottomLayout.getVisibility() == View.VISIBLE;
    }

    public DatabaseNRClass getMainClass()
    {
        return new DatabaseNRClass(periodName, mainClassField.getText().toString(), Integer.parseInt(mainClassLunch.getText().toString()), activatedDays);
    }

    public DatabaseNRClass getAlternateClass()
    {
        if(!hasAlternateClass())
            throw new RuntimeException("Tried to access invisible alternate class on schedule configuration page");

        return new DatabaseNRClass(periodName, altClassField.getText().toString(), Integer.parseInt(altClassLunch.getText().toString()), inverse(activatedDays));
    }

    public String validate()
    {
        if(mainClassField.getText().toString().trim().equals("") || (hasAlternateClass() && altClassField.getText().toString().trim().equals("")))
            return "Please fill in all class names";
        return null;
    }

    public void setMainClassField(String text)
    {
        mainClassField.setText(text);
    }

    public void setAltClassField(String text)
    {
        altClassField.setText(text);
    }

    public void setMainClassLunch(int lunch)
    {
        mainClassLunch.setText(String.valueOf(lunch));
    }

    public void setAltClassLunch(int lunch)
    {
        altClassLunch.setText(String.valueOf(lunch));
    }

    public void setActivatedDays(boolean[] activatedDays)
    {
        this.activatedDays = activatedDays;
        checkDayActivatedChange();
    }

    public static ConfigureClassField load(Context context, DatabaseNRClass mainClass, DatabaseNRClass altClass)
    {
        ConfigureClassField classField = new ConfigureClassField(context, mainClass.getPeriodName());

        classField.setMainClassField(mainClass.getClassName());
        classField.setMainClassLunch(mainClass.getLunchPeriod());
        classField.setActivatedDays(mainClass.getActivatedArray());

        if(altClass != null)
        {
            classField.setAltClassField(altClass.getClassName());
            classField.setAltClassLunch(altClass.getLunchPeriod());
        }

        return classField;
    }
}
