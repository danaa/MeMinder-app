package com.android.meminder3.database;


import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


public class ActivitiesTables 
{
	
	// tasks table
	public static final String TABLE_TASKS = "tasks_table";
	public static final String COLUMN_ID = BaseColumns._ID; // key
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_PREP = "prep_word";
	public static final String COLUMN_SUBJECT = "subject";
	public static final String COLUMN_OPTIONAL = "optional_info";
	
	// events table
	public static final String TABLE_EVENTS = "events_table";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_HOUR = "hour";
	public static final String COLUMN_MINUTES = "minutes";
	public static final String COLUMN_NUMBER_NOTIFICATION_DAYS = "notification_days";
	public static final String COULMN_DAY = "day";
	public static final String COLUMN_MONTH = "month";
	public static final String COLUMN_YEAR = "year";
	public static final String COLUMN_REPEAT_DAYS = "repeat_days";
	
	
	private static final String CREATE_TASKS_TABLE = "create table "
			+ TABLE_TASKS
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_TITLE + " string not null, "
			+ COLUMN_PREP + " string, "
			+ COLUMN_SUBJECT + " string, "
			+ COLUMN_OPTIONAL + " string"
			+ ");";
	
	private static final String CREATE_EVENTS_TABLE = "create table "
			+ TABLE_EVENTS
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_TYPE + " string not null,"
			+ COLUMN_TITLE + " string not null, "
			+ COLUMN_PREP + " string, "
			+ COLUMN_SUBJECT + " string, "
			+ COLUMN_OPTIONAL + " string,"
			+ COLUMN_HOUR + " integer,"
			+ COLUMN_MINUTES + " integer,"
			+ COLUMN_NUMBER_NOTIFICATION_DAYS + " string,"
			+ COULMN_DAY + " integer,"
			+ COLUMN_MONTH + " integer,"
			+ COLUMN_YEAR + " integer,"
			+ COLUMN_REPEAT_DAYS + " string"
			+ ");";
	
	// methods

	public static void onCreate(SQLiteDatabase database) // called only once, when user first run the app
	{
		database.execSQL(CREATE_TASKS_TABLE);
		database.execSQL(CREATE_EVENTS_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)  // erases all data on db upgrade, TODO: need to find a way to save it
	{
		database.execSQL("drop table if exists " + TABLE_TASKS);
		database.execSQL("drop table if exists " + TABLE_EVENTS);
		onCreate(database);
	}
	
}
