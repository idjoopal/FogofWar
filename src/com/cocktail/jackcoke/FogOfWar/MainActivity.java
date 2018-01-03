package com.cocktail.jackcoke.FogOfWar;

import com.cocktail.jackcoke.R;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	
	//initialize--------------------------------------------------------------
	Button startbtn, statusbtn, settingbtn,endbtn;
	ExitAlertDialog exitdlg;
	MediaPlayer mplayer;
	SoundPool pool;
	boolean effextflag,bgmflag;
	int menu_select;

	void init() {
		startbtn = (Button) findViewById(R.id.btn_start);
		startbtn.setOnClickListener(this);
		statusbtn = (Button) findViewById(R.id.btn_status);
		statusbtn.setOnClickListener(this);
		settingbtn = (Button) findViewById(R.id.btn_setting);
		settingbtn.setOnClickListener(this);
		endbtn = (Button)findViewById(R.id.btn_exit);
		endbtn.setOnClickListener(this);
		mplayer = new MediaPlayer();
		
		pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		menu_select = pool.load(this, R.raw.effect_menu_select,1);
	
	}
	//-------------------------------------------------------------------------

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_start:
			pool.play(menu_select, 1, 1, 0, 0, 1);
			Intent gstartIntent = new Intent(this, GameActivity.class);
			startActivity(gstartIntent);
			break;
		case R.id.btn_status:
			//pool.play(menu_select, 1, 1, 0, 0, 1);
			//Intent statusIntent = new Intent(this, StatusActivity.class);
			//startActivity(statusIntent);
			break;
		case R.id.btn_setting:
			pool.play(menu_select, 1, 1, 0, 0, 1);
			Intent settingIntent = new Intent(this, SettingActivity.class);
			startActivity(settingIntent);
			break;
		case R.id.btn_exit:
			pool.play(menu_select, 1, 1, 0, 0, 1);
			exitdlg = new ExitAlertDialog(this, this);
			LayoutParams params2 = exitdlg.getWindow().getAttributes();
			params2.width = LayoutParams.WRAP_CONTENT;
			params2.height = LayoutParams.WRAP_CONTENT;
			exitdlg.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params2);
			exitdlg.show();
			break;
		}

	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mplayer.stop();
		mplayer.release();
		mplayer = null;
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mplayer = MediaPlayer.create(MainActivity.this, R.raw.bgm_main);
		mplayer.setVolume(0.5f, 0.5f);
		mplayer.start();
		super.onResume();
	}

}
