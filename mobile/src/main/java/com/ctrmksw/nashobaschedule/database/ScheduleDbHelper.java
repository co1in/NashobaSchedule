package com.ctrmksw.nashobaschedule.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Colin on 11/21/2014.
 */
public class ScheduleDbHelper
{
    private DbHelper database;
    public ScheduleDbHelper(Context context)
    {
        database = new DbHelper(context);
    }

    public void getClassInfo(QueryRunnable whenFinished)
    {
        new Thread(new ObjectRunnable(whenFinished)
        {
            @Override
            public void run()
            {
                synchronized (database)
                {
                    MySchedule classMap = new MySchedule();

                    SQLiteDatabase db = database.getReadableDatabase();

                    String[] projection = {};

                    Cursor c = db.query(DbHelper.TABLE_NAME, projection, null, null, null, null, null);
                    if(c.moveToFirst())
                    {
                        do
                        {
                            DatabasePeriodName periodName = DatabasePeriodName.valueOf(c.getString(c.getColumnIndex(DbHelper.COLUMN_NAME_PERIOD)));
                            String className = c.getString(c.getColumnIndex(DbHelper.COLUMN_NAME_CLASS_NAME));
                            int lunchPeriod = c.getInt(c.getColumnIndex(DbHelper.COLUMN_NAME_LUNCH_PERIOD));
                            boolean[] activatedClasses = fromActivatedString(c.getString(c.getColumnIndex(DbHelper.COLUMN_NAME_ACTIVATED_DAYS)));
                            DatabaseNRClass classInfo = new DatabaseNRClass(periodName, className, lunchPeriod, activatedClasses);

                            classMap.add(classInfo);
                        }while(c.moveToNext());
                    }


                    ((QueryRunnable)objs[0]).run(classMap);
                }
            }
        }).start();
    }

    public interface QueryRunnable
    {
        public void run(MySchedule map);
    }

    public void updateDatabase(MySchedule info, Runnable whenFinished)
    {
        new Thread(new ObjectRunnable(info, whenFinished)
        {
            @Override
            public void run()
            {
                synchronized (database)
                {
                    database.clearTable();
                    SQLiteDatabase db = database.getWritableDatabase();

                    MySchedule info = (MySchedule) objs[0];
                    for(DatabaseNRClass dbClass : info)
                    {
                        ContentValues values = new ContentValues();
                        values.put(DbHelper.COLUMN_NAME_PERIOD, dbClass.getPeriodName().name());
                        values.put(DbHelper.COLUMN_NAME_CLASS_NAME, dbClass.getClassName());
                        values.put(DbHelper.COLUMN_NAME_LUNCH_PERIOD, dbClass.getLunchPeriod());
                        values.put(DbHelper.COLUMN_NAME_ACTIVATED_DAYS, toActivatedString(dbClass.getActivatedArray()));

                        db.insert(DbHelper.TABLE_NAME, null, values);
                    }

                    ((Runnable)objs[1]).run();
                }
            }
        }).start();
    }

    private String toActivatedString(boolean[] activatedDays)
    {
        String s = "";
        for(int i = 0; i < activatedDays.length; i++)
        {
            if(activatedDays[i])
                s += i;
        }
        return s;
    }

    private boolean[] fromActivatedString(String s)
    {
        boolean[] activated = new boolean[s.length()];
        for(int i = 0; i < s.length(); i++)
        {
            activated[Integer.parseInt("" + s.charAt(i))] = true;
        }
        return activated;
    }

    private abstract class ObjectRunnable implements Runnable
    {
        protected Object objs[];
        public ObjectRunnable(Object... obj)
        {
            this.objs = obj;
        }
    }

    private class DbHelper extends SQLiteOpenHelper
    {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "NashobaScheduleDatabase.db";

        private static final String TEXT_TYPE = " TEXT", INT_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";

        static final String TABLE_NAME = "classes";
        static final String COLUMN_NAME_PERIOD = "PeriodName";
        static final String COLUMN_NAME_CLASS_NAME = "ClassName";
        static final String COLUMN_NAME_LUNCH_PERIOD = "LunchPeriod";
        static final String COLUMN_NAME_ACTIVATED_DAYS = "ActivatedDays";

        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_PERIOD + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_CLASS_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LUNCH_PERIOD + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_ACTIVATED_DAYS + TEXT_TYPE + " )";
        private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public DbHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            onUpgrade(db, oldVersion, newVersion);
        }

        public void clearTable()
        {
            getWritableDatabase().execSQL(SQL_DELETE_ENTRIES);
            getWritableDatabase().execSQL(SQL_CREATE_ENTRIES);
        }
    }
}
