package com.android.meminder3.dataStractures;

public class AlarmData 
{
	public enum AlarmStatus{OFF, ON};
	
	
	private int _hour;
	private int _minutes;
	private int _snooze;
	private AlarmStatus _status;
	
	// c'tor
	public AlarmData(int hour, int minutes, int snooze, AlarmStatus status)
	{
		_hour = hour;
		_minutes = minutes;
		_snooze = snooze;
		_status = status;
	}
	
	public int getHour()
	{
		return _hour;
	}
	
	public int getMinutes()
	{
		return _minutes;
	}
	
	public int getSnooze()
	{
		return _snooze;
	}
	
	public AlarmStatus getStatus()
	{
		return _status;
	}
	
	public void setHour(int hour)
	{
		_hour = hour;
	}
	
	
	public void setMinutes(int minutes)
	{
		_minutes = minutes;
	}
	
	public void setSnooze(int snooze)
	{
		_snooze = snooze;
	}
	
	public void setStatus(AlarmStatus status)
	{
		_status = status;
	}
	
}
