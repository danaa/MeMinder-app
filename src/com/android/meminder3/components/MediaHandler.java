package com.android.meminder3.components;


import java.io.IOException;
import com.android.meminder3.R;
import android.content.Context;
import android.media.MediaPlayer;


public class MediaHandler 
{
	
	//private static final int MAX_PLAYS = 1;
	private static MediaHandler instance;
	private MediaPlayer _player;
	
	
	private MediaHandler(Context context)
	{
		_player = MediaPlayer.create(context, R.raw.dreamy);
		_player.setLooping(true);
	}
	
	
	public static MediaHandler getInstance(Context context)
	{
		if(instance == null)
		{
			instance = new MediaHandler(context);
		}
		return instance;
	}
	
	public void play()
	{
		_player.start();
	}
	
	public void stop()
	{
		_player.stop();
		try {
			_player.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
