package com.cocktail.jackcoke.FogOfWar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class StatusView extends View {

	// initialize------------------------------------------------------------------
	float width, height, cx, cy;
	Context context;
	Paint paint;
	View view;
	DisplayMetrics metrics = new DisplayMetrics();
	float densityScale; // dp∑Œ πŸ≤„¡ÿ¥Ÿ.
    
	public void init() {
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getMetrics(metrics);
		densityScale = metrics.density;
		Point point = new Point();
		display.getSize(point);
		width = point.x;
		height = point.y;
		cx = width / 2;
		cy = height / 2;
		paint = new Paint();
	}
	//-------------------------------------------------------------------------

	//constructor---------------------------------------------------------------
	public StatusView(Context context) {
		super(context);
		this.context = context;
		init();
		// TODO Auto-generated constructor stub
	}

	public StatusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
		// TODO Auto-generated constructor stub
	}

	public StatusView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
		// TODO Auto-generated constructor stub
	}
	//-------------------------------------------------------------------------


	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	
	}
}
