package com.android.meminder3.dataStractures;

import java.io.Serializable;
import java.util.ArrayList;

//this class is serializable so we can pass it's objects inside intents
public class ActivityTemplateData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String _title;
	private String _type;
	private String _default_prep_word;
	private ArrayList<Integer> _notificationDays;
	
	
	public ActivityTemplateData()
	{
		_notificationDays = new ArrayList<Integer>();
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public String getType()
	{
		return _type;
	}
	
	public String getDefaultPrepWord()
	{
		return _default_prep_word;
	}
	
	public ArrayList<Integer> getNotificationDays()
	{
		// TODO: check if index ok
		return (ArrayList<Integer>)_notificationDays.clone(); // return clone
	}
	
	public void setTitle(final String title)
	{
		//TODO check
		_title = title;
	}
	
	public void setType(final String type)
	{
		//TODO: check
		_type = type;
	}
	
	public void setDefaultPrepWord(final String prepWord)
	{
		_default_prep_word = prepWord;
	}
	
	public void addNotificationDays(int numDays)
	{
		//TODO: check
		_notificationDays.add(numDays);
	}
}
