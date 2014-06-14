package com.android.meminder3.components;


import java.util.ArrayList;
import java.util.Collections;
import com.android.meminder3.dataStractures.ActivityData;
import com.android.meminder3.dataStractures.ActivityTemplateData;
import com.android.meminder3.dataStractures.AlarmData;
import com.android.meminder3.dataStractures.MyDate;
import com.android.meminder3.dataStractures.AlarmData.AlarmStatus;
import com.android.meminder3.database.ActivitiesDatabaseHelper;
import com.android.meminder3.database.ActivitiesTables;
import com.android.meminder3.database.ActivityTemplatesDatabaseHelper;
import com.android.meminder3.database.ActivityTemplatesTables;
import com.android.meminder3.database.AlarmClockDatabaseHelper;
import com.android.meminder3.database.AlarmClockTable;
import com.android.meminder3.utils.utils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * This class represents the Database handler component,
 * a component that functions as an adapter between the system
 * and an external Android SQLite component.
 * This class is a singleton
 */
public class DatabaseHandler 
{
	// constants
	/**
	 * represents a create mode
	 */
	public static final int CREATE = 0;
	
	/**
	 * represents a modify mode
	 */
	public static final int MODIFY = 1;
	
	private static DatabaseHandler instance; 
	
	// all the database helpers
	AlarmClockDatabaseHelper _alarmClockDB;
	ActivitiesDatabaseHelper _activitiesDB;
	ActivityTemplatesDatabaseHelper _activityTemplatesDB;
	Boolean _isInitialized;
	
	
	// c'tor
	private DatabaseHandler()
	{
		_isInitialized = false;
	}
	
	//-------------------------------------------------------------------------------------------------------
	
	/**
	 * @return an instance of the database handler component
	 */
	public static DatabaseHandler getInstance()
	{
		if(instance == null)
		{
			instance = new DatabaseHandler();
		}
		return instance;
	}
	
	//-------------------------------------------------------------------------------------------------------
	
	/**
	 * Initializes this component
	 * must be called before any other function of this component
	 * @param context the context of the first class that requests any database operations
	 */
	public void init(Context context)
	{
		_alarmClockDB = new AlarmClockDatabaseHelper(context);
		_activitiesDB = new ActivitiesDatabaseHelper(context);
		_activityTemplatesDB = new ActivityTemplatesDatabaseHelper(context);
		
		// this action creates all the database's tables
		SQLiteDatabase db1 = _alarmClockDB.getReadableDatabase();
		SQLiteDatabase db2 = _activitiesDB.getReadableDatabase();
		SQLiteDatabase db3 = _activityTemplatesDB.getReadableDatabase();
		db1.close();
		db2.close();
		db3.close();
		
		_isInitialized = true;
	}
	
	//-------------------------------------------------------------------------------------------------------
		
	/**
	 * sets the alarm flag (on or off) in the DB according to the given value
	 * @param val the value to set
	 */
	public void changeAlarmFlagInDB(AlarmStatus val)
	{
		if(_isInitialized)
		{
			SQLiteDatabase writeableDb = _alarmClockDB.getWritableDatabase();

			writeableDb.beginTransaction();  
			ContentValues cv = new ContentValues();
			cv.put("is_on", val.ordinal());
			writeableDb.update("alarm_clock", cv, "_id = 1", null);
			
			writeableDb.setTransactionSuccessful();
			writeableDb.endTransaction();
			writeableDb.close();
		}
	}
	
	//-------------------------------------------------------------------------------------------------------
	
	/**
	 * saves the given alarm data in the DB
	 * @param data the alarm data to save in the DB
	 */
	public void saveAlarmInDB(AlarmData data)
	{
		if(_isInitialized)
		{
			SQLiteDatabase writeableDb = _alarmClockDB.getWritableDatabase();
		
			writeableDb.beginTransaction();
			ContentValues cv = new ContentValues();
			cv.put(AlarmClockTable.COLUMN_HOUR, data.getHour());
			cv.put(AlarmClockTable.COLUMN_MINUTES, data.getMinutes());
			cv.put(AlarmClockTable.COLUMN_SNOOZE, data.getSnooze());
			cv.put(AlarmClockTable.COLUMN_IS_ON, data.getStatus().ordinal()); // turns the alarm on
			writeableDb.update(AlarmClockTable.TABLE_ALARM_CLOCK, cv, "_id = 1", null); 

			writeableDb.setTransactionSuccessful();
			writeableDb.endTransaction();
			writeableDb.close();
		}
			
	}
	
