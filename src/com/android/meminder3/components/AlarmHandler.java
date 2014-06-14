package com.android.meminder3.components;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


/**
 * This class represents the Alarm handler component
 * A component that functions as an adapter between the system
 * and the external Android Alarm Manager component.
 * This class is a singleton.
 */
public class AlarmHandler 
{
	
	enum AlarmType{REPEATING, ONE_TIME}; // id for pending intent
	
	private static AlarmHandler instance;
	
	// private c'tor
	private AlarmHandler()
	{}
	
	//-----------------------------------------------------------------------------------------------------------
	
	/**
	 * @return the instance of the alarm handler component
	 */
	public static AlarmHandler getInstance()
	{
		if(instance == null)
		{
			instance = new AlarmHandler();
		}
		return instance;
	}
	
	//-----------------------------------------------------------------------------------------------------------
	
	/**
	 * This function schedule a repeating alarm according to the given params
	 * @param triggerAtMillis time in milliseconds that the alarm should first go off
	 * @param intervalMillis interval in milliseconds between subsequent repeats of the alarm
	 * @param context the context of the gui class that requested to schedule an alarm 
	 * @param cls the GUI class that handles the alarm
	 */
	public void scheduleRepeatingAlarm(long triggerAtMillis, long intervalMillis, Context context, Class<?> cls)
	{
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, AlarmType.REPEATING.ordinal(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
	}
	
	//-----------------------------------------------------------------------------------------------------------
	
	/**
	 * This function cancels a repeating alarm
	 * @param context the context of the gui class that requested to cancel the alarm 
	 * @param cls the GUI class that handles the alarm
	 */
	public void cancelRepeatingAlarm(Context context, Class<?> cls)
	{
		Intent intent = new Intent(context, cls);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, AlarmType.REPEATING.ordinal(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pendingIntent);
	}
	
	//-----------------------------------------------------------------------------------------------------------
	
	/**
	 * This function schedules a one-time alarm
	 * @param triggerAtMillis time in milliseconds that the alarm should first go off
	 * @param context the context of the gui class that requested to schedule the alarm
	 * @param cls the GUI class that handles the alarm
	 */
	public void scheduleOneTimeAlarm(long triggerAtMillis, Context context, Class<?> cls)
	{
		Intent intent = new Intent(context, cls);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, AlarmType.ONE_TIME.ordinal(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
	}
	
	//-----------------------------------------------------------------------------------------------------------
	
	/**
	 * This function cancels a one time alarm
	 * @param context the context of the gui class that requested to cancel the alarm 
	 * @param cls the GUI class that handles the alarm
	 */
	public void cancelOneTimeAlarm(Context context, Class<?> cls)
	{
		Intent intent = new Intent(context, cls);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, AlarmType.ONE_TIME.ordinal(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pendingIntent);
	}

	
}
