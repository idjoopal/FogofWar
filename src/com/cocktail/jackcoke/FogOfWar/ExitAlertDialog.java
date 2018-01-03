package com.cocktail.jackcoke.FogOfWar;

import com.cocktail.jackcoke.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class ExitAlertDialog extends Dialog implements OnClickListener{
	
	Button extbtn, cancelbtn;
	Context context;
	MainActivity main;
	
	
	
	void init(){
		extbtn = (Button)findViewById(R.id.btn_exitalert_exit);
		extbtn.setOnClickListener(this);
		cancelbtn  = (Button)findViewById(R.id.btn_exitalert_cancel);
		cancelbtn.setOnClickListener(this);
	}

	//constructor---------------------------------------------------------------
	public ExitAlertDialog(Context context, MainActivity main) {
		super(context);
		// TODO Auto-generated constructor stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exit_alert_dlg);
		this.context = context;
		this.main = main;
		init();
	}
	//-------------------------------------------------------------------------

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_exitalert_exit:
			main.finish();
			break;
		case R.id.btn_exitalert_cancel:
			this.dismiss();
			break;
		}
		
	}
	
	
	

}
