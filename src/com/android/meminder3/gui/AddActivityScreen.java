package com.android.meminder3.gui;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.meminder3.R;
import com.android.meminder3.components.Logic;
import com.android.meminder3.dataStractures.ActivityData;
import com.android.meminder3.dataStractures.ActivityTemplateData;
import com.android.meminder3.dataStractures.MyDate;
import com.android.meminder3.utils.utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


public class AddActivityScreen extends Activity
{
	
	// states
	public static final int CREATE = 0;
	public static final int MODIFY_ACTIVITY = 1;
	public static final int MODIFY_TEMPLATE = 2;
	
	public static final CharSequence[] DAYS = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
	
	public static final Map<Integer, String> NOTIFICATION_DAYS_STR;
	static
	{
		HashMap<Integer,String> map = new HashMap<Integer, String>();
		//map.put(0, "Same day");
		map.put(1, "1 day before");
		map.put(3, "3 days before");
		map.put(7, "7 days before");
		map.put(14, "14 days before");
		map.put(21, "21 days before");
		NOTIFICATION_DAYS_STR = Collections.unmodifiableMap(map);
	}
	
	
	// data members
	
	// graphic
	protected EditText _editTitleText;
	protected Spinner _prepSpinner;
	protected ArrayAdapter<CharSequence> _prepSpinnerAdapter;
	protected EditText _editSubjectText;
	protected Button _editDateButton;
	protected Button _editTimeButton;
	protected Button _saveButton;
	protected CheckBox _saveTemplateBox;
	
	//private Button _editNotifictionsButton;
	//private Button _optionalDetailsButton;
	
	//***
	private EditText _editTextInfo;
	private Button _cancelButton;
	//***
	
	private LinearLayout _typeLayout;
	private LinearLayout _repeatingLayout;
	private LinearLayout _timeAndDateLayout;
	private LinearLayout _notificationAndExtraLayout;
	
	private Button _oneTime;
	private Button _task;
	private Button _repeating;
	private Button _yearly;
	private Button _monthly;
	private Button _weekly;
	
	// logic
	protected int _mode; // create or modify
	protected boolean _isNew;  // is this a new template or an existing template (used only in create mode)
	protected int _id;
	protected String _title;
	protected String _type;
	protected String _prepWord;
	protected String _subject;
	protected String _infoStr;

	// date
	protected int _day;
	protected int _month;
	protected int _year;
	protected ArrayList<Integer> _daysOfRepeat;
	
	// time
	protected int _hour;
	protected int _minutes;
	
	// notification days
	protected ArrayList<Integer> _notificationDays;
	
