package com.android.meminder3.components;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.android.meminder3.dataStractures.ActivityData;
import com.android.meminder3.dataStractures.ActivityTemplateData;
import com.android.meminder3.dataStractures.AlarmData;
import com.android.meminder3.dataStractures.MyDate;
import com.android.meminder3.dataStractures.AlarmData.AlarmStatus;
import com.android.meminder3.utils.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;


public class Logic 
{

	private static Logic instance; // this class is singleton
	
	// inner copies of application data
	private AlarmData _alarmData;
	private boolean _isInitialized;
	private ArrayList<ActivityData> _twoWeeks;
	private ArrayList<ActivityData> _reminder;
	
	//--------------------------------------------------------------------------------------------------
	
	// c'tor
	private Logic()
	{
		_isInitialized = false;
		_twoWeeks = new ArrayList<ActivityData>();
		_reminder = new ArrayList<ActivityData>();
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void init(Context context)
	{
		DatabaseHandler.getInstance().init(context);
		_isInitialized = true;
		_alarmData = DatabaseHandler.getInstance().getAlarmFromDB();
		this.refreshActivityData();
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public static Logic getInstance() // this class is singleton
	{
		if (instance == null)
		{
			instance = new Logic();
		}
		return instance;
	}
	
	
	//--------------------------------------------------------------------------------------------------
	
	public void setAlarmEnabledOrDisabled(AlarmStatus val, Context context, Class<?> cls)
	{
		
		if(_isInitialized)
		{
			DatabaseHandler.getInstance().changeAlarmFlagInDB(val);
			_alarmData.setStatus(val);
		
			// if alarm set to on, schedule alarm in Alarm Manager
			if(val.equals(AlarmStatus.ON))
			{
				this.scheduleRepeatingAlarm(_alarmData, context, cls);
			}
			else // if set to off check if alarm exists and cancel it and cancel snooze if exists
			{
				AlarmHandler.getInstance().cancelRepeatingAlarm(context, cls);
				
				// cancels snooze if exists
				AlarmHandler.getInstance().cancelOneTimeAlarm(context, cls);
			}
		}
		
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void setAlarmData(AlarmData data, Context context, Class<?> cls)
	{
		
		if(_isInitialized)
		{
			DatabaseHandler.getInstance().saveAlarmInDB(data);
			this.scheduleRepeatingAlarm(data, context, cls);
		
			// save data in object
			_alarmData = data;
			
			// always alarm is enabled after scheduling
		}
	}

	//--------------------------------------------------------------------------------------------------
	
	private void scheduleRepeatingAlarm(AlarmData data, Context context, Class<?> cls) // maybe data not needed
	{
		
		// calculate time from now until alarm time
		Calendar c = Calendar.getInstance();
		int currHour = c.get(Calendar.HOUR_OF_DAY);
		int currMinutes = c.get(Calendar.MINUTE);
		int currSeconds = c.get(Calendar.SECOND);
		
		String currTimeStr = utils.hourMinutesAndSecondsToString(currHour, currMinutes, currSeconds);
		String alarmTimeStr = utils.hourMinutesAndSecondsToString(data.getHour(), data.getMinutes(), 0);
		
		SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
		try 
		{
			Date curr, next;
			curr = format.parse(currTimeStr);
			next = format.parse(alarmTimeStr);
			long diff = next.getTime() - curr.getTime();
			if(diff < 0)
			{
				diff += AlarmManager.INTERVAL_DAY; 
			}
		
			
			AlarmHandler.getInstance().scheduleRepeatingAlarm(System.currentTimeMillis() + diff, AlarmManager.INTERVAL_DAY , context, cls);
			
			int h = (int)(diff / (60*60*1000));
			diff -= h*60*60*1000;
			int m = (int)(diff / (60*1000));
			
			Toast t = Toast.makeText(context, "alarm is set to " + h + " hours and " + m + " minutes", Toast.LENGTH_LONG);
			t.show();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void scheduleSnoozeAlarm(Context context, Class<?> cls)
	{
		if(_isInitialized) // if alarm data is initialized
		{
			AlarmHandler.getInstance().scheduleOneTimeAlarm(System.currentTimeMillis() + _alarmData.getSnooze() * 60 * 1000, context, cls);
		}
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public AlarmData getAlarmData(Context context)
	{
		return _alarmData; // is it safe to return the field?
	}
	
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * @param data - the template to save
	 * @return true if save succeed, false otherwise
	 */
	public boolean saveActivityTemplate(ActivityTemplateData data) 
	{
		return DatabaseHandler.getInstance().saveActivityTemplateInDB(data, DatabaseHandler.CREATE);
	}
	
	//--------------------------------------------------------------------------------------------------
	
	/**
	 * @param data - the modified template data
	 * @return true if modify succeed, false otherwise 
	 */
	public boolean modifyActivityTemplate(ActivityTemplateData data)
	{
		return DatabaseHandler.getInstance().saveActivityTemplateInDB(data, DatabaseHandler.MODIFY);
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void saveActivity(ActivityData data)
	{
		DatabaseHandler.getInstance().saveActivityInDB(data, DatabaseHandler.CREATE);
		refreshActivityData();
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void modifyActivity(ActivityData data) 
	{
		DatabaseHandler.getInstance().saveActivityInDB(data, DatabaseHandler.MODIFY);
		refreshActivityData();
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public ArrayList<String> getTemplatesTitlesList()
	{
		return DatabaseHandler.getInstance().getTemplatesTitlesList();
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public ActivityTemplateData getTemplateByTitle(String title)
	{
		return DatabaseHandler.getInstance().getTemplateDataFromDB(title);
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void removeTemplateByTitle(String title)
	{
		DatabaseHandler.getInstance().removeTemplateDataFromDB(title);
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public ArrayList<ActivityData> getAgenda(int extent)
	{
		
		if(extent == 0)
		{
			ArrayList<ActivityData> todayList = new ArrayList<ActivityData>();
			MyDate today = new MyDate();
			for(int i = 0; i < _twoWeeks.size(); i++)
			{
				ActivityData curr = _twoWeeks.get(i);
				MyDate eventDate = new MyDate(curr.getDay(), curr.getMonth(), curr.getYear());
				if(curr.isTask() || (eventDate.compareTo(today) == 0))
				{
					todayList.add(curr);
				}
				else
				{
					break;
				}
			}
			return todayList;
		}
		else if(extent == 7)
		{
			ArrayList<ActivityData> weekList = new ArrayList<ActivityData>();
			MyDate today = new MyDate();
			MyDate weekFromToday = new MyDate();
			weekFromToday.addDays(7);
			for(int i = 0; i < _twoWeeks.size(); i++)
			{
				ActivityData curr = _twoWeeks.get(i);
				MyDate eventDate = new MyDate(curr.getDay(), curr.getMonth(), curr.getYear());
				if(curr.isTask() || eventDate.compareTo(today) == 0 || (eventDate.after(today) && eventDate.before(weekFromToday)))
				{
					weekList.add(curr);
				}
				else
				{
					break;
				}
			}
			return weekList;
		}
		else if(extent == 14)
		{
			return _twoWeeks;
		}
		else
			return null;
		
	}
	
	
	//--------------------------------------------------------------------------------------------------
	
	public ArrayList<ActivityData> getReminder()
	{
		return _reminder;
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void removeActivityFromDB(ActivityData toRemove)
	{
		// remove from database
		if(toRemove.getType().equals("task"))
		{
			DatabaseHandler.getInstance().removeTaskFromDB(toRemove.getID());
		}
		else // event
		{
			DatabaseHandler.getInstance().removeEventFromDB(toRemove.getID());
		}
		
		// remove from application data
		ArrayList<ActivityData> toRemoveArr = new ArrayList<ActivityData>();
		for(int i = 0; i < _twoWeeks.size();i++)
		{
			if(toRemove.getID() == _twoWeeks.get(i).getID() && toRemove.getType().equals(_twoWeeks.get(i).getType()))
			{
				toRemoveArr.add(_twoWeeks.get(i));
			}
		}
		_twoWeeks.removeAll(toRemoveArr);
		toRemoveArr.clear();
	
		ArrayList<ActivityData> toRemoveReminderArr = new ArrayList<ActivityData>();
		for(int i = 0; i < _reminder.size();i++)
		{
			if(toRemove.getID() == _reminder.get(i).getID())
			{
				toRemoveReminderArr.add(_reminder.get(i));
			}
		}
		
		_reminder.removeAll(toRemoveReminderArr);
		toRemoveReminderArr.clear();
		
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void initTTS(Context context)
	{
		String version = Build.VERSION.SDK;
		if(Integer.parseInt(version) > 3)
		{
			SpeechHandler.getInstance().initTTS(context);
		}
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void shotDownTTS()
	{
		String version = Build.VERSION.SDK;
		if(Integer.parseInt(version) > 3)
		{
			SpeechHandler.getInstance().shutDownTTS();
		}
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void reminderToSpeech(Context context)
	{
	
		String version = Build.VERSION.SDK;
		if(_isInitialized && Integer.parseInt(version) > 3)
		{
		
			MyDate date = new MyDate();
			
			// gets the reminder from the DB
			ArrayList<ActivityData> reminder = DatabaseHandler.getInstance().getReminderListFromDB(date);
		
			// give the reminder to the speech handler
			SpeechHandler.getInstance().reminderToSpeech(reminder);
		}
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void playAlarmSound(Context context)
	{
		MediaHandler.getInstance(context).play();
	}
	
	//--------------------------------------------------------------------------------------------------
	
	public void stopAlarmSound(Context context)
	{
		MediaHandler.getInstance(context).stop();
	}
	
	//--------------------------------------------------------------------------------------------------
	
	private void refreshActivityData()
	{
		_twoWeeks.clear();
		_reminder.clear();
		MyDate date = new MyDate();;
		
		_twoWeeks.addAll(DatabaseHandler.getInstance().getAllTasks());
		_twoWeeks.addAll(DatabaseHandler.getInstance().getFollowingDaysEventsByExtent(date, 13));
		_reminder.addAll(DatabaseHandler.getInstance().getReminderListFromDB(date));
	}


	
}
