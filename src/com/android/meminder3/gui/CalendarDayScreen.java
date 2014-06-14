package com.android.meminder3.gui;


import java.util.ArrayList;
import com.android.meminder3.components.Logic;
import com.android.meminder3.dataStractures.ActivityData;
import com.android.meminder3.dataStractures.MyDate;
import com.android.meminder3.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class CalendarDayScreen extends Activity
{
	
	private static final String[] DAYS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	
	private ListView _list;
	private TextView _dateText;
	private ArrayList<ActivityData> _dayEvents; // reference
	private MyDate _date;
	private ActivityData _selected;
	ArrayAdapter<String> _adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.calendar_day);
		
		_list = (ListView) findViewById(R.id.dayListView);
		_dateText = (TextView) findViewById(R.id.dayDateText);
		
		Bundle extras = getIntent().getExtras();
		_date = (MyDate) extras.getSerializable("date");
		_dayEvents = (ArrayList<ActivityData>) extras.getSerializable("dayEvents");
		
		_dateText.setText(DAYS[_date.getDay() - 1] + " " + _date.getDate() + "/" + _date.getMonth() + "/" + _date.getYear());
		
		_list.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
			{
				_selected = _dayEvents.get(position);
				
				Intent intent = new Intent(CalendarDayScreen.this, ViewActivityScreen.class);
				intent.putExtra("data", _selected);
				startActivity(intent);
				finish();
			}
		});
		
		this.registerForContextMenu(_list);
		
		this.refreshList();
	}
	
	//-----------------------------------------------------------------------------------------------------
	
	private void refreshList()
	{
		ArrayList<String> eventsStrings = new ArrayList<String>();
		for(int i = 0; i < _dayEvents.size(); i++)
		{
			eventsStrings.add(_dayEvents.get(i).toStringNoDate());
		}
		
		_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, eventsStrings);
		_list.setAdapter(_adapter);
	}
	
	//-----------------------------------------------------------------------------------------------------
	
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		if(item.getTitle().equals("modify"))
		{
			Intent intent = new Intent(CalendarDayScreen.this, AddActivityScreen.class);
			intent.putExtra("mode", AddActivityScreen.MODIFY_ACTIVITY);
			intent.putExtra("data", _selected);
			startActivity(intent);
			this.refreshList();
		}
		else if(item.getTitle().equals("remove"))
		{
			// TODO: ask if sure
			Logic.getInstance().removeActivityFromDB(_selected);
			this.refreshList();
		}
		
		return super.onContextItemSelected(item);
	}
	
	//-----------------------------------------------------------------------------------------
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		_selected = _dayEvents.get(info.position);

		menu.setHeaderTitle(_adapter.getItem(info.position));
		menu.add(0, v.getId(), 0, "modify");
		menu.add(0, v.getId(), 0, "remove");
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
}