	// booleans
	protected boolean _isDateInitialized;
	protected boolean _isTimeInitialized;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_activity);
		
		this.initLogicalDataMembers();
		this.initGraphicalDataMembers();
		
		_oneTime.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				oneTimeButtonClicked(v);
			}
		});
		
		_task.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				taskButtonClicked(v);
			}
		});
		
		_repeating.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				repeatingButtonClicked(v);
			}
		});
		
		_yearly.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				yearlyButtonClicked(v);
			}
		});
		
		_monthly.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				monthlyButtonClicked(v);
			}
		});
		
		_weekly.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				weeklyButtonClicked(v);
			}
		});
		
	
		// get information about the activity and about the mode - create or modify
		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			_mode = extras.getInt("mode");
			
			if(_mode == CREATE || _mode == MODIFY_TEMPLATE) // if we are in create mode
			{
				_isNew = extras.getBoolean("isNew");
			
				if(_isNew) // new template
				{
					_type = "one-time"; // set the default type value, user may change it
					_title = "";
					_prepWord = "";
				}
				else // if it's an existing template get the template data
				{
					ActivityTemplateData templateData = (ActivityTemplateData) extras.getSerializable("templateData");
					_type = templateData.getType();
					_title = templateData.getTitle();
					_prepWord = templateData.getDefaultPrepWord();
				
					ArrayList<Integer> days = templateData.getNotificationDays();
					for(int i = 0; i < days.size(); i++)
					{
						_notificationDays.add(days.get(i));
					}
				}
			}
			else if(_mode == MODIFY_ACTIVITY) // if we are in modify activity mode
			{
				// get the activity data of the activity the user wants to modify
				ActivityData activityData = (ActivityData) extras.getSerializable("data");
				_type = activityData.getType();
				_title = activityData.getTitle();
				_prepWord = activityData.getPrepWord();
				_subject = activityData.getSubject();
				_infoStr = activityData.getOptionalInfo();
				_id = activityData.getID();
				
				if(activityData.isOneTimeEvent())
				{
					_day = activityData.getDay();
					_month = activityData.getMonth();
					_year = activityData.getYear();
					_isDateInitialized = true;
				}
				else if(activityData.isRepeatingYearlyEvent())
				{
					_day = activityData.getDay();
					_month = activityData.getMonth();
					_isDateInitialized = true;
				}
				else if(activityData.isRepeatingMonthlyEvent())
				{
					_day = activityData.getDay();
					_isDateInitialized = true;
				}
				else if(activityData.isRepeatingWeeklyEvent())
				{
					_daysOfRepeat = activityData.getDaysOfRepeat();
				}
				
				if(activityData.hasTime())
				{
					_hour = activityData.getHour();
					_minutes = activityData.getMinutes();
					_isTimeInitialized = true;
				}
				
				// get notification days
				if(activityData.hasNotificationDays())
				{
					_notificationDays = activityData.getNotificationTimes();
				}
			
			}
		}
		
		// populate preposition word spinner with values
		_prepSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_prepSpinner.setAdapter(_prepSpinnerAdapter);
		
		
		//*********
		// the bottom text edit for more information about the event
		_editTextInfo = (EditText) findViewById(R.id.editTextinfo);
		
		//*********
		
		// set edit date button behavior
		_editDateButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				showDateDialog();
			}
		});
		
		// set edit time button behavior
		_editTimeButton.setOnClickListener(new View.OnClickListener() 
		{	
			public void onClick(View v) 
			{
				createTimePickerDialog();
			}
		});
		
		// notification times button
		/*_editNotifictionsButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				createNotificationDaysChoiceDialog();
			}
		});*/
		
		/*_optionalDetailsButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				createExtraDetailsDialog();
			}
		});*/
		
		// Notification times button
		/*String text = "";
		for(int i = 0; i < _notificationDays.size(); i++)
		{
			text += NOTIFICATION_DAYS_STR.get(_notificationDays.get(i));
			if(i != _notificationDays.size()-1)
			{
				text += "\n";
			}
		}
		if(!text.equals(""))
		{
			_editNotifictionsButton.setText(text);
		}*/
		
		// save button
		_saveButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				_title = _editTitleText.getText().toString();
				
				if(_mode == MODIFY_TEMPLATE)
				{
					saveTemplateInDatabase();
					finish();
				}
				else // in create or modify activity mode
				{
					if(_saveTemplateBox.isChecked())
					{
						saveTemplateInDatabase();
					}
				
					saveActivityInDatabase();
				}
				
			}
		});
		
		
		// sets the graphic according to the mode - create or modify
		if(_mode == CREATE || _mode == MODIFY_TEMPLATE) // set the graphic of create mode
		{
			if(_isNew) // new template
			{
				_editTitleText.requestFocus();
				
				// set the default graphic, which is one-time
				_oneTime.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button_pressed));
				_oneTime.setEnabled(false);
				_repeatingLayout.setVisibility(View.GONE);
			}
			else // this is an existing template
			{
				_editSubjectText.requestFocus();
				
				_saveTemplateBox.setVisibility(View.GONE);
				_typeLayout.setVisibility(View.GONE);
				_repeatingLayout.setVisibility(View.GONE);
				
				// set the title
				if(_title != null)
				{
					_editTitleText.setText(_title);
				}
				
				_editTitleText.setEnabled(false);
				_prepSpinner.setSelection(_prepSpinnerAdapter.getPosition(_prepWord));
				
				if(_mode == MODIFY_TEMPLATE)
				{
					_editSubjectText.setVisibility(View.GONE);
					//_editOptionalInfoText.setVisibility(View.GONE);
					//_optionalDetailsButton.setVisibility(View.GONE);
					
					//****
					 _editTextInfo.setVisibility(View.GONE);
					//****
				}
			}
		}
		else if(_mode == MODIFY_ACTIVITY) // set the graphic of modify mode
		{
	
			_editTitleText.requestFocus();
			_saveTemplateBox.setVisibility(View.GONE);
			
			_typeLayout.setVisibility(View.GONE);
			_repeatingLayout.setVisibility(View.GONE);
			
			// set the title
			_editTitleText.setText(_title);
			_editTitleText.setEnabled(true);
			
			// set the preposition word
			_prepSpinner.setSelection(_prepSpinnerAdapter.getPosition(_prepWord));
			
			// set the subject
			if(_subject != null)
			{
				_editSubjectText.setText(_subject);
			}
			
			/*if(_additionalInformation != null && !_additionalInformation.equals(""))
			{
				_optionalDetailsButton.setText("Edit extra details");
			}*/
			
			//**********
			if(_infoStr != null && !_infoStr.equals("")) // if there is additional informatio
			{
				_editTextInfo.setText(_infoStr);
			}
			//**********
			
			if(_isTimeInitialized)
			{
				_editTimeButton.setText(utils.hourAndMinutesToTextFormatString(_hour, _minutes));
			}
		}
		
		// set the appropriate screen for the template type
		if(_type.equals("task"))
		{
			this.showTask();
		}
		else if(_type.equals("one-time"))
		{
			this.showOneTime();
		}
		else // repeating
		{
			this.showRepeating();
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------

	private void initGraphicalDataMembers() 
	{
		
		// initialize graphic fields
		_editTitleText = (EditText) findViewById(R.id.editTitleText);
		_prepSpinner = (Spinner) findViewById(R.id.prepSpinner);
		_prepSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.prep_array, android.R.layout.simple_spinner_item);
		_editSubjectText = (EditText) findViewById(R.id.editSubjectText);
		//_editOptionalInfoText = (EditText) findViewById(R.id.editOptionalText);	
		//_optionalDetailsButton = (Button) findViewById(R.id.extraDetailsButton);
		_editDateButton = (Button) findViewById(R.id.date_button);
		_editTimeButton = (Button) findViewById(R.id.time_button);
		//_notificationTimesText = (TextView) findViewById(R.id.notificationTimes);
		//_editNotifictionsButton = (Button) findViewById(R.id.notificationButton);
		_saveButton = (Button) findViewById(R.id.addActivityButton);
		_saveTemplateBox = (CheckBox) findViewById(R.id.saveCheckBox);
		_typeLayout = (LinearLayout) findViewById(R.id.typeLayout);
		_repeatingLayout = (LinearLayout) findViewById(R.id.repeatingLayout);
		_oneTime = (Button) findViewById(R.id.oneTimeButton);
		_task = (Button) findViewById(R.id.taskButton);
		_repeating = (Button) findViewById(R.id.repeatingButton);
		_yearly = (Button) findViewById(R.id.yearlyButton);
		_monthly = (Button) findViewById(R.id.monthlyButton);
		_weekly = (Button) findViewById(R.id.weeklyButton);
		_timeAndDateLayout = (LinearLayout) findViewById(R.id.timeAndDateLayout);
		//_notificationAndExtraLayout = (LinearLayout) findViewById(R.id.notificationAndExtraLayout);
	}

	//-------------------------------------------------------------------------------------------------------------------

	private void initLogicalDataMembers() 
	{
		
		// initialize logical fields
		_isDateInitialized = false;
		_isTimeInitialized = false;
		_notificationDays = new ArrayList<Integer>();
		_daysOfRepeat = new ArrayList<Integer>();
		_title = "";
		_type = "";
		_prepWord = "";
		_subject = "";
		_infoStr = "";
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void showDateDialog()
	{
		if(_type.equals("one-time") || _type.equals("repeating yearly"))
		{
			createDatePickerDialog();
		}
		else if(_type.equals("repeating monthly"))
		{
			createDayOfMonthChoiceDialog();
		}
		else // repeating weekly
		{
			createDaysOfWeekChoiceDialog();
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void saveTemplateInDatabase() 
	{	
		if(!_title.equals("")) // if title is not empty
		{
			ActivityTemplateData data = new ActivityTemplateData();
			data.setTitle(_title);
			data.setDefaultPrepWord(_prepSpinner.getSelectedItem().toString());
			
			for(int i = 0; i < _notificationDays.size(); i++)
			{
				data.addNotificationDays(_notificationDays.get(i));
			}
			
			if(_mode == CREATE)
			{
				data.setType(_type);
				boolean succeed = Logic.getInstance().saveActivityTemplate(data);
				if(!succeed)
					Toast.makeText(AddActivityScreen.this, "Template not saved, title already exists", Toast.LENGTH_LONG).show();
				
			}
			else if(_mode == MODIFY_TEMPLATE)
			{
				Logic.getInstance().modifyActivityTemplate(data);
			}
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void saveActivityInDatabase()
	{
		
		if(_title.equals(""))
			Toast.makeText(AddActivityScreen.this, "You must enter a title", Toast.LENGTH_LONG).show();
		else if((_type.equals("one-time") || _type.equals("repeating yearly") || _type.equals("repeating montly")) && !_isDateInitialized) // date is mandatory in these events
			Toast.makeText(AddActivityScreen.this, "You must enter a date", Toast.LENGTH_LONG).show();
		else if(_type.equals("repeating weekly") && _daysOfRepeat.isEmpty()) // repeat days are mandatory
			Toast.makeText(AddActivityScreen.this, "You must enter days of repeat", Toast.LENGTH_LONG).show();
		else // no error
		{
			// give the activity data to the logic
			ActivityData data = new ActivityData();
			data.setType(_type);
			data.setTitle(_title);            
			data.setSubject( _editSubjectText.getText().toString());
			data.setPrepWord(_prepSpinner.getSelectedItem().toString());
			data.setOptionalInfo(_editTextInfo.getText().toString());

			if(!_type.equals("task")) // if this is not a task
			{
				if(_isTimeInitialized)
				{
					data.setHour(_hour);
					data.setMinutes(_minutes);
					data.setHasTime(true);
				}

				if(_isDateInitialized) // need to check date MANDATORY on one-time and yearly repeating
				{
					data.setDay(_day);
					
					if(!_type.equals("repeating monthly")) // this is NOT a repeating monthly event
					{
						data.setMonth(_month);
						
						if(_type.equals("one-time"))
						{
							data.setYear(_year);
						}
						
					}
				}

				if(!_notificationDays.isEmpty()) // there are notification times
				{
					for(int i = 0; i < _notificationDays.size(); i++)
					{
						data.addNotificationTimes(_notificationDays.get(i));
					}
				}
				
				if(!_daysOfRepeat.isEmpty()) 
				{
					for(int i = 0; i < _daysOfRepeat.size(); i++)
					{
						data.addDayOfRepeat(_daysOfRepeat.get(i));
					}
				}

			}

			if(_mode == CREATE)
			{
				Logic.getInstance().saveActivity(data);
			}
			else if(_mode == MODIFY_ACTIVITY)
			{
				data.setID(_id);
				Logic.getInstance().modifyActivity(data);
			}
			
			finish();
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void showTask()
	{
		_repeatingLayout.setVisibility(View.GONE);
		_timeAndDateLayout.setVisibility(View.GONE);
		
		//_editDateButton.setVisibility(View.GONE);
		//_editTimeButton.setVisibility(View.GONE);
		//_notificationTimesText.setVisibility(View.GONE);
		//_editNotifictionsButton.setVisibility(View.GONE);
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void showOneTime()
	{
		_repeatingLayout.setVisibility(View.GONE);
		
		//_notificationTimesText.setVisibility(View.VISIBLE);
		
		//_editNotifictionsButton.setVisibility(View.VISIBLE);
		
		if(_mode == MODIFY_TEMPLATE)
		{
			_timeAndDateLayout.setVisibility(View.GONE);
		}
		else
		{
			_timeAndDateLayout.setVisibility(View.VISIBLE);
			_editDateButton.setText("Date");
			
			if(_isDateInitialized)
			{
				_editDateButton.setText(_day + "/" + _month + "/" + _year);
			}
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void showRepeating()
	{
		if(_mode == MODIFY_TEMPLATE)
		{
			_timeAndDateLayout.setVisibility(View.GONE);
		}
		else
		{
			if(_mode == CREATE && _isNew)
			{	
				_repeatingLayout.setVisibility(View.VISIBLE);
				//_scrollView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 8));
			}
			else
			{
				_repeatingLayout.setVisibility(View.GONE);
			}
			
			//_notificationTimesText.setVisibility(View.VISIBLE);
			//_editNotifictionsButton.setVisibility(View.VISIBLE);
			
			//****
			_editTextInfo.setVisibility(View.VISIBLE);
			//****
			
			
			_timeAndDateLayout.setVisibility(View.VISIBLE);
		}
		
		
		if(_isNew) // new template
		{ 
			// "press" the yearly button - this is the default repeating option
			this.yearlyButtonClicked(_yearly);
		}
		else // existing template
		{
			if(_type.equals("repeating yearly"))
			{
				showRepeatingYearly();
			}
			else if(_type.equals("repeating monthly"))
			{
				showRepeatingMonthly();
			}
			else // repeating weekly
			{
				showRepeatingWeekly();
			}
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void showRepeatingYearly()
	{
		_editDateButton.setText("Date");
		
		if(_isDateInitialized)
		{
			_editDateButton.setText(_day + "/" + _month);
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void showRepeatingMonthly()
	{
		_editDateButton.setText("Day of the month");
		
		if(_isDateInitialized)
		{
			_editDateButton.setText("on the " + _day + "th every month");
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void showRepeatingWeekly()
	{
		_editDateButton.setText("Days of repeat");
		
		String days = "";
		for(int i = 0; i < _daysOfRepeat.size(); i++)
		{
			days +=  AddActivityScreen.DAYS[_daysOfRepeat.get(i)-1];
			if(i != _daysOfRepeat.size()-1)
			{
				days += ", ";
			}
		}
		
		if(!days.equals(""))
		{
			_editDateButton.setText(days);
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void createDaysOfWeekChoiceDialog()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivityScreen.this);
		dialog.setTitle("Choose days of repeat");
		dialog.setCancelable(true);
		
		// get checked items if any (HOW?)
		boolean checked[] = new boolean[7];
		for(int i = 0; i < 7; i++)
		{
			if(_daysOfRepeat.contains(i+1))
			{
				checked[i] = true;
			}
			else
			{
				checked[i] = false;
			}
		}

		dialog.setMultiChoiceItems(DAYS, checked, new DialogInterface.OnMultiChoiceClickListener() 
		{
			public void onClick(DialogInterface dialog, int which, boolean isChecked) 
			{
			}
		});
			
		// OK button
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				ListView list = ((AlertDialog)dialog).getListView();
				list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				SparseBooleanArray arr = list.getCheckedItemPositions();
				
				for(int i = 0; i < list.getCount(); i++)
				{
					int day = i + 1;
					
					if(arr.get(i) == true) // value is checked
					{
						if(!_daysOfRepeat.contains(day))
						{
							_daysOfRepeat.add(day);
						}
					}
					else // value not checked
					{
						if(_daysOfRepeat.contains(day))
						{
							_daysOfRepeat.remove((Integer)day);
						}
					}
				}
				
				Collections.sort(_daysOfRepeat);
				
				String text = "";
				for(int i = 0; i < _daysOfRepeat.size(); i++)
				{
					text += DAYS[_daysOfRepeat.get(i)-1].toString();
					if(i != _daysOfRepeat.size()-1)
					{
						text += ", ";
					}
				}
				
				_editDateButton.setText(text);
			}
		});
		
		// Cancel button
		dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
			}
		});
		
		AlertDialog alert = dialog.create();
		alert.show();
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void createDatePickerDialog()
	{
		DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() 
		{
			public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) 
			{
				//TODO: check if event with the same hour and date exists
				_year = year;
				_month = monthOfYear+1; // month start from 0
				_day = dayOfMonth;
				_isDateInitialized = true;
				
				if(_type.equals("one-time"))
				{
					_editDateButton.setText(_day + "/" + _month + "/" + _year);
				}
				else // repeating yearly
				{
					_editDateButton.setText(_day + "/" + _month);
				}
			}
		};
		
		DatePickerDialog dateDialog;
		MyDate date = new MyDate();
		if(_isDateInitialized)
		{
			int year;
			if(_year == 0)
			{
				year = date.getYear();
			}
			else
			{
				year = _year;
			}
			dateDialog = new DatePickerDialog(AddActivityScreen.this, listener, year, (_month-1), _day);
		}
		else
		{
			dateDialog = new DatePickerDialog(AddActivityScreen.this, listener, date.getYear(), (date.getMonth()-1), date.getDate());
		}
		
		dateDialog.show();
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void createDayOfMonthChoiceDialog()
	{
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivityScreen.this);
		dialog.setTitle("Choose days of the month");
		dialog.setCancelable(true);
		
		final CharSequence[] items = new CharSequence[31];
		for(int i = 1; i <= 31; i++)
		{
			items[i-1] = Integer.toString(i);
		}
		
		dialog.setSingleChoiceItems(items, _day-1, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				ListView lv = ((AlertDialog)dialog).getListView();
	            lv.setTag(new Integer(which));
			}
		}); 
		
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				//TODO: take the data of the days				
				ListView lv = ((AlertDialog)dialog).getListView();
		        Integer selected = (Integer)lv.getTag();
		        if(selected == null)
		        {
		        	selected = _day;
		        }
		        
		        String choice = items[selected].toString();
		        _day = Integer.parseInt(choice);
		        _isDateInitialized = true;
		        _editDateButton.setText("on the " + _day + "th every month");
			}
		});
		
		dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
			}
		});

		
		AlertDialog alert = dialog.create();
        
		alert.show();
		
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void createTimePickerDialog()
	{
		TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() 
		{	
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
			{
				//TODO: check if event with the same hour and date exists
				_hour = hourOfDay;
				_minutes = minute;
				_isTimeInitialized = true;
				_editTimeButton.setText(utils.hourAndMinutesToTextFormatString(_hour, _minutes));
			}
		};

		TimePickerDialog timeDialog;
		
		if(_isTimeInitialized)
		{
			timeDialog = new TimePickerDialog(AddActivityScreen.this, listener, _hour, _minutes, true);
		}
		else
		{
			// get current time in 24 hours format
			int hour = Calendar.getInstance().get(Calendar.HOUR);
			int minutes = Calendar.getInstance().get(Calendar.MINUTE);
			int amOrPm  = Calendar.getInstance().get(Calendar.AM_PM);
			if(amOrPm == Calendar.PM)
			{
				hour += 12;
			}
			
			timeDialog = new TimePickerDialog(AddActivityScreen.this, listener, hour, minutes, true);
		}
		timeDialog.show();
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	private void createNotificationDaysChoiceDialog()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivityScreen.this);
		dialog.setTitle("Choose notification times");
		dialog.setCancelable(true);
	
		final CharSequence[] items = {"1 day before", "3 days before", "7 days before", "14 days before", "21 days before"};
		
		// TODO: get checked items if any (HOW?)
		Collection<Integer> keyArr = NOTIFICATION_DAYS_STR.keySet(); // get the keys from the notification days hash map
		ArrayList<Integer> sorted = new ArrayList<Integer>(keyArr); // turn them into an array list
		java.util.Collections.sort(sorted); // sort them in ascending order
 		boolean checked[] = new boolean[sorted.size()];
		for(int i = 0; i < sorted.size(); i++)
		{
			if(_notificationDays.contains(sorted.get(i)))
			{
				checked[i] = true;
			}
			else
			{
				checked[i] = false;
			}
		}
		
		dialog.setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() 
		{	
			public void onClick(DialogInterface dialog, int which, boolean isChecked) 
			{
			}
		});
		
		// OK button
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				
				ListView list = ((AlertDialog)dialog).getListView();
				list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				SparseBooleanArray arr = list.getCheckedItemPositions();
				
				for(int i = 0; i < list.getCount(); i++)
				{
					
					String choice = items[i].toString();
					int numberOfDays;
					
					if(choice.equals("Same day"))
					{
						numberOfDays = 0;
					}
					else
					{
						String str[] = choice.split(" ", 2);
						numberOfDays = Integer.parseInt(str[0]);
					}
					
					if(arr.get(i) == true) // value is checked
					{
						if(!_notificationDays.contains(numberOfDays))
						{
							_notificationDays.add(numberOfDays);
						}
					}
					else // value not checked
					{
						if(_notificationDays.contains(numberOfDays))
						{
							_notificationDays.remove((Integer)numberOfDays);
						}
					}
				}
				
				Collections.sort(_notificationDays);
				/*String text = "";
				for(int i = 0; i < _notificationDays.size(); i++)
				{
					text += NOTIFICATION_DAYS_STR.get(_notificationDays.get(i));
					if(i != _notificationDays.size() - 1)
					{
						text += "\n";
					}
				}
				_editNotifictionsButton.setText(text);*/
			}
		});
		
		
		// cancel button
		dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
			}
		});
		
		AlertDialog alert = dialog.create();
		
		alert.show();
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------
	
	/*private void createExtraDetailsDialog()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivityScreen.this);
		final EditText input = new EditText(this);
		dialog.setTitle("Extra details");
		dialog.setCancelable(true);
		dialog.setView(input);
		
		if(_infoStr != "")
		{
			input.setText(_infoStr);
		}

		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				_infoStr = input.getText().toString();
				//_optionalDetailsButton.setText("Edit extra details");
			}
		});
		
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
			}
		});
		
		dialog.show();
	}*/
	
	public void oneTimeButtonClicked(View v)
	{
		_oneTime.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button_pressed));
		_oneTime.setEnabled(false);
		
		_task.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_task.setEnabled(true);
		_repeating.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_repeating.setEnabled(true);
		
		//_scrollView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0, 9));
		
		_type = "one-time";
		_isDateInitialized = false;
		showOneTime();
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	public void taskButtonClicked(View v)
	{
		_task.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button_pressed));
		_task.setEnabled(false);
		
		_oneTime.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_oneTime.setEnabled(true);
		_repeating.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_repeating.setEnabled(true);
		
		_type = "task";
		_isDateInitialized = false;
		showTask();
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	public void repeatingButtonClicked(View v)
	{
		_repeating.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button_pressed));
		_repeating.setEnabled(false);
		
		_oneTime.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_oneTime.setEnabled(true);
		_task.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_task.setEnabled(true);
		
		_isDateInitialized = false;
		showRepeating();
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	public void yearlyButtonClicked(View v)
		{
		_yearly.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button_pressed));
		_yearly.setEnabled(false);
			
		_monthly.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_monthly.setEnabled(true);
		_weekly.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_weekly.setEnabled(true);
			
		_isDateInitialized = false;
		_type = "repeating yearly";
		showRepeatingYearly();
	}
		
	//-------------------------------------------------------------------------------------------------------------------
	
	public void monthlyButtonClicked(View v)
	{
		_monthly.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button_pressed));
		_monthly.setEnabled(false);
		
		_yearly.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_yearly.setEnabled(true);
		_weekly.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_weekly.setEnabled(true);
		
		_isDateInitialized = false;
		_type = "repeating monthly";
		showRepeatingMonthly();
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	
	public void weeklyButtonClicked(View v)
	{
		_weekly.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button_pressed));
		_weekly.setEnabled(false);
		
		_yearly.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_yearly.setEnabled(true);
		_monthly.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_button));
		_monthly.setEnabled(true);
		
		_isDateInitialized = false;
		_type = "repeating weekly";
		showRepeatingWeekly();
	}

}
