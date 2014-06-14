package com.android.meminder3.gui;


import java.util.ArrayList;

import com.android.meminder3.components.DatabaseHandler;
import com.android.meminder3.dataStractures.ActivityData;
import com.android.meminder3.dataStractures.MyDate;
import com.android.meminder3.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class CalendarScreen extends Activity implements OnGestureListener
{
	// swipe constants
	 private static final int SWIPE_MIN_DISTANCE = 120;
	 private static final int SWIPE_MAX_OFF_PATH = 250;
	 private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	
	private static final String[] DAYS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	
	// graphic
	private TextView _weekDatesText;
	private ListView _list;
	private ArrayList<ArrayList<ActivityData>> _days;
	private GestureDetector _gestureScanner;
	
	// logic
	private MyDate _beginningOfWeek;
	private MyDate _endOfWeek;
	private ArrayList<ActivityData> _currentWeek;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		
		_gestureScanner = new GestureDetector(this);
		
		_days = new ArrayList<ArrayList<ActivityData>>(7);
		for(int i = 0; i < 7; i++)
		{
			_days.add(new ArrayList<ActivityData>());
		}
		
		_weekDatesText = (TextView) findViewById(R.id.calendarTextView);
		_list = (ListView) findViewById(R.id.calendarListView);
		
		_beginningOfWeek = new MyDate();
		if(_beginningOfWeek.getDay() != 1)
		{
			_beginningOfWeek.addDays(1 - _beginningOfWeek.getDay());
		}
		
		_endOfWeek = new MyDate(_beginningOfWeek);
		_endOfWeek.addDays(6);
		
		refreshList();
		
		_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() 
		{
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) 
			{
				MyDate selectedDate = new MyDate(_beginningOfWeek);
				selectedDate.addDays(position); // gets the selected date
				
				Intent intent = new Intent(CalendarScreen.this, CalendarDayScreen.class);
				intent.putExtra("date", selectedDate);
				intent.putExtra("dayEvents", _days.get(position));
				
				startActivity(intent);
				return false;
			}
		});
		
		_list.setOnTouchListener(new View.OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				_gestureScanner.onTouchEvent(event);
				return false;
			}
		});
		
		/*_previousButton.setOnClickListener(new View.OnClickListener() 
		{	
			public void onClick(View v) 
			{
				_beginningOfWeek.addDays(-7); // set to the next Sunday
				_endOfWeek.addDays(-7); // set to the next Saturday
				
				refreshList();
			}
		});*/
		
		/*_nextButton.setOnClickListener(new View.OnClickListener() 
		{	
			public void onClick(View v)
			{
				_beginningOfWeek.addDays(7); // set to the next Sunday
				_endOfWeek.addDays(7); // set to the next Saturday
				
				refreshList();
			}*/
		//});
			
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		this.refreshList();
	}
	
	//------------------------------------------------------------------------------------------------------------
	
	
	private void refreshList()
	{
	
		_weekDatesText.setText(_beginningOfWeek.getDate() + "/" + _beginningOfWeek.getMonth() + "/" + _beginningOfWeek.getYear()
				+ " - " + _endOfWeek.getDate() + "/" + _endOfWeek.getMonth() + "/" + _endOfWeek.getYear());
		
		if(_currentWeek != null)
		{
			_currentWeek.clear();
		}
		
		for(int i = 0; i < _days.size(); i++)
		{
			_days.get(i).clear();
		}
		
		_currentWeek = DatabaseHandler.getInstance().getFollowingDaysEventsByExtent(_beginningOfWeek, 6); //TODO: get from logic
				
		for(int i = 0; i < _currentWeek.size(); i++)
		{
			ActivityData currEvent = _currentWeek.get(i);
			MyDate eventDate = new MyDate(currEvent.getDay(), currEvent.getMonth(), currEvent.getYear());
			int day = eventDate.getDay(); // gets the day of the week
			_days.get(day-1).add(currEvent);
		}
		
		ArrayList<String> daysAndDates = new ArrayList<String>();
		MyDate curr = new MyDate(_beginningOfWeek);
		for(int i = 0; i < DAYS.length; i++)
		{
			daysAndDates.add(DAYS[i] + " " + curr.getDate() + "/" + curr.getMonth() + "  (" + _days.get(i).size() + ")");
			curr.addDays(1);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, daysAndDates);
		_list.setAdapter(adapter);
	}

	
	// gesture methods
	
	public boolean onDown(MotionEvent e) 
	{
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
	{
		try 
		{
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
			{
				return false;
			}

			// right to left swipe
			if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) 
			{
				_beginningOfWeek.addDays(7); // set to the next Sunday
				_endOfWeek.addDays(7); // set to the next Saturday
				refreshList();
			} 
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) 
			{
				_beginningOfWeek.addDays(-7); // set to the next Sunday
				_endOfWeek.addDays(-7); // set to the next Saturday
				refreshList();
			}
		} 
		catch (Exception e)
		{
			// nothing
		}
		return true;
	}

	public void onLongPress(MotionEvent e) 
	{
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) 
	{
		return false;
	}

	public void onShowPress(MotionEvent e) 
	{
	}

	public boolean onSingleTapUp(MotionEvent e) 
	{
		return false;
	}
	
}
