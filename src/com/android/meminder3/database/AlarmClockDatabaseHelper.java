package com.android.meminder3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class AlarmClockDatabaseHelper extends SQLiteOpenHelper
{
	
	private static final String TABLE_NAME = "alarm_clock_table.db";
	private static final int DB_VERSION = 1;

	public AlarmClockDatabaseHelper(Context context) 
	{
		super(context, TABLE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) // called only once, when user first run the app
	{
		AlarmClockTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		AlarmClockTable.onUpgrade(db, oldVersion, newVersion);
	}

}
