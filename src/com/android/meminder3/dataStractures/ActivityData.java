package com.android.meminder3.dataStractures;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import com.android.meminder3.utils.utils;



// this class is serializable (strings) so we can pass it's instances inside intents
public class ActivityData implements Serializable, Comparable<Object>
{
	// TODO: put event types as consts
	public static final String BIRTHDAY = "Birthday";
	public static final String BUY = "Buy";
	public static final String CALL = "Call";
	public static final String COURSE = "Course";
	public static final String INTERVIEW = "Interview";
	
	
	public static final int UNDEFINED = -1;
	private static final long serialVersionUID = 1L;
	
	private int _id;
	private String _type;			// mandatory
	private String _title;			// mandatory
	private String _prepWord;		// optional (empty if not exist)
	private String _subject;		// optional (empty if not exist)
	private String _optionalInfo;	// optional (empty if not exist)
	
	private int _hour;				// optional
	private int _minutes;			// optional
	private boolean _hasTime;		// true when there's a time
	
	private int _day;				// mandatory for one-time events and yearly repeating events
	private int _month;				// mandatory for one-time events and yearly repeating events
	private int _year;				// mandatory for one-time events
	
	private ArrayList<Integer> _notificationTimes; // optional, for events
	private ArrayList<Integer> _daysOfRepeat;	   // mandatory for repeating weekly events
	
	int _numOfDays;				// only for tts use
	
	public ActivityData()
	{
		_id = UNDEFINED;
		_hour = UNDEFINED;
		_minutes = UNDEFINED;
		_day = UNDEFINED;
		_month = UNDEFINED;
		_year = UNDEFINED;
		_notificationTimes = new ArrayList<Integer>();
		_daysOfRepeat = new ArrayList<Integer>();
		_hasTime = false;
	}
	
	public boolean isTask()
	{
		return (_type.equals("task"));
	}
	
	public boolean isEvent()
	{
		return (!_type.equals("task"));
	}
	
	public boolean isOneTimeEvent()
	{
		return (_type.equals("one-time"));
	}
	
	public boolean isRepeatingYearlyEvent()
	{
		return (_type.equals("repeating yearly"));
	}
	
	public boolean isRepeatingMonthlyEvent()
	{
		return (_type.equals("repeating monthly"));
	}
	
	public boolean isRepeatingWeeklyEvent()
	{
		return (_type.equals("repeating weekly"));
	}
	
	
	public int getID()
	{
		return _id;
	}
	
	public String getType()
	{
		return _type;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public String getPrepWord()
	{
		return _prepWord;
	}
	
	public String getSubject()
	{
		return _subject;
	}
	
	/**
	 * @return description of the event
	 */
	public String getDescription()
	{
		return _title + " " + _prepWord + " " + _subject;
	}
	
	public String getOptionalInfo()
	{
		return _optionalInfo;
	}
	
	public ArrayList<Integer> getNotificationTimes()
	{
		return (ArrayList<Integer>)_notificationTimes.clone(); // return clone?
	}
	
	public int getHour()
	{
		return _hour;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTimeAsString()
	{
		if(_hasTime)
		{
			return utils.hourAndMinutesToTextFormatString(_hour, _minutes);
		}
		return null;
	}
	
	public String getDateAsString()
	{
		return _day + "/" + _month;
	}
	
	public int getMinutes()
	{
		return _minutes;
	}
	
	public int getDay()
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
	
	
	
	public boolean hasTime()
	{
		return _hasTime;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasDate()
	{
		return _day != UNDEFINED;
	}
	
	public boolean hasNotificationDays()
	{
		return (!_notificationTimes.isEmpty());
	}
	
	public int getNumDays()
	{
		return _numOfDays;
	}
	
	public ArrayList<Integer> getDaysOfRepeat()
	{
		return (ArrayList<Integer>)_daysOfRepeat.clone(); 
	}
	
	public void setID(int id)
	{
		_id = id;
	}
	
	public void setType(String type)
	{
		_type = type;
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}
	
	public void setPrepWord(String prepWord)
	{
		_prepWord = prepWord;
	}
	
	public void setSubject(String subject)
	{
		_subject = subject;
	}
	
	public void setOptionalInfo(String info)
	{
		_optionalInfo = info;
	}
	
	public void setHour(int hour)
	{
		_hour = hour;
	}
	
	public void setMinutes(int minutes)
	{
		_minutes = minutes;
	}
	
	public void setDay(int day)
	{
		_day = day;
	}
	
	public void setMonth(int month)
	{
		_month = month;
	}
	
	public void setYear(int year)
	{
		_year = year;
	}
	
	public void addNotificationTimes(int numDays)
	{
		_notificationTimes.add(numDays);
	}
	
	public void setNotificationTimes(final ArrayList<Integer> days)
	{
		_notificationTimes.clear();
		_notificationTimes = (ArrayList<Integer>)days.clone();
	}
	
	public void setHasTime(boolean val)
	{
		_hasTime = val;
	}
	
	public void setNumDays(int num)
	{
		_numOfDays = num;
	}
	
	public void addDayOfRepeat(int day)
	{
		_daysOfRepeat.add(day);
	}
	
	public void setDaysOfRepeat(final ArrayList<Integer> daysOfRepeat)
	{
		_daysOfRepeat.clear();
		_daysOfRepeat = (ArrayList<Integer>)daysOfRepeat.clone();
	}

	// returns 0 if time equal, returns > 0 if other is after this, returns < 0 if other is before this
	public int compareTo(Object other) 
	{
		ActivityData otherData = (ActivityData) other;
		Date thisDate, otherDate;
		
		if(this._type.equals("task") || otherData._type.equals("task"))
		{
			return 1; // cause task is always first
		}
		else // both are events, both has date, maybe time
		{
			thisDate = new Date(_year, _month, _day);
			if(this._hasTime)
			{
				thisDate.setHours(_hour);
				thisDate.setMinutes(_minutes);
				thisDate.setSeconds(0);
			}
			else
			{
				thisDate.setHours(0);
				thisDate.setMinutes(1);
				thisDate.setSeconds(0);
			}
			
			otherDate = new Date(otherData._year, otherData._month, otherData._day);
			if(otherData._hasTime)
			{
				otherDate.setHours(otherData._hour);
				otherDate.setMinutes(otherData._minutes);
				otherDate.setSeconds(0);
			}
			else
			{
				otherDate.setHours(0);
				otherDate.setMinutes(1);
				otherDate.setSeconds(0);
			}
		}
		
		return thisDate.compareTo(otherDate);
	}

	@Override
	public String toString() 
	{
		String activityText = "";

		if (this.isEvent()) 
		{

			activityText += _day + "/" + _month + " ";

			if(_hasTime)
			{
				activityText += utils.hourAndMinutesToTextFormatString(_hour, _minutes) + " ";
			}
		}

		activityText += _title + " " + _prepWord + " " + _subject;
		
		return activityText;
	}
	
	public String toStringNoDate()
	{
		String activityText = "";

		if (this.isEvent()) 
		{
			if(_hasTime)
			{
				activityText += utils.hourAndMinutesToTextFormatString(_hour, _minutes) + " ";
			}
		}

		activityText += _title + " " + _prepWord + " " + _subject;
		
		return activityText;
	}
	
	


}
