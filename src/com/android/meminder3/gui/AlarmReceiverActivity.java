package com.android.meminder3.gui;


import java.util.logging.Logger;

import com.android.meminder3.components.Logic;
import com.android.meminder3.R;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class AlarmReceiverActivity extends Activity 
{
	// data members
	private Button _snoozeButton;
	private Button _dismissButton;
	private PowerManager.WakeLock _lock;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		// Acquire screen lock
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		_lock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP , "my wake lock");
		_lock.acquire(); 
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
							| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
							| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN
							| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
							| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
					
		setContentView(R.layout.alarm_is_on);
         
         // initialize text to speech
 		//Logic.getInstance().initTTS(this);
 		
 		// play alarm
 		Logic.getInstance().playAlarmSound(this);

		
		// snooze button
		_snoozeButton = (Button) findViewById(R.id.snoozeButton);
		_snoozeButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{		 
				// stops the ring 
				Logic.getInstance().stopAlarmSound(AlarmReceiverActivity.this);
				
				// set one time alarm to snooze time
				Logic.getInstance().scheduleSnoozeAlarm(AlarmReceiverActivity.this, AlarmReceiverActivity.class);;
				
				finish();
			}
		});


		// dismiss button
		_dismissButton = (Button) findViewById(R.id.dismissButton);
		_dismissButton.setOnClickListener(new View.OnClickListener() 
		{

			public void onClick(View v) 
			{
				Logic.getInstance().reminderToSpeech(AlarmReceiverActivity.this); // builds the string
				Logic.getInstance().stopAlarmSound(AlarmReceiverActivity.this);
				Logic.getInstance().initTTS(AlarmReceiverActivity.this); // make it speak
			}
		});

	}
	
	
	//---------------------------------------------------------------------------------------------------------------
	
	@Override
	protected void onStop()
	{
		Logger.getAnonymousLogger().severe("stops");
		if(_lock.isHeld())
		{
			_lock.release();
		}
		
		Logic.getInstance().shotDownTTS();
		Logic.getInstance().stopAlarmSound(this);
		super.onStop();
	}
	
	//---------------------------------------------------------------------------------------------------------------
	
	


}
