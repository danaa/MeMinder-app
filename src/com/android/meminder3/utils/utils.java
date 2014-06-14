package com.android.meminder3.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class utils 
{
	
	public static String hourAndMinutesToTextFormatString(int hour, int minutes)
	{
		String hourStr = "";
		String minutesStr = "";
		
		if(hour < 10)
		{
			hourStr += "0";
		}
		if(minutes < 10)
		{
			minutesStr += "0";
		}
		
		hourStr += hour;
		minutesStr += minutes;
		
		return (hourStr + ":" + minutesStr);
	}
	
	public static String hourAndMinutesToSpeechFormatString(int hour, int minutes)
	{
		String amPm;
		String hourStr = "";
		String minutesStr = "";
		
		if(hour >= 12 && hour < 24)
		{
			amPm = "p m.";
		}
		else
		{
			amPm = "a m.";
		}
		
		if(hour > 12)
		{
			hourStr += (hour - 12);
		}
		else if(hour > 0 && hour <= 12)
		{
			hourStr += hour;
		}
		else if (hour == 0)
		{
			hourStr += 12;
		}
		
		if(minutes > 0 && minutes < 10)
		{
			minutesStr += "oh " + minutes;
		}
		else if(minutes >= 10)
		{
			minutesStr += minutes;
		}
		
		return (hourStr + " " + minutesStr + " " + amPm);
	}
	
	
	public static String hourMinutesAndSecondsToString(int hour, int minutes, int seconds)
	{
		String secondsStr = "";
		
		if(seconds < 10)
		{
			secondsStr += "0";
		}
		
		secondsStr += seconds;
		
		return (hourAndMinutesToTextFormatString(hour, minutes) + ":" + secondsStr);
	}
	
	public static String intsArrayListToString(final ArrayList<Integer> arr)
	{
		String str = "";
		
		for(int i = 0; i < arr.size(); i++)
		{
			str += arr.get(i) + " ";		
		}
		
		return str;
	}
	
	// assumes str is a string of ints separated by white space
	public static ArrayList<Integer> stringToIntArrayList(final String str)
	{
		ArrayList<Integer> arr = new ArrayList<Integer>();
		String split[] = str.split(" ");
		
		for(int i = 0; i < split.length; i++)
		{
			arr.add(Integer.parseInt(split[i]));
		}
		
		return arr;
	}
	
}
