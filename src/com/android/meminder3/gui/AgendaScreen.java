package com.android.meminder3.gui;
/*ackage com.android.meminder.gui;


import java.util.ArrayList;
import com.android.meminder.components.Logic;
import com.android.meminder.dataStractures.ActivityData;
import com.android.meminder.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class AgendaScreen extends Activity
{

	// graphic data members
	private Button _remind;
	private Button _today;
	private Button _week;
	private Button _all;
	private ListView _agendaListView;
	private ArrayAdapter<String> _adapter;

	private ActivityData _selectedActivity;
	private ArrayList<ActivityData> _currentAgenda; // "points" at the list of the current mode


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agenda);
		
		_remind = (Button) findViewById(R.id.remindButton);
		_today = (Button) findViewById(R.id.todayButton);
		_week = (Button) findViewById(R.id.weekButton);
		_all = (Button) findViewById(R.id.allButton);
		
		// default
		_today.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button_pressed));
		_today.setEnabled(false);
		_currentAgenda = Logic.getInstance().getAgenda(0);
		
		_remind.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				_currentAgenda = Logic.getInstance().getReminder();
				_remind.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button_pressed));
				_remind.setEnabled(false);
				
				_today.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_today.setEnabled(true);
				_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_week.setEnabled(true);
				_all.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_all.setEnabled(true);
				showAgendaList();
			}
		});
		
		_today.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				_currentAgenda = Logic.getInstance().getAgenda(0);
				_today.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button_pressed));
				_today.setEnabled(false);
				
				_remind.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_remind.setEnabled(true);
				_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_week.setEnabled(true);
				_all.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_all.setEnabled(true);
				showAgendaList();
			}
		});
		
		_week.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				_currentAgenda = Logic.getInstance().getAgenda(7);
				_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button_pressed));
				_week.setEnabled(false);
				
				_remind.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_remind.setEnabled(true);
				_today.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_today.setEnabled(true);
				_all.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_all.setEnabled(true);
				showAgendaList();
			}
		});
		
		_all.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				_currentAgenda = Logic.getInstance().getAgenda(14);
				_all.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button_pressed));
				_all.setEnabled(false);
				
				_remind.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_remind.setEnabled(true);
				_today.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_today.setEnabled(true);
				_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.agenda_button));
				_week.setEnabled(true);
				showAgendaList();
			}
		});
		
		
		_agendaListView = (ListView) findViewById(R.id.agendalistView);
		this.showAgendaList();	
		
		this.registerForContextMenu(_agendaListView);
		
		_agendaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
			{
				_selectedActivity = _currentAgenda.get(position);
				Intent intent = new Intent(AgendaScreen.this, ViewActivityScreen.class);
				intent.putExtra("data", _selectedActivity);
				startActivity(intent);
			}
		});	
		
	}

	//-----------------------------------------------------------------------------------------
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		this.showAgendaList();
	}
	
	//-----------------------------------------------------------------------------------------
	
	private void showAgendaList()
	{
	
		ArrayList<String> agendaStrings = new ArrayList<String>();
		
		// go over each activity data and make a string to show
		for(int i = 0; i < _currentAgenda.size(); i++)
		{
			ActivityData curr = _currentAgenda.get(i);
			agendaStrings.add(curr.toString());
		}
		
		_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, agendaStrings);
		_agendaListView.setAdapter(_adapter);
					
	}
	
	//-----------------------------------------------------------------------------------------
	
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		if(item.getTitle().equals("modify"))
		{
			Intent intent = new Intent(AgendaScreen.this, AddActivityScreen.class);
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		//_selectedString = _adapter.getItem(info.position);
		_selectedActivity = _currentAgenda.get(info.position);

		menu.setHeaderTitle(_adapter.getItem(info.position));
		menu.add(0, v.getId(), 0, "modify");
		menu.add(0, v.getId(), 0, "remove");
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
}*/
