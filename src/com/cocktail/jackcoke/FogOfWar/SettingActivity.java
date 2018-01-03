package com.cocktail.jackcoke.FogOfWar;

import com.cocktail.jackcoke.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 프레퍼런스 저장이 제대로 안되고있음 >> 세팅에 관한 정보 저장해야하는데;; 왜안되는걸까
 * 
 * 
 */

public class SettingActivity extends Activity implements OnClickListener {

	Button effect_onoff, bgm_onoff;

	public static boolean effectFlag, bgmFlag;

	// initialize---------------------------------------------------------------
	void init() {
		effect_onoff = (Button) findViewById(R.id.btn_setting_effect_onoff);
		effect_onoff.setOnClickListener(this);
		bgm_onoff = (Button) findViewById(R.id.btn_setting_bgm_onoff);
		bgm_onoff.setOnClickListener(this);
		effectFlag = true;
		bgmFlag = true;
		
		if(effectFlag)
			effect_onoff.setBackgroundResource(R.drawable.btn_on);
		else
			effect_onoff.setBackgroundResource(R.drawable.btn_off);
		
		if(bgmFlag)
			bgm_onoff.setBackgroundResource(R.drawable.btn_on);
		else
			bgm_onoff.setBackgroundResource(R.drawable.btn_off);
			
	}

	// getters&setters
	// ----------------------------------------------------------

	// -------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// -------------------------------------------------------------------------

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		// effect
		case R.id.btn_setting_effect_onoff:
			if (effectFlag){
				effectFlag = false;
				effect_onoff.setBackgroundResource(R.drawable.btn_off);
			}
			else{
				effectFlag = true;
				effect_onoff.setBackgroundResource(R.drawable.btn_on);
			}
			break;

		// bgm
		case R.id.btn_setting_bgm_onoff:
			if (bgmFlag){
				bgmFlag = false;
				bgm_onoff.setBackgroundResource(R.drawable.btn_off);
			}
			else{
				bgm_onoff.setBackgroundResource(R.drawable.btn_on);
				bgmFlag = true;
			}
			break;

		}//

	}

}