	//-------------------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @return
	 */
	public AlarmData getAlarmFromDB()
	{
		AlarmData data = null;
		
		if(_isInitialized)
		{
			
			SQLiteDatabase readableDb = _alarmClockDB.getReadableDatabase();
			Cursor c = readableDb.rawQuery("SELECT * FROM " + AlarmClockTable.TABLE_ALARM_CLOCK + ";" , null);
			c.moveToNext();
	
			int hour = c.getInt(c.getColumnIndex(AlarmClockTable.COLUMN_HOUR));
			int minutes = c.getInt(c.getColumnIndex(AlarmClockTable.COLUMN_MINUTES));
			int snooze = c.getInt(c.getColumnIndex(AlarmClockTable.COLUMN_SNOOZE));
			int isOn = c.getInt(c.getColumnIndex(AlarmClockTable.COLUMN_IS_ON));
			AlarmStatus status = (isOn == AlarmStatus.ON.ordinal())? AlarmStatus.ON : AlarmStatus.OFF;
			
			data = new AlarmData(hour, minutes, snooze, status);
			
			c.close();
			readableDb.close();
		}
		return data;
	}
	
	//-------------------------------------------------------------------------------------------------------
	
	public boolean saveActivityTemplateInDB(ActivityTemplateData data, int mode) 
	{
		
		if(_isInitialized)
		{
			boolean saveSucceed = true;
			
			SQLiteDatabase writeableDb = _activityTemplatesDB.getWritableDatabase();
			writeableDb.beginTransaction();
			
			ContentValues cv = new ContentValues();
			cv.put(ActivityTemplatesTables.COLUMN_TITLE, data.getTitle());
			cv.put(ActivityTemplatesTables.COLUMN_DEFAULT_PREP, data.getDefaultPrepWord());
			
			String days = utils.intsArrayListToString(data.getNotificationDays());
			cv.put(ActivityTemplatesTables.COLUMN_NUMBER_NOTIFICATION_DAYS, days);
			
			if(mode == CREATE)
			{
				cv.put(ActivityTemplatesTables.COLUMN_TYPE, data.getType());
				long success = writeableDb.insert(ActivityTemplatesTables.TABLE_TEMPLATES, null, cv);
				
				if(success == -1) // failed to insert, indicates duplicate title
				{
					saveSucceed = false;
				}
			}
			else if(mode == MODIFY)
			{
				writeableDb.update(ActivityTemplatesTables.TABLE_TEMPLATES, cv, ActivityTemplatesTables.COLUMN_TITLE + " = " + "\'" + data.getTitle() + "\'", null);
			}
			
			writeableDb.setTransactionSuccessful();
			writeableDb.endTransaction();
			writeableDb.close();
			return saveSucceed;
		}
		
		return false;
	}
	
	//-------------------------------------------------------------------------------------------------------
	
