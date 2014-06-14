package com.android.meminder3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ActivitiesDatabaseHelper extends SQLiteOpenHelper 
{
	
	private static final String TABLE_NAME = "activities_tables.db";
	private static final int DB_VERSION = 1;

	public ActivitiesDatabaseHelper(Context context) 
	{
		super(context, TABLE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		ActivitiesTables.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		ActivitiesTables.onUpgrade(db, oldVersion, newVersion);	
	}
}
