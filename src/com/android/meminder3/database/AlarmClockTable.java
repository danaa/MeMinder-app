package com.android.meminder3.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


public class AlarmClockTable 
{
	// database table
	public static final String TABLE_ALARM_CLOCK = "alarm_clock";
	public static final String COLUMN_ID = BaseColumns._ID; // key
	public static final String COLUMN_HOUR = "hour";
	public static final String COLUMN_MINUTES = "minutes";
	public static final String COLUMN_SNOOZE = "snooze";
	public static final String COLUMN_IS_ON = "is_on";
	
	// default values
	private static final int HOUR = 8;
	private static final int MINUTES = 0;
	private static final int SNOOZE = 10;
	private static final int IS_ON = 0; 
	
	// SQL table creation statement
	private static final String TABLE_CREATE = "create table "
			+ TABLE_ALARM_CLOCK
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_HOUR + " integer not null, "
			+ COLUMN_MINUTES + " integer not null, "
			+ COLUMN_SNOOZE + " integer not null, "
			+ COLUMN_IS_ON + " integer not null"
			+ ");";
	
	// SQL insert default values statement
	private static final String INSERT_DEFAULT_VALUES = "insert into "
		+ TABLE_ALARM_CLOCK
		+ "(" + COLUMN_HOUR + "," + COLUMN_MINUTES + "," + COLUMN_SNOOZE + "," + COLUMN_IS_ON + ")"
		+ " values " 
		+ "(" + HOUR + "," + MINUTES + "," + SNOOZE + "," + IS_ON + ");";
	
	
	// methods
	
	public static void onCreate(SQLiteDatabase database) // called only once, when user first run the app
	{
		database.execSQL(TABLE_CREATE);
		database.execSQL(INSERT_DEFAULT_VALUES);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)  // erases all data on db upgrade, need to find a way to save it
	{
		database.execSQL("drop table if exists " + TABLE_ALARM_CLOCK);
		onCreate(database);
	}

			
}
