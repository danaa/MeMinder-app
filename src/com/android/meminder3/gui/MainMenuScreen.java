package com.android.meminder3.gui;


import java.util.ArrayList;


import com.android.meminder3.components.Logic;
import com.android.meminder3.dataStractures.ActivityData;
import com.android.meminder3.dataStractures.AlarmData;
import com.android.meminder3.dataStractures.AlarmData.AlarmStatus;
import com.android.meminder3.gui.animations.ExpandAnimation;
import com.android.meminder3.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainMenuScreen extends Activity 
{
	
	private final String SELECT_COLOR = "#C0BEC0";
	private final String ROW_COLOR = "#E9E9E9";
	private final String BUTTON_COLOR = "#293741";
	private final int STROKE_SIZE = 10; // in px
	
	private enum Mode {REMINDER, WEEK, TWO_WEEKS};

	// data members
	//private ToggleButton _alarmToggle;
	private ImageButton _alarmButton;
	private ImageButton _addActivityButton;
	//private Button _playButton;
	private ImageButton _calendarButton;
	
	private Button _newPressed;
	private Button _pressed;
	private Button _remind;
	private Button _week;
	private Button _twoWeeks;
	private ListView _agendaListView;
	private ArrayAdapter<ActivityData> _adapter;

	private Mode _mode;
	private ActivityData _selectedActivity;
	private ArrayList<ActivityData> _currentAgenda; // "points" at the list of the current mode
	
	private View _currentListItem = null;
	

	// methods
	//-------------------------------------------------------------------------------------------------------------


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Logic.getInstance().init(this);
		//Logic.getInstance().initTTS(this);

		// schedule alarm button
		_alarmButton = (ImageButton) findViewById(R.id.clockButton);
		_alarmButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				startActivity(new Intent(MainMenuScreen.this, ScheduleAlarmScreen.class));
			}
		});

		// on/off button
		/*AlarmData.AlarmStatus status = Logic.getInstance().getAlarmData(this).getStatus();
		_alarmToggle = (ToggleButton) findViewById(R.id.alarmOn);
		_alarmToggle.setChecked(status.equals(AlarmStatus.ON));
		_alarmToggle.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				AlarmData.AlarmStatus value = _alarmToggle.isChecked() ? AlarmData.AlarmStatus.ON : AlarmData.AlarmStatus.OFF;
				Logic.getInstance().setAlarmEnabledOrDisabled(value, MainMenuScreen.this, AlarmReceiverActivity.class);
			}
		});*/

		// add new event button
		_addActivityButton = (ImageButton) findViewById(R.id.plusButton);
		_addActivityButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				startActivity(new Intent(MainMenuScreen.this, TemplatesListScreen.class));
			}
		});

		// play reminder button
		/*_playButton = (Button) findViewById(R.id.playButton);
		_playButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				Logic.getInstance().reminderToSpeech(MainMenuScreen.this);
			}
		});*/

		// calendar button
		_calendarButton = (ImageButton) findViewById(R.id.calButton);
		_calendarButton.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				startActivity(new Intent(MainMenuScreen.this, CalendarScreen.class));
			}
		});

		// bottom bar buttons
		_remind = (Button) findViewById(R.id.remindButton);
		_week = (Button) findViewById(R.id.weekButton);
		_twoWeeks = (Button) findViewById(R.id.twoWeeksButton);

		// default agenda is shown
		_mode = Mode.REMINDER;
		_currentAgenda = Logic.getInstance().getReminder();

		_remind.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				onBottomButtonPressed(v);
			}
		});

		_week.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				onBottomButtonPressed(v);
			}
		});

		_twoWeeks.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				onBottomButtonPressed(v);
			}
		});

		_agendaListView = (ListView) findViewById(R.id.agendaListView);
		this.showAgendaList();	

		this.registerForContextMenu(_agendaListView);

		_agendaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, final View view, int position,long id) 
			{
				// change the color on click
				//view.setBackgroundColor(Color.parseColor(SELECT_COLOR)); // not working
				
				// show the event information
				/*_selectedActivity = _currentAgenda.get(position);
				Intent intent = new Intent(MainMenuScreen.this, ViewActivityScreen.class);
				intent.putExtra("data", _selectedActivity);
				startActivity(intent);*/
				
				// handles expend animation
				if(view.equals(_currentListItem))
					return; // don't do the event if the list item is already expanded
					
				if(_currentListItem != null)
					doExpendOrCollapse();
				
				_currentListItem = view;
				doExpendOrCollapse();
                
                // put the event information visible only on expend
				 _selectedActivity = _currentAgenda.get(position);
                TextView info = (TextView) view.findViewById(R.id.information);
                info.setText(_selectedActivity.getOptionalInfo());
                
                /*initCollapseButton(parent.getChildAt(position));
                initEditButton(parent.getChildAt(position));
                initDiscardButton(parent.getChildAt(position));*/
                
			}
		});	
		
		_remind.performClick();
	}


	//-------------------------------------------------------------------------------------------------------------


	@Override
	public void onResume() 
	{
		super.onResume();
		
		this.showAgendaList();
		AlarmData.AlarmStatus status = Logic.getInstance().getAlarmData(this).getStatus();

		// on/off button
		//_alarmToggle.setChecked(status == AlarmStatus.ON);

		//Logic.getInstance().initTTS(this);
	}

	//-------------------------------------------------------------------------------------------------------------

	@Override
	protected void onStop() 
	{
		Logic.getInstance().shotDownTTS();
		super.onStop();
	}

	//-------------------------------------------------------------------------------------------------------------
	
	public void doCollapse(View v)
	{
		doExpendOrCollapse();
		_currentListItem = null;
	}
	
	public void doEdit(View v)
	{
		Intent intent = new Intent(MainMenuScreen.this, AddActivityScreen.class);
		intent.putExtra("mode", AddActivityScreen.MODIFY_ACTIVITY);
		intent.putExtra("data", _selectedActivity);
		startActivity(intent);
		showAgendaList(); // should do it only if something changed!!!
	}
	
	public void doDiscard(View v)
	{
		Logic.getInstance().removeActivityFromDB(_selectedActivity);
		showAgendaList();
	}
	
	/*private void initCollapseButton(final View listItemView) 
	{
		ImageButton collapse = (ImageButton) listItemView.findViewById(R.id.collaspe);
		collapse.setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				doExpendOrCollapse();
				_currentListItem = null;
			}
		});
	}
	
	private void initEditButton(View listItemView)
	{
		ImageButton edit = (ImageButton)listItemView.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() 
        {	
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(MainMenuScreen.this, AddActivityScreen.class);
				intent.putExtra("mode", AddActivityScreen.MODIFY_ACTIVITY);
				intent.putExtra("data", _selectedActivity);
				startActivity(intent);
				showAgendaList();
			}
		});
	}
	
	private void initDiscardButton(View listItemView)
	{
		ImageButton discard = (ImageButton)listItemView.findViewById(R.id.discard);
        discard.setOnClickListener(new View.OnClickListener() 
        {	
			@Override
			public void onClick(View v) 
			{
				Logic.getInstance().removeActivityFromDB(_selectedActivity);
				showAgendaList();
			}
		});
	}*/
	
	private void doExpendOrCollapse()
	{
		View toolbar = _currentListItem.findViewById(R.id.toolbar);
		ExpandAnimation expandAni = new ExpandAnimation(toolbar, 200);
		toolbar.startAnimation(expandAni);
		expandAni = null;
	}

	private void showAgendaList()
	{
		
		if(_mode == Mode.REMINDER)
		{
			_currentAgenda = Logic.getInstance().getReminder();
		}
		else if(_mode == Mode.WEEK)
		{
			_currentAgenda = Logic.getInstance().getAgenda(7);
		}
		else if(_mode == Mode.TWO_WEEKS)
		{
			_currentAgenda = Logic.getInstance().getAgenda(14);
		}
		
		//ArrayList<String> agendaStrings = new ArrayList<String>();
		_adapter = new MyArrayAdapter();
		_agendaListView.setAdapter(_adapter);

	}

	//-----------------------------------------------------------------------------------------

	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		if(item.getTitle().equals("modify"))
		{
			Intent intent = new Intent(MainMenuScreen.this, AddActivityScreen.class);
			intent.putExtra("mode", AddActivityScreen.MODIFY_ACTIVITY);
			intent.putExtra("data", _selectedActivity);
			startActivity(intent);
			this.showAgendaList();
		}
		else if(item.getTitle().equals("remove"))
		{
			// TODO: ask if sure
			Logic.getInstance().removeActivityFromDB(_selectedActivity);
			this.showAgendaList();
		}

		return super.onContextItemSelected(item);
	}

	//-----------------------------------------------------------------------------------------

	@Override
	
	 /**
	  * what will happen if the user will long click on an event in the list
	  */
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		_selectedActivity = _currentAgenda.get(info.position);

		menu.setHeaderTitle(_selectedActivity.getDescription());
		menu.add(0, v.getId(), 0, "modify");
		menu.add(0, v.getId(), 0, "remove");

		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	//-----------------------------------------------------------------------------------------
	
	private void onBottomButtonPressed(View v)
	{
		if(v.equals(_remind))
		{
			_newPressed = _remind;
			_mode = Mode.REMINDER;
		}
		else if(v.equals(_week))
		{
			_newPressed = _week;
			_mode = Mode.WEEK;
		}
		else if(v.equals(_twoWeeks))
		{
			_newPressed = _twoWeeks;
			_mode = Mode.TWO_WEEKS;
		}
		bottomButtonPressedGraphic();
		showAgendaList();
	}
	
	/**
	 * 
	 */
	private void bottomButtonPressedGraphic()
	{
		if(_pressed != null)
		{
			_pressed.setBackground(getResources().getDrawable(R.drawable.bottom_button));
			_pressed.setEnabled(true);
		}
		_newPressed.setBackground(getResources().getDrawable(R.drawable.bottom_button_pressed));
		_newPressed.setEnabled(false);
		_pressed = _newPressed;
	}
	
	//-----------------------------------------------------------------------------------------
	
	// private array adapter class
	private class MyArrayAdapter extends ArrayAdapter<ActivityData>
	{
		
		public MyArrayAdapter()
		{
			super(MainMenuScreen.this,R.layout.item_view,_currentAgenda);
		}
		
		@Override
		public boolean isEnabled(int position)
		{
		    return true;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View itemView = convertView;
			if(itemView == null)
				itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
			
			// put the event description in the text view
			ActivityData curr = _currentAgenda.get(position);
			TextView theEvent = (TextView) itemView.findViewById(R.id.event);
			theEvent.setText(curr.getTitle() + " " + curr.getPrepWord() + " " + curr.getSubject());
			
			// put the time in the text view
			TextView theTime = (TextView) itemView.findViewById(R.id.time);
			String str = "";
			if(curr.hasDate())
			{
				str += curr.getDateAsString();
				if(curr.hasTime())
					str += "   " + curr.getTimeAsString();
			}
			else
				str += "task";
			theTime.setText(str);
			
			// put the image that corresponds to the event
			ImageView image = (ImageView) itemView.findViewById(R.id.imageView);
			String title = curr.getTitle();
			if(title.equals(ActivityData.BIRTHDAY))
				image.setImageResource(R.drawable.birthday);
			else if(title.equals(ActivityData.BUY))
				image.setImageResource(R.drawable.buy);
			else if(title.equals(ActivityData.CALL))
				image.setImageResource(R.drawable.call);
			else if(title.equals(ActivityData.COURSE))
				image.setImageResource(R.drawable.course);
			else if(title.equals(ActivityData.INTERVIEW))
				image.setImageResource(R.drawable.interview);
			else
				image.setImageResource(R.drawable.custom);
			
			// put a different color in rows with even index 
			/*if(position % 2 == 0)
				itemView.setBackgroundColor(Color.parseColor(ROW_COLOR)); */// now workings
				
			
			// Resets the toolbar to be closed
            View toolbar = itemView.findViewById(R.id.toolbar);
            LayoutParams params = (LinearLayout.LayoutParams)toolbar.getLayoutParams();
            params.bottomMargin = -60;
            toolbar.setVisibility(View.GONE);
			
			return itemView;
		}
	}
	
}