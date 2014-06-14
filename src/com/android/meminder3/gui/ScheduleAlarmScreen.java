package com.android.meminder3.gui;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.android.meminder3.components.Logic;
import com.android.meminder3.dataStractures.AlarmData;
import com.android.meminder3.dataStractures.AlarmData.AlarmStatus;
import com.android.meminder3.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;


public class ScheduleAlarmScreen extends Activity
{
	
	private static final Map<String, Integer> SNOOZE_MINUTES;
	static
	{
		HashMap<String,Integer> map = new HashMap<String, Integer>();
		map.put("5 minutes", 5);
		map.put("10 minutes", 10);
		map.put("20 minutes", 20);
		map.put("30 minutes", 30);
		SNOOZE_MINUTES = Collections.unmodifiableMap(map);
	}
	
	
	// data members
	private TimePicker _timePicker;
	private Spinner _snoozeSpinner;
	private Button _saveButton;
	private Button _cancelButton;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_alarm);

		
		AlarmData data = Logic.getInstance().getAlarmData(this);

		// time picker
		_timePicker = (TimePicker) findViewById(R.id.alarmPicker);
		_timePicker.setCurrentHour(data.getHour());
		_timePicker.setCurrentMinute(data.getMinutes());
		_timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

		// populate snooze spinner with values
		_snoozeSpinner = (Spinner) findViewById(R.id.snoozeSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.snooze_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_snoozeSpinner.setAdapter(adapter);

		// put the snooze data from the db
		String choice = data.getSnooze() + " minutes";
		_snoozeSpinner.setSelection(adapter.getPosition(choice));


		// save button
		_saveButton = (Button) findViewById(R.id.saveAlarmButton);
		_saveButton.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View v) 
			{
				// get time from time picker
				int hour = _timePicker.getCurrentHour();
				int minutes = _timePicker.getCurrentMinute();

				// get snooze
				String snoozeStr = _snoozeSpinner.getSelectedItem().toString();
				int snooze = SNOOZE_MINUTES.get(snoozeStr);

				AlarmData data = new AlarmData(hour, minutes, snooze, AlarmStatus.ON);
				Logic.getInstance().setAlarmData(data, ScheduleAlarmScreen.this, AlarmReceiverActivity.class);
				
				finish();
			}
		});


		// cancel button
		_cancelButton = (Button) findViewById(R.id.cancelButtonInAlarmScreen);
		_cancelButton.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View v) 
			{
				finish();
			}
			
		});

	}
}
