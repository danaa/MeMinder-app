package com.android.meminder3.database;


import java.util.logging.Logger;
import android.database.sqlite.SQLiteDatabase;


public class ActivityTemplatesTables 
{

	// template type table
	public static final String TABLE_TEMPLATES = "templates_table";
	public static final String COLUMN_TITLE = "_title"; // key
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_DEFAULT_PREP = "prep_word";
	public static final String COLUMN_NUMBER_NOTIFICATION_DAYS = "notification_days";
	
	// default template titles
	public static final String BIRTHDAY = "\'Birthday\'";
	public static final String WEDDING = "\'Wedding\'";
	public static final String COURSE = "\'Course\'";
	public static final String INTERVIEW = "\'Interview\'";
	public static final String EXAM = "\'Exam\'";
	public static final String MEETING = "\'Meeting\'";
	public static final String PAY_BILLS = "\'Pay bills\'";
	public static final String CALL = "\'Call\'";
	public static final String BUY = "\'Buy\'";
	
	// types
	public static final String REPEATING_WEEKLY = "\'repeating weekly\'";
	public static final String REPEATING_YEARLY = "\'repeating yearly\'";
	public static final String ONE_TIME = "\'one-time\'";
	public static final String TASK = "\'task\'";
	
	// default preposition words
	public static final String TO = "\'to\'";
	public static final String OF = "\'of\'";
	public static final String IN = "\'in\'";
	public static final String WITH = "\'with\'";
	

	// SQL template type table creation statement
	private static final String TYPE_TABLE_CREATE = "create table "
			+ TABLE_TEMPLATES
			+ "("
			+ COLUMN_TITLE + " string primary key, "
			+ COLUMN_TYPE + " string not null, "
			+ COLUMN_DEFAULT_PREP + " string, "
			+ COLUMN_NUMBER_NOTIFICATION_DAYS + " string"
			+ ");";
	
	public static final String TYPE_INSERT_PREFACE = "insert into "
			+ TABLE_TEMPLATES
			+ " (" + COLUMN_TITLE + "," + COLUMN_TYPE + "," + COLUMN_DEFAULT_PREP + "," + COLUMN_NUMBER_NOTIFICATION_DAYS + ") ";

	// SQL statement - insert default values to template type table 
	public static final String INSERT_DEFAULT_TYPE_VALUES = 
			  TYPE_INSERT_PREFACE 
			+ "select " + BIRTHDAY + "," + REPEATING_YEARLY + "," + TO + "," + "\'1 3\'" + " union "
			+ "select " + WEDDING + "," + ONE_TIME + "," + OF  + "," +  "\'1 7\'" + " union "
			+ "select " + COURSE + "," + REPEATING_WEEKLY + "," + IN  + "," + "null" + " union "
			+ "select " + INTERVIEW + "," + ONE_TIME + "," + WITH  + "," +  "\'1 3\'" + " union "	
			+ "select " + EXAM + "," + ONE_TIME + "," + IN  + "," +  "\'1 7\'" + " union "
			+ "select " + MEETING + "," + ONE_TIME + "," + WITH  + "," +  "\'1\'" + " union "
			+ "select " + PAY_BILLS + "," + TASK + "," + "null" + "," + "null" + " union "
			+ "select " + CALL + "," + TASK + "," + "null" + "," + "null" + " union "
			+ "select " + BUY + "," + TASK + "," + "null" + "," + "null" + ";";
	
	
	// SQL template type table creation statement
	/*private static final String NOTIFICATION_TABLE_CREATE = "create table "
			+ TABLE_TEMPLATE_NOTIFICATION
			+ "("
			+ COLUMN_TITLE + " string not null, "
			+ COLUMN_NUMBER_NOTIFICATION_DAYS + " integer not null "
			+ ");";*/
	
	/*private static final String NOTIFICATION_INSERT_PREFACE = "insert into "
			+ TABLE_TEMPLATE_NOTIFICATION
			+ "(" + COLUMN_TITLE + "," + COLUMN_NUMBER_NOTIFICATION_DAYS + ") ";*/
	
	// SQL statement - insert default values to template notification table
	/*private static final String INSERT_DEFAULT_NOTIFICATION_VALUES =
			  NOTIFICATION_INSERT_PREFACE
			+ "select " + BIRTHDAY + "," + 0 + " union "
			+ "select " + BIRTHDAY + "," + 1 + " union "
			+ "select " + BIRTHDAY + "," + 5 + " union "
			+ "select " + WEDDING + "," + 0 + " union "
			+ "select " + WEDDING + "," + 1 + " union "
			+ "select " + WEDDING + "," + 5 + " union "
			+ "select " + COURSE + "," + 0 + " union "
			+ "select " + INTERVIEW + "," + 0 + " union "
			+ "select " + INTERVIEW + "," + 1 + " union "
			+ "select " + INTERVIEW + "," + 3 + " union "
			+ "select " + EXAM + "," + 0 + " union "
			+ "select " + EXAM + "," + 1 + " union "
			+ "select " + EXAM + "," + 7 + " union "
			+ "select " + MEETING + "," + 0 + " union "
			+ "select " + MEETING + "," + 1 + ";";*/
	
			
	// methods

	public static void onCreate(SQLiteDatabase database) // called only once, when user first run the app
	{
		Logger.getAnonymousLogger().severe(INSERT_DEFAULT_TYPE_VALUES);
		database.execSQL(TYPE_TABLE_CREATE);
		database.execSQL(INSERT_DEFAULT_TYPE_VALUES);
		//database.execSQL(NOTIFICATION_TABLE_CREATE);
		//database.execSQL(INSERT_DEFAULT_NOTIFICATION_VALUES);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)  // erases all data on db upgrade, need to find a way to save it
	{
		database.execSQL("drop table if exists " + TABLE_TEMPLATES);
		//database.execSQL("drop table if exists " + TABLE_TEMPLATE_NOTIFICATION);
		onCreate(database);
	}

}
