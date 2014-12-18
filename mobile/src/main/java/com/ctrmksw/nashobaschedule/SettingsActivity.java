package com.ctrmksw.nashobaschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ctrmksw.nashobaschedule.network.DeveloperLoginRequest;


public class SettingsActivity extends Activity
{
    private int versionLayoutClickCount = 0;
    private View versionLayout;
    private Toast appToast;
    private View longBlockStudyLayout;

    private CheckBox longBlockStudyBox;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            ((TextView) findViewById(R.id.settings_version_text)).setText("Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Version Number", "Could not find package name", e);
        }

        versionLayout = findViewById(R.id.settings_version_layout);
        if(SchedPrefs.getHasActivatedDeveloperPage(this))
        {
            findViewById(R.id.developer_login_divider).setVisibility(View.VISIBLE);
            findViewById(R.id.settings_developer_login_layout).setVisibility(View.VISIBLE);
            versionLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                }
            });
        }
        else
        {
            versionLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(versionLayoutClickCount < 10)
                    {
                        versionLayoutClickCount++;
                        if(versionLayoutClickCount == 10)
                        {
                            updateToast("Developer Page Unlocked");

                            findViewById(R.id.developer_login_divider).setVisibility(View.VISIBLE);
                            findViewById(R.id.settings_developer_login_layout).setVisibility(View.VISIBLE);
                            SchedPrefs.didActivateDeveloperPage(SettingsActivity.this);
                        }
                        else if(versionLayoutClickCount >= 5)
                        {
                            updateToast("You are " + (10 - versionLayoutClickCount) + " steps away from unlocking the developer page");
                        }
                    }
                }
            });
        }

        longBlockStudyLayout = findViewById(R.id.settings_lbstudy_layout);
        longBlockStudyBox = (CheckBox)findViewById(R.id.settings_lbstudy_checkbox);
        longBlockStudyBox.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                event.setLocation(event.getX() + v.getLeft(), event.getY() + v.getTop());
                longBlockStudyLayout.onTouchEvent(event);
                return true;
            }
        });
        longBlockStudyBox.setChecked(SchedPrefs.getHighlightLongblockStudy(SettingsActivity.this));

        longBlockStudyLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                longBlockStudyBox.setChecked(!longBlockStudyBox.isChecked());
                SchedPrefs.setHighlightLongBlockStudy(SettingsActivity.this, longBlockStudyBox.isChecked());
            }
        });

        findViewById(R.id.settings_edit_schedule_layout).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(SettingsActivity.this, ConfigureScheduleActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.settings_developer_login_layout).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder build = new AlertDialog.Builder(SettingsActivity.this);

                LayoutInflater inflater = getLayoutInflater();

                View dialogRoot = inflater.inflate(R.layout.dialog_developer_signin, null);
                final ProgressBar dialogProgress = (ProgressBar) dialogRoot.findViewById(R.id.dev_login_progress);
                final EditText dialogField = (EditText) dialogRoot.findViewById(R.id.dev_login_password_field);

                dialogField.setText(SchedPrefs.getSavedDeveloperPassword(SettingsActivity.this));

                build.setView(dialogRoot)
                        .setPositiveButton("Login", null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        });

                final AlertDialog d = build.create();
                d.setOnShowListener(new DialogInterface.OnShowListener()
                {
                    @Override
                    public void onShow(DialogInterface dialog)
                    {
                        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                String password = dialogField.getText().toString();

                                dialogField.setEnabled(false);
                                dialogProgress.setVisibility(View.VISIBLE);

                                new DeveloperLoginRequest(new DeveloperLoginRequest.AfterLogin()
                                {
                                    @Override
                                    public void afterLogin(boolean result)
                                    {
                                        if (result)
                                        {
                                            SchedPrefs.setSavedDeveloperPassword(SettingsActivity.this, dialogField.getText().toString());

                                            Intent i = new Intent(SettingsActivity.this, DeveloperDashboardActivity.class);
                                            startActivity(i);

                                            d.cancel();
                                        }
                                        else
                                        {
                                            dialogProgress.setVisibility(View.GONE);
                                            dialogField.setEnabled(true);
                                            updateToast("Incorrect Password");
                                        }
                                    }
                                }).execute(password);
                            }
                        });
                    }
                });

                d.show();
            }
        });
    }

    private void updateToast(String message)
    {
        if(appToast != null)
            appToast.cancel();
        appToast = Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT);
        appToast.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