	public void saveActivityInDB(ActivityData data, int mode)
	{
		if(_isInitialized)
		{
			
			SQLiteDatabase writeableDb = _activitiesDB.getWritableDatabase();
			writeableDb.beginTransaction();
			
			ContentValues cv = new ContentValues();
			cv.put(ActivitiesTables.COLUMN_TITLE, data.getTitle());
			cv.put(ActivitiesTables.COLUMN_PREP, data.getPrepWord());
			cv.put(ActivitiesTables.COLUMN_SUBJECT, data.getSubject());
			cv.put(ActivitiesTables.COLUMN_OPTIONAL, data.getOptionalInfo());
			
			// if it's an event put time anyway, if no time, time columns will be -1 (UNDEFINED)
			if(data.isEvent())
			{
				cv.put(ActivitiesTables.COLUMN_HOUR, data.getHour());
				cv.put(ActivitiesTables.COLUMN_MINUTES, data.getMinutes());
			}
			
			if(data.getType().equals("one-time"))
			{
				cv.put(ActivitiesTables.COULMN_DAY, data.getDay());
				cv.put(ActivitiesTables.COLUMN_MONTH, data.getMonth());
				cv.put(ActivitiesTables.COLUMN_YEAR, data.getYear());
			}
			else if(data.getType().equals("repeating yearly"))
			{
				// only day and month, no year
				cv.put(ActivitiesTables.COULMN_DAY, data.getDay());
				cv.put(ActivitiesTables.COLUMN_MONTH, data.getMonth());
			}
			else if(data.getType().equals("repeating monthly"))
			{
				cv.put(ActivitiesTables.COULMN_DAY, data.getDay());
			}
			
			if(data.isRepeatingWeeklyEvent()) // get days of repeat
			{
				String daysOfRepeat = utils.intsArrayListToString(data.getDaysOfRepeat());
				cv.put(ActivitiesTables.COLUMN_REPEAT_DAYS, daysOfRepeat);
			}
			
			if(data.hasNotificationDays())
			{
				String notificationDays = utils.intsArrayListToString(data.getNotificationTimes());
				cv.put(ActivitiesTables.COLUMN_NUMBER_NOTIFICATION_DAYS, notificationDays); 
			}
			
			String table;
			if(data.getType().equals("task")) // tasks table
			{
				table = ActivitiesTables.TABLE_TASKS;
			}
			else // events table
			{
				cv.put(ActivitiesTables.COLUMN_TYPE, data.getType());
				table = ActivitiesTables.TABLE_EVENTS;
			}
			
			if(mode == CREATE)
			{
				writeableDb.insert(table, null, cv);
			}
			else if(mode == MODIFY)
			{
				writeableDb.update(table, cv, ActivitiesTables.COLUMN_ID + "=" + data.getID(), null);
			}
			
			writeableDb.setTransactionSuccessful();
			writeableDb.endTransaction();
			writeableDb.close();
				
		}	
		
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	public ArrayList<String> getTemplatesTitlesList()
	{
		
		if(_isInitialized)
		{
			SQLiteDatabase readableDb = _activityTemplatesDB.getReadableDatabase();
			Cursor c = readableDb.rawQuery("select * from " + ActivityTemplatesTables.TABLE_TEMPLATES + ";", null);
			c.moveToNext();
		
			ArrayList<String> templateTitles = new ArrayList<String>();
			for(int i = 0; i < c.getCount(); i++)
			{
				templateTitles.add(c.getString(c.getColumnIndex(ActivityTemplatesTables.COLUMN_TITLE)));
				c.moveToNext();
			}
			c.close();
			readableDb.close();
		
			return templateTitles;
		}
		
		return null;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	public ActivityTemplateData getTemplateDataFromDB(final String title)
	{
		String type, defaultPrepWord, notficatioDays;
		
		if(_isInitialized)
		{
			ActivityTemplateData data = new ActivityTemplateData();
			
			SQLiteDatabase readableDb = _activityTemplatesDB.getReadableDatabase();
			Cursor c = readableDb.rawQuery("select * from " 
					+ ActivityTemplatesTables.TABLE_TEMPLATES+ " where " + ActivityTemplatesTables.COLUMN_TITLE + " = \'" + title + "\';" , null);
			c.moveToNext();
			
			type = c.getString(c.getColumnIndex(ActivityTemplatesTables.COLUMN_TYPE));
			defaultPrepWord = c.getString(c.getColumnIndex(ActivityTemplatesTables.COLUMN_DEFAULT_PREP));
			
			data.setType(type);
			data.setTitle(title);
			data.setDefaultPrepWord(defaultPrepWord);
			
			if(!type.equals("task")) // if this is not a task.  
			{
				notficatioDays = c.getString(c.getColumnIndex(ActivityTemplatesTables.COLUMN_NUMBER_NOTIFICATION_DAYS));
				
				if(notficatioDays != null && !notficatioDays.equals(""))
				{
					String[] temp;
					temp = notficatioDays.split(" ");
				
					for(int i = 0; i < temp.length; i++)
					{
						data.addNotificationDays(Integer.parseInt(temp[i]));
					}
				}
			}
			
			c.close();
			readableDb.close();
			
			return data;
		}
		
		return null;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	public void removeTemplateDataFromDB(final String title)
	{
		if(_isInitialized)
		{
			SQLiteDatabase writeableDb =_activityTemplatesDB.getWritableDatabase();

			writeableDb.beginTransaction();  
			
			writeableDb.delete(ActivityTemplatesTables.TABLE_TEMPLATES, ActivityTemplatesTables.COLUMN_TITLE + "=" + "\'" + title + "\'", null);
			
			writeableDb.setTransactionSuccessful();
			writeableDb.endTransaction();
			writeableDb.close();
		}
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	public ArrayList<ActivityData> getFollowingDaysEventsByExtent(MyDate startDate, int extent)
	{
		if(_isInitialized)
		{
	
			ArrayList<ActivityData> events = new ArrayList<ActivityData>();
			//events.addAll(this.getAllTasks());
			
			// open the DB
			SQLiteDatabase readableDb = _activitiesDB.getReadableDatabase();
			
			// Calculate the end date according to start date and extent
			MyDate endDate = new MyDate(startDate);
			endDate.addDays(extent);
			
			// get events from DB according to the date and the extent
			Cursor eventCursor = readableDb.rawQuery("SELECT * from " + ActivitiesTables.TABLE_EVENTS + ";", null);
			eventCursor.moveToNext();
			
			for(int i = 0; i < eventCursor.getCount(); i++)
			{
				String type = eventCursor.getString(eventCursor.getColumnIndex(ActivitiesTables.COLUMN_TYPE));
				ArrayList<MyDate> datesOfEvent = new ArrayList<MyDate>();
				
				// get the date of the event
				if(type.equals("one-time"))
				{
					datesOfEvent.add(this.getOneTimeEventDate(eventCursor));
				}
				else if(type.equals("repeating yearly"))
				{
					datesOfEvent.add(this.getRepeatingYearlyEventDate(eventCursor, startDate));
				}
				else if(type.equals("repeating monthly"))
				{
					datesOfEvent.add(this.getRepeatingMonthlyEventDate(eventCursor, startDate));
				}
				else // repeating weekly
				{
					datesOfEvent.addAll(this.getRepeatingWeeklyEventDates(eventCursor, startDate, extent));
				}

				
				for(int j = 0; j < datesOfEvent.size(); j++)
				{
					MyDate curr = datesOfEvent.get(j);
					
					if(curr.compareTo(startDate) == 0 || (curr.after(startDate) && curr.before(endDate)) || curr.compareTo(endDate) == 0)
					{
						ActivityData eventData = this.getEventFromDB(eventCursor, datesOfEvent.get(j));
						events.add(eventData);
					}
				}
				
				eventCursor.moveToNext();
			}
			
			eventCursor.close();
			readableDb.close();
			
			Collections.sort(events);
			return events;
			
		}
		return null;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	public ArrayList<ActivityData> getReminderListFromDB(MyDate currentDate)
	{
		if(_isInitialized)
		{
	
			// open the DB
			ArrayList<ActivityData> reminder = new ArrayList<ActivityData>();
			reminder.addAll(this.getAllTasks());
			
			reminder.addAll(this.getFollowingDaysEventsByExtent(currentDate, 0)); // get todays events
			
			// open the DB
			SQLiteDatabase readableDb = _activitiesDB.getReadableDatabase();
			
			// get events from DB according to the date and the notification days
			Cursor eventCursor = readableDb.rawQuery("SELECT * from " + ActivitiesTables.TABLE_EVENTS + ";", null);
			eventCursor.moveToNext();
			for(int i = 0; i < eventCursor.getCount(); i++)
			{
				String notificationDaysStr = eventCursor.getString(eventCursor.getColumnIndex(ActivitiesTables.COLUMN_NUMBER_NOTIFICATION_DAYS));
				
				if(notificationDaysStr != null) // if there are notification times
				{
					String type = eventCursor.getString(eventCursor.getColumnIndex(ActivitiesTables.COLUMN_TYPE));
					ArrayList<MyDate> datesOfEvent = new ArrayList<MyDate>();
					
					// get the date of the event
					if(type.equals("one-time"))
					{
						datesOfEvent.add(this.getOneTimeEventDate(eventCursor));
					}
					else if(type.equals("repeating yearly"))
					{
						datesOfEvent.add(this.getRepeatingYearlyEventDate(eventCursor, currentDate));
					}
					else if(type.equals("repeating monthly"))
					{
						datesOfEvent.add(this.getRepeatingMonthlyEventDate(eventCursor, currentDate));
					}
					else // repeating weekly
					{
						datesOfEvent.addAll(this.getRepeatingWeeklyEventDates(eventCursor, currentDate, 7));
					}
					
					
					// get the notification times
					ArrayList<Integer> days = utils.stringToIntArrayList(notificationDaysStr);
					
					// goes over all the dates of the event (repeating weekly events may have more than one date)
					// and checks if notification days == num of days from today until the event
					for(int k = 0; k < datesOfEvent.size(); k++) 
					{
						// number of days between today and the date of the event
						int daysDiff = (int)((datesOfEvent.get(k).getMillis() - currentDate.getMillis())/(24*60*60*1000));
						boolean insertionDone = false;
					
						for(int j = 0; j < days.size() && !insertionDone; j++)
						{
							int numberOfDays = days.get(j);
						
							if(numberOfDays == daysDiff)
							{
								ActivityData eventData = this.getEventFromDB(eventCursor, datesOfEvent.get(k));
								eventData.setNumDays(numberOfDays);
								reminder.add(eventData);
								insertionDone = true;
							}
						}
					}
				}
				
				eventCursor.moveToNext();
			}
			
			// close the DB
			eventCursor.close();
			readableDb.close();
			
			Collections.sort(reminder);
			return reminder;
		}
		
		return null;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	public void removeTaskFromDB(int taskId)
	{
		if(_isInitialized)
		{
			SQLiteDatabase writeableDb = _activitiesDB.getWritableDatabase();

			writeableDb.beginTransaction();  
			
			writeableDb.delete(ActivitiesTables.TABLE_TASKS, ActivitiesTables.COLUMN_ID + "=" + taskId, null);
			
			writeableDb.setTransactionSuccessful();
			writeableDb.endTransaction();
			writeableDb.close();
		}
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	public void removeEventFromDB(int eventId)
	{
		if(_isInitialized)
		{
			SQLiteDatabase writeableDb = _activitiesDB.getWritableDatabase();

			writeableDb.beginTransaction();  
			
			writeableDb.delete(ActivitiesTables.TABLE_EVENTS, ActivitiesTables.COLUMN_ID + "=" + eventId, null);
			
			writeableDb.setTransactionSuccessful();
			writeableDb.endTransaction();
			writeableDb.close();
		}
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	public ArrayList<ActivityData> getAllTasks()
	{
		// open the DB
		ArrayList<ActivityData> tasks = new ArrayList<ActivityData>();
		SQLiteDatabase readableDb = _activitiesDB.getReadableDatabase();

		// get all the tasks from the DB
		Cursor taskCursor = readableDb.rawQuery("select * from " + ActivitiesTables.TABLE_TASKS + ";", null);
		taskCursor.moveToNext();
		for(int i = 0; i < taskCursor.getCount(); i++)
		{
			ActivityData taskData = new ActivityData();
			taskData.setType("task");
			taskData.setID(taskCursor.getInt(taskCursor.getColumnIndex(ActivitiesTables.COLUMN_ID)));
			taskData.setTitle(taskCursor.getString(taskCursor.getColumnIndex(ActivitiesTables.COLUMN_TITLE)));
			taskData.setPrepWord(taskCursor.getString(taskCursor.getColumnIndex(ActivitiesTables.COLUMN_PREP)));
			taskData.setSubject(taskCursor.getString(taskCursor.getColumnIndex(ActivitiesTables.COLUMN_SUBJECT)));
			taskData.setOptionalInfo(taskCursor.getString(taskCursor.getColumnIndex(ActivitiesTables.COLUMN_OPTIONAL)));
			tasks.add(taskData);

			taskCursor.moveToNext();
		}
		
		// close the DB
		taskCursor.close();
		readableDb.close();
		return tasks;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	private MyDate getOneTimeEventDate(Cursor c)
	{
		MyDate date = new MyDate();
		date.setDate(c.getInt(c.getColumnIndex(ActivitiesTables.COULMN_DAY)));
		date.setMonth(c.getInt(c.getColumnIndex(ActivitiesTables.COLUMN_MONTH))); 
		date.setYear(c.getInt(c.getColumnIndex(ActivitiesTables.COLUMN_YEAR))); 
		return date;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	private MyDate getRepeatingYearlyEventDate(Cursor c, MyDate current)
	{		
		int day = c.getInt(c.getColumnIndex(ActivitiesTables.COULMN_DAY));
		int month = c.getInt(c.getColumnIndex(ActivitiesTables.COLUMN_MONTH));
		int year = current.getYear();
		MyDate date = new MyDate(day, month, year);
		
		if(date.before(current))
		{
			date.setYear(year + 1);
		}

		return date;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	private MyDate getRepeatingMonthlyEventDate(Cursor c, MyDate current)
	{
		MyDate date = new MyDate();
		date.setDate(c.getInt(c.getColumnIndex(ActivitiesTables.COULMN_DAY)));
		date.setMonth(current.getMonth());
		date.setYear(current.getYear());
		
		if(date.getDate() < current.getDate())
		{
			// if the date is pass, look at the next month
			date.addOneMonth();
		}
		
		return date;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	private ArrayList<MyDate> getRepeatingWeeklyEventDates(Cursor c, final MyDate start, int extent)
	{
		ArrayList<MyDate> dates = new ArrayList<MyDate>();
		String days = c.getString(c.getColumnIndex(ActivitiesTables.COLUMN_REPEAT_DAYS));
		ArrayList<Integer> daysOfWeek = utils.stringToIntArrayList(days);
		MyDate curr = new MyDate(start);
		
		for(int i = 0; i <= extent; i++)
		{
			for(int j = 0; j < daysOfWeek.size(); j++)
			{
				if(curr.getDay() == daysOfWeek.get(j))
				{
					dates.add(new MyDate(curr));
				}
			}
			curr.addDays(1);
		}
		
		return dates;
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	private ActivityData getEventFromDB(Cursor c, MyDate d)
	{
		ActivityData eventData = new ActivityData();
		eventData.setType(c.getString(c.getColumnIndex(ActivitiesTables.COLUMN_TYPE)));
		eventData.setID(c.getInt(c.getColumnIndex(ActivitiesTables.COLUMN_ID)));
		eventData.setTitle(c.getString(c.getColumnIndex(ActivitiesTables.COLUMN_TITLE)));
		eventData.setPrepWord(c.getString(c.getColumnIndex(ActivitiesTables.COLUMN_PREP)));
		eventData.setSubject(c.getString(c.getColumnIndex(ActivitiesTables.COLUMN_SUBJECT)));
		eventData.setOptionalInfo(c.getString(c.getColumnIndex(ActivitiesTables.COLUMN_OPTIONAL)));
		eventData.setDay(d.getDate());
		eventData.setMonth(d.getMonth());
		eventData.setYear(d.getYear());

		String numDays = c.getString(c.getColumnIndex(ActivitiesTables.COLUMN_NUMBER_NOTIFICATION_DAYS));
		if(numDays != null && !numDays.equals(""))
		{
			eventData.setNotificationTimes(utils.stringToIntArrayList(numDays));
		}
		
		String daysOfRepeat = c.getString(c.getColumnIndex(ActivitiesTables.COLUMN_REPEAT_DAYS));
		if(daysOfRepeat != null && !daysOfRepeat.equals(""))
		{
			eventData.setDaysOfRepeat(utils.stringToIntArrayList(daysOfRepeat));
		}
	
		int hour = c.getInt(c.getColumnIndex(ActivitiesTables.COLUMN_HOUR));
	
		if(hour != ActivityData.UNDEFINED)
		{
			eventData.setHour(hour);
			eventData.setMinutes(c.getInt(c.getColumnIndex(ActivitiesTables.COLUMN_MINUTES)));
			eventData.setHasTime(true);
		}
		
		return eventData;
	}


	
}
