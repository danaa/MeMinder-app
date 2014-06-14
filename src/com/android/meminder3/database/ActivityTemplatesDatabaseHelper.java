package com.android.meminder3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ActivityTemplatesDatabaseHelper extends SQLiteOpenHelper 
{
	private static final String TABLE_NAME = "templates_tables.db";
	private static final int DB_VERSION = 1;

	public ActivityTemplatesDatabaseHelper(Context context) 
	{
		super(context, TABLE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		ActivityTemplatesTables.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		ActivityTemplatesTables.onUpgrade(db, oldVersion, newVersion);	
	}

}
