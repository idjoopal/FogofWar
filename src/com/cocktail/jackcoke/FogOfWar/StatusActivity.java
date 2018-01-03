package com.cocktail.jackcoke.FogOfWar;

import com.cocktail.jackcoke.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StatusActivity extends Activity implements OnClickListener{
	
	Button backbtn;
	
	void init(){
		backbtn = (Button) findViewById(R.id.btn_status_back);
		backbtn.setOnClickListener(this);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_activity);
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
		switch(v.getId()){
		case R.id.btn_status_back:
			this.finish();
			break;
		}
		
	}

}
