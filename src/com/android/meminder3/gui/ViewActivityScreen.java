package com.android.meminder3.gui;


import java.util.ArrayList;

import com.android.meminder3.components.Logic;
import com.android.meminder3.dataStractures.ActivityData;
import com.android.meminder3.utils.utils;
import com.android.meminder3.R;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ViewActivityScreen extends Activity
{
	
	// graphic data members
	private TextView _what;
	private TextView _optionalInfo;
	private TextView _when;
	private TextView _notificationTimes;
	//private Button _modify;
	//private Button _remove;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_activity);
		
		_what = (TextView) findViewById(R.id.whatTextView);
		_optionalInfo = (TextView) findViewById(R.id.optionalTextView);
		_when = (TextView) findViewById(R.id.whenTextView);
		_notificationTimes = (TextView) findViewById(R.id.timesTextView);
		
		// get information about the activity
		Bundle extras = getIntent().getExtras();
		final ActivityData data = (ActivityData)extras.getSerializable("data");
		
		_what.setText(data.getTitle() + " " + data.getPrepWord() + " " + data.getSubject());
		_optionalInfo.setText(data.getOptionalInfo());
	
	
		String timeTxt = "";
		if(data.isOneTimeEvent())
		{
			timeTxt += data.getDay() + "/" + data.getMonth() + "/" + data.getYear();
		}
		else if(data.isRepeatingYearlyEvent())
		{
			timeTxt += data.getDay() + "/" + data.getMonth();
		}
		else if(data.isRepeatingMonthlyEvent())
		{
			timeTxt += "on the " + data.getDay() + "th every month"; 
		}
		else if(data.isRepeatingWeeklyEvent())
		{
			ArrayList<Integer> daysOfRepeat = data.getDaysOfRepeat();
			for(int i = 0; i < daysOfRepeat.size(); i++)
			{
				timeTxt +=  AddActivityScreen.DAYS[daysOfRepeat.get(i)-1];
				if(i != daysOfRepeat.size()-1)
				{
					timeTxt += "\n";
				}
			}
		}
		
		if(data.hasTime())
		{
			timeTxt += "\n" + utils.hourAndMinutesToTextFormatString(data.getHour(), data.getMinutes());
		}
		
		_when.setText(timeTxt);
		
		if(data.hasNotificationDays())
		{
			String daysTxt = "";
			ArrayList<Integer> notificationDays = data.getNotificationTimes();
			
			for(int i = 0; i < notificationDays.size(); i++)
			{
				daysTxt += AddActivityScreen.NOTIFICATION_DAYS_STR.get(notificationDays.get(i));
				if(i != notificationDays.size()-1)
				{
					daysTxt += "\n";
				}
			}
			
			_notificationTimes.setText(daysTxt);
		}
		else
		{
			TextView notificationTitle = (TextView) findViewById(R.id.notificationTextView);
			notificationTitle.setVisibility(View.GONE);
		}
		
		/*_modify = (Button) findViewById(R.id.modifyButtonInview);
		_modify.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent(ViewActivityScreen.this, AddActivityScreen.class);
				intent.putExtra("mode", AddActivityScreen.MODIFY_ACTIVITY);
				intent.putExtra("data", data);
				startActivity(intent);
				finish();
			}
		});*/
		
		/*_remove = (Button) findViewById(R.id.removeButtonInview);
		_remove.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				// TODO: ask if sure
				Logic.getInstance().removeActivityFromDB(data);
				finish();
			}
		});*/
		
	}

}
