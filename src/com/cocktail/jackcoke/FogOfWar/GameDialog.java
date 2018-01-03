package com.cocktail.jackcoke.FogOfWar;

import com.cocktail.jackcoke.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class GameDialog extends Dialog implements OnClickListener{
	
	Button return_btn , testbtn, gmaestartbtn;
	Context context;
	GameActivity main;
	SoundPool pool;
	TextView tv;
	int gunshot;
	float effectVol,bgmVol;
	
	
	void init(){
		return_btn = (Button)findViewById(R.id.btn_gamestartdlg_return);
		return_btn.setOnClickListener(this);
		testbtn = (Button)findViewById(R.id.btn_sound_test);
		testbtn.setOnClickListener(this);
		gmaestartbtn = (Button)findViewById(R.id.btn_gamestart);
		gmaestartbtn.setOnClickListener(this);
		
		pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		gunshot = pool.load(context, R.raw.effect_gunshot,1);
		tv = (TextView) findViewById(R.id.testTV);
		String tt = ""+effectVol;
		tv.setText(tt);
	}

	//constructor---------------------------------------------------------------
	public GameDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_start_dlg);
		this.context = context;
		init();
	}
	
	public GameDialog(Context context, GameActivity gameActivity) {
		super(context);
		// TODO Auto-generated constructor stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game_start_dlg);
		this.context = context;
		this.main = gameActivity;
		init();
	}
	//-------------------------------------------------------------------------

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_gamestartdlg_return:
			this.dismiss();
			break;
			
		case R.id.btn_sound_test:
			pool.play(gunshot, effectVol, effectVol, 0, 0, 1);
			break;
			
		case R.id.btn_gamestart:
			//((MainActivity)context).gameStart();
			break;
		}
		
	}
	

	
	
	

}
