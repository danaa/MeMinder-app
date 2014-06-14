package com.android.meminder3.dataStractures;


import java.io.Serializable;
import java.util.Calendar;


public class MyDate implements Serializable
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Calendar _date;
	private int _day;
	private int _month;
	private int _year;
	private int _hours;
	private int _minutes;
	
	// creates today's date with time 00:01:00
	public MyDate()
	{
		_day = Calendar.getInstance().get(Calendar.DATE);
		_month = (Calendar.getInstance().get(Calendar.MONTH)) + 1;
		_year = Calendar.getInstance().get(Calendar.YEAR);
		_hours = 0;
		_minutes = 0;
		

		_date = Calendar.getInstance();
		_date.set(Calendar.DATE, _day);
		_date.set(Calendar.MONTH, _month-1);
		_date.set(Calendar.YEAR, _year);
		_date.set(Calendar.HOUR, _hours);
		_date.set(Calendar.MINUTE, _minutes);
		_date.set(Calendar.SECOND, 0);
		_date.set(Calendar.MILLISECOND, 0);
		
	}
	
	// creates a date with time 00:01:00
	public MyDate(int day, int month, int year)
	{
		_day = day;
		_month = month;
		_year = year;
		_hours = 0;
		_minutes = 0;
		
		
		_date = Calendar.getInstance();
		_date.set(Calendar.DATE, _day);
		_date.set(Calendar.MONTH, _month-1);
		_date.set(Calendar.YEAR, _year);
		_date.set(Calendar.HOUR, _hours);
		_date.set(Calendar.MINUTE, _minutes);
		_date.set(Calendar.SECOND, 0);
		_date.set(Calendar.MILLISECOND, 0);
	}
	
	public MyDate(final MyDate other) 
	{
		_date = (Calendar)other._date.clone();
		_day = other._day;
		_month = other._month;
		_year = other._year;
		_hours = other._hours;
		_minutes = other._minutes;
	}

	public long getMillis()
	{
		return _date.getTimeInMillis();
	}
	
	public int getDate()
	{
		return _day;
	}
	
	public int getMonth()
	{
		return _month;
	}
	
	public int getYear()
	{
		return _year;
	}
	
	public int getDay()
	{
		return _date.get(Calendar.DAY_OF_WEEK);
	}
	
	public int getHours()
	{
		return _hours;
	}
	
	public int getMinutes()
	{
		return _minutes;
	}
	
	public int compareTo(MyDate other)
	{
		return _date.compareTo(other._date);
	}
	
	public boolean before(MyDate other)
	{
		return (_date.before(other._date));
	}
	
	public boolean after(MyDate other)
	{
		return (_date.after(other._date));
	}
	
	public void setDate(int day)
	{
		_date.set(Calendar.DATE, day);
		_day = day;
	}
	
	public void setMonth(int month)
	{
		_date.set(Calendar.MONTH, month-1);
		_month = month;
	}
	
	public void setYear(int year)
	{
		_date.set(Calendar.YEAR, year);
		_year = year;
	}
	
	public void setHour(int hour)
	{
		_hours = hour;
		_date.set(Calendar.HOUR, hour);
	}
	
	public void setMinutes(int minutes)
	{
		_minutes = minutes;
		_date.set(Calendar.MINUTE, minutes);
	}
	
	public void addDays(int numDays)
	{
		_date.add(Calendar.DATE, numDays);
		_day = _date.get(Calendar.DATE);
		_month = _date.get(Calendar.MONTH) + 1;
		_year = _date.get(Calendar.YEAR);
	}

	public void addOneMonth()
	{
		_date.add(Calendar.MONTH, 1);
		_day = _date.get(Calendar.DATE);
		_month = _date.get(Calendar.MONTH) + 1;
		_year = _date.get(Calendar.YEAR);
	}

	@Override
	public String toString() 
	{
		return _date.toString();
	}
	
	
	
}
