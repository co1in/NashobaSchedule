package com.ctrmksw.nashobaschedule.powerschool;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Colin on 12/20/2014.
 */
public class PowerschoolPrefs
{
    public static PowerschoolPrefs get(Context context)
    {
        return new PowerschoolPrefs(context);
    }

    private Context context;
    private String PREFS_NAME = "a3sldkfjen2ipdlwe7fdfjs";
    private String PREF_USER = "a";
    private String PREF_PASSWORD = "b";

    private PowerschoolPrefs(Context context)
    {
        this.context = context;
    }

    public String getUserName()
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return e(base64decode(prefs.getString(PREF_USER, "")), b);
    }

    public void setUserName(String userName)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        userName = base64encode(e(userName, b));
        edit.putString(PREF_USER, userName);
        edit.apply();
    }

    public String getPassword()
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return e(base64decode(prefs.getString(PREF_PASSWORD, "")), b);
    }

    public void setPassword(String password)
    {
        SharedPreferences.Editor edit = context.getSharedPreferences(PREFS_NAME, 0).edit();
        password = base64encode(e(password, b));
        edit.putString(PREF_PASSWORD, password);
        edit.apply();
    }

    private static String base64encode(String text)
    {
        try
        {
            return new String(Base64.encode(text.getBytes("UTF-8"), Base64.DEFAULT));
        }
        catch ( UnsupportedEncodingException e ) {
            return null;
        }
    }

    private static String base64decode(String text){

        try {
            return new String(Base64.decode(text.getBytes(), Base64.DEFAULT), "UTF-8");
        }
        catch ( IOException e ) {
            return null;
        }

    }

    private String e(String a1, String a2)
    {
        try {
            if (a1==null || a2==null ) return null;

            char[] l2=a2.toCharArray();
            char[] l1=a1.toCharArray();

            int ml=l1.length;
            int kl=l2.length;
            char[] v1=new char[ml];

            for (int i=0; i<ml; i++){
                v1[i]=(char)(l1[i]^l2[i%kl]);
            }
            return new String(v1);
        }
        catch ( Exception e ) {
            return null;
        }
    }

    String b="e&pG1(Y2PE5=mSF?|,;waAIe8pcWd%";
}
