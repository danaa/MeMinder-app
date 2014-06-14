package com.android.meminder3.gui;


import java.util.ArrayList;

import com.android.meminder3.components.Logic;
import com.android.meminder3.dataStractures.ActivityTemplateData;
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


public class TemplatesListScreen extends Activity
{

	private final String CUSTOM = "Custom";
	
	private ListView _templatesListView;
	private ArrayAdapter<String> _adapter;
	private String _menuSelected;


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		_menuSelected = null;
		
		// here we need to create a dynamic layout because we get the template list on runtime
		super.onCreate(savedInstanceState);
		setContentView(R.layout.templates_list);
				
		// tell the logic to get the templates list	
		ArrayList<String> templateTitles = Logic.getInstance().getTemplatesTitlesList();
		
		// show the list
		templateTitles.add(0, CUSTOM);
		_templatesListView = (ListView) findViewById(R.id.templateslist);	
		_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, templateTitles);
		_templatesListView.setAdapter(_adapter);
		this.registerForContextMenu(_templatesListView);
		
		_templatesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
			{
				
				ActivityTemplateData data = null; 
				String title = _adapter.getItem(position);
				
				if(!title.equals(CUSTOM)) // if this is NOT a custom event
				{
					data = Logic.getInstance().getTemplateByTitle(title);
				}
				
				Intent intent = new Intent(TemplatesListScreen.this, AddActivityScreen.class);
				intent.putExtra("mode", AddActivityScreen.CREATE);
				
				if(data == null) // this is a new template
				{
					intent.putExtra("isNew", true);
				}
				else
				{
					intent.putExtra("title", title);
					intent.putExtra("templateData", data);
				}
				
				startActivity(intent);
				finish();
			}
		});
		
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		_menuSelected = _adapter.getItem(info.position);
		
		if(!_menuSelected.equals(CUSTOM))
		{
			menu.setHeaderTitle(_adapter.getItem(info.position));
			menu.add(0, v.getId(), 0, "modify");
			menu.add(0, v.getId(), 0, "remove");
		}
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		if(item.getTitle().equals("modify"))
		{
			ActivityTemplateData data = Logic.getInstance().getTemplateByTitle(_menuSelected);
			Intent intent = new Intent(TemplatesListScreen.this, AddActivityScreen.class);
			intent.putExtra("mode", AddActivityScreen.MODIFY_TEMPLATE);
			intent.putExtra("templateData", data);
			startActivity(intent);
			finish();
		}
		else if(item.getTitle().equals("remove"))
		{
			// TODO: ask if sure
			Logic.getInstance().removeTemplateByTitle(_menuSelected);
			_adapter.remove(_menuSelected);
		}
		
		return super.onContextItemSelected(item);
	}
	
	//--------------------------------------------------------------------------------
	
	
}
