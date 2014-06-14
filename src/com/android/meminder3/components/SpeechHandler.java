package com.android.meminder3.components;


import java.util.ArrayList;
import java.util.Locale;
import com.android.meminder3.dataStractures.ActivityData;
import com.android.meminder3.utils.utils;
import android.content.Context;
import android.speech.tts.TextToSpeech;


public class SpeechHandler 
{
	private static SpeechHandler instance;
	private String _toSpeak;
	private TextToSpeech _tts; // you need to stop and shutdown somehow.
	
	
	private SpeechHandler()
	{
	}
	
	
	public static SpeechHandler getInstance()
	{
		if(instance == null)
		{
			instance = new SpeechHandler();
		}
		return instance;
	}
	
	public void initTTS(Context context)
	{
		// init text to speech engine
		_tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() 
		{
			public void onInit(int status) 
			{
				if(status == TextToSpeech.SUCCESS)
				{
					_tts.setLanguage(Locale.UK);
					if(_toSpeak != null)
						_tts.speak("The reminder is: " + _toSpeak , TextToSpeech.QUEUE_FLUSH, null);
				}
			}
		});
	}
	
	public void shutDownTTS()
	{
		if(_tts != null)
		{
			_tts.stop();
			_tts.shutdown();
		}
	}
	
	
	public void reminderToSpeech(ArrayList<ActivityData> reminder)
	{
		if(_tts != null)
		{
			if(reminder.isEmpty())
			{
				//_tts.speak("hello! no reminder today." , TextToSpeech.QUEUE_FLUSH, null);
				_toSpeak = "hello! no reminder today.";
			}
			else
			{
				String text = "";
	
				for(int i = 0; i < reminder.size(); i++)
				{
					ActivityData curr = reminder.get(i);
					
					String title = curr.getTitle();
					String prepWord =  curr.getPrepWord();
					String subject = curr.getSubject();
					int numOfDays = -1;
					
					if(!curr.getType().equals("task")) // if this is NOT a task
					{
						numOfDays = curr.getNumDays();
					}
					
					String time = "";
					
					if(curr.hasTime())
					{
						time += "at " + utils.hourAndMinutesToSpeechFormatString(curr.getHour(), curr.getMinutes());
					}
					
					if(numOfDays >= 0)
					{
						if(numOfDays == 0)
							text += "today: ";
						else if(numOfDays == 1)
							text += "tomorrow: ";
						else if(numOfDays == 7)
							text += "in one week: ";
						else if(numOfDays == 14)
							text +="in 2 weeks: ";
						else if(numOfDays == 21)
							text += "in 3 weeks: ";
						else
							text += "in " + numOfDays + " days: ";
					}
					
					text += title + " " + prepWord + " " + subject + " " + time + "; ";
					
					
				}
				//_tts.speak("The reminder is: " + text , TextToSpeech.QUEUE_FLUSH, null);
				_toSpeak = text;
			}
		}
	}
}
