package com.cocktail.jackcoke.FogOfWar;

import com.cocktail.jackcoke.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class GameView extends View implements OnTouchListener {

	int flag = 0; // 0:move, 1:attack_1, 2:attack_2
					// 6: vib 7:incomming, 9:win
					// 8: turn_end

	enum ACTIONFLAG {
		MOVE, ATTACK, INCOMMING, INIT, LOSE ,VIB
	}

	enum ATTACKTYPE {
		BOOM, GUNSHOT
	}

	// 턴 정하기
	public static boolean isMyturn = true;

	int turn_count = 0;

	// 게임
	public float moveableRange = 500;
	public float attackRange = 700;
	float realCx, realCy, realTocx, realTocy;
	static int sendx, sendy, sendString, myCx_send, myCy_send;
	static ACTIONFLAG actionFlag = ACTIONFLAG.MOVE;
	static ATTACKTYPE attackType = ATTACKTYPE.BOOM;

	float tcx, tcy;
	float height, width;
	static Context context;
	boolean isVisible = false;
	static boolean attackingFlag = false;
	static boolean moveingFlag = false;

	Bitmap tank_01, tank_02, tank_03, tank_04, background, ui, ui_hp1, ui_hp2;
	int tankflag = 1, incompingType;
	Paint paint;

	static MyHandler handler;
	ActionMove move;
	ActionAttack attack;
	Incomming incomming;

	float dp, scale;
	int marginTop;
	float degree_attack = 0;
	float degree_move = 0;

	// sound
	SoundPool pool;
	int effect_boom,effect_gunshot;
	

	// getters&setters--------------------------------------------------------------------------------------------
	public static int getSendy() {
		return sendy;
	}

	public static int getSendx() {
		return sendx;
	}

	public static void setActionFlag(ACTIONFLAG actionFlag) {
		GameView.actionFlag = actionFlag;
	}

	public static void setAttackType(ATTACKTYPE attackType) {
		GameView.attackType = attackType;
	}

	public static void setMyturn(boolean isMyturn) {
		GameView.isMyturn = isMyturn;
	}

	public static boolean isMyturn() {
		return isMyturn;
	}

	public static boolean isAttackingFlag() {
		return attackingFlag;
	}

	public static boolean isMoveingFlag() {
		return moveingFlag;
	}

	public static int getMyCy_send() {
		return myCy_send;
	}

	public static int getMyCx_send() {
		return myCx_send;
	}

	// initialize-------------------------------------------------------------------------------------------------
	public void init() {
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		width = point.x;
		height = point.y;

		scale = width / 14;

		// 이미지
		tank_01 = BitmapFactory.decodeResource(getResources(),
				R.drawable.tank_01);
		tank_02 = BitmapFactory.decodeResource(getResources(),
				R.drawable.tank_02);
		tank_03 = BitmapFactory.decodeResource(getResources(),
				R.drawable.tank_03);
		tank_04 = BitmapFactory.decodeResource(getResources(),
				R.drawable.tank_04);
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.game_bg);
		ui = BitmapFactory
				.decodeResource(getResources(), R.drawable.ui_game_01);

		// 이미지 스케일링
		tank_01 = Bitmap.createScaledBitmap(tank_01, (int) scale, (int) scale,
				true);
		tank_02 = Bitmap.createScaledBitmap(tank_02, (int) scale, (int) scale,
				true);
		tank_03 = Bitmap.createScaledBitmap(tank_03, (int) scale, (int) scale,
				true);
		tank_04 = Bitmap.createScaledBitmap(tank_04, (int) scale, (int) scale,
				true);
		background = Bitmap.createScaledBitmap(background, (int) width,
				(int) height, true);
		ui = Bitmap.createScaledBitmap(ui, (int) width, (int) (width / 8.3),
				true);

		// padding
		marginTop = (int) ((width / 8.3) + (scale / 2));
		setOnTouchListener(this);

		handler = new MyHandler();
		paint = new Paint();
		move = new ActionMove();
		attack = new ActionAttack();
		incomming = new Incomming(this.context);

		moveableRange = (moveableRange / 2000 * width);
		attackRange = (attackRange / 2000 * width);

		DisplayMetrics outMetrics = new DisplayMetrics();
		((GameActivity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(outMetrics);
		dp = 1 / outMetrics.density;

		realCx = (float) (Math.random() * width);
		realCy = (float) (Math.random() * height);
		if (realCy < marginTop) {
			realCy += marginTop;
		}
		tcx = realCx;
		tcy = realCy;

		myCx_send = (int) (tcx / width * 2000);
		myCy_send = (int) (tcy / width * 2000);
		
		pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		effect_boom = pool.load(context, R.raw.effect_explode,1);
		effect_gunshot = pool.load(context, R.raw.effect_gunshot,1);

	}

	// constructor--------------------------------------------------------------------------------------------------
	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();

	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	// --------------------------------------------------------------------------------------------------------------

	// 좌표계 변경
	public void sendMessageWithFlag(int flag) {
		sendx = (int) (sendx / width * 2000);
		sendy = (int) (sendy / width * 2000);
		sendString = sendx * 100000 + sendy * 10 + flag; // 마지막 0 == flag
		((GameActivity) context).sendMessage(sendString);
	}

	// 탱크그리기
	void drawTank(Canvas canvas) {
		if (actionFlag == ACTIONFLAG.MOVE) { // 이동모드일때
			if (tcx - realCx != 0) {
				if (tcx <= realCx) {
					degree_move = (float) ((float) 180 / Math.PI * Math
							.atan((float) (tcy - realCy)
									/ (float) (tcx - realCx))) - 90;
					canvas.rotate(degree_move, realCx, realCy); // 전역변수에 몸체각도
																// 저장후 회전
				} else {
					degree_move = (float) ((float) 180 / Math.PI * Math
							.atan((float) (tcy - realCy)
									/ (float) (tcx - realCx))) + 90;
					canvas.rotate(degree_move, realCx, realCy); // 전역변수에 몸체각도
																// 저장후 회전

				}
				switch (tankflag) {
				case 1:
					canvas.drawBitmap(tank_01, realCx - (scale / 2), realCy
							- (scale / 2), null);
					break;
				case 2:
					canvas.drawBitmap(tank_02, realCx - (scale / 2), realCy
							- (scale / 2), null);
					break;
				case 3:
					canvas.drawBitmap(tank_03, realCx - (scale / 2), realCy
							- (scale / 2), null);
					break;
				}
			} else {
				switch (tankflag) {
				case 1:
					canvas.drawBitmap(tank_01, realCx - (scale / 2), realCy
							- (scale / 2), null);
					break;
				case 2:
					canvas.drawBitmap(tank_02, realCx - (scale / 2), realCy
							- (scale / 2), null);
					break;
				case 3:
					canvas.drawBitmap(tank_03, realCx - (scale / 2), realCy
							- (scale / 2), null);
					break;
				}
			}
			canvas.restore();
			canvas.rotate(degree_attack, realCx, realCy);
			canvas.drawBitmap(tank_04, realCx - (scale / 2), realCy
					- (scale / 2), null);
			canvas.restore();

		}else if (actionFlag == ACTIONFLAG.ATTACK) { // 공격모드일때

			canvas.rotate(degree_move, realCx, realCy); // 일단 몸체의 각도를 불러와서
			canvas.drawBitmap(tank_01, realCx - (scale / 2), realCy
					- (scale / 2), null); // 몸체를 그리고
			canvas.restore(); // 캔버스 원상복귀

			if (tcx - realCx != 0) {
				if (tcx <= realCx) {
					degree_attack = (float) ((float) 180 / Math.PI * Math
							.atan((float) (tcy - realCy)
									/ (float) (tcx - realCx))) - 90;
					canvas.rotate(degree_attack, realCx, realCy); // 포탑의각도를 저장후
																	// 회전
				} else {
					degree_attack = (float) ((float) 180 / Math.PI * Math
							.atan((float) (tcy - realCy)
									/ (float) (tcx - realCx))) + 90;
					canvas.rotate(degree_attack, realCx, realCy); // 포탑의각도를 저장후
																	// 회전
				}
				canvas.drawBitmap(tank_04, realCx - (scale / 2), realCy
						- (scale / 2), null);
				canvas.restore();
			} else {
				canvas.restore();
				canvas.drawBitmap(tank_04, realCx - (scale / 2), realCy
						- (scale / 2), null);
				canvas.restore();
			}

		}else{
			canvas.rotate(degree_move,realCx,realCy);
			canvas.drawBitmap(tank_01, realCx-(scale/2), realCy-(scale/2),null);
			canvas.restore();
			canvas.rotate(degree_attack,realCx,realCy);
			canvas.drawBitmap(tank_04, realCx-(scale/2), realCy-(scale/2),null);
			canvas.restore();
			
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		// 백그라운드
		canvas.drawBitmap(background, 0, 0, null);
		canvas.drawBitmap(ui, 0, 0, null);

		switch (actionFlag) {
		case MOVE:
			move.MoveDraw(canvas);
			break;
		case ATTACK:
			attack.AttackDraw(canvas);
			break;
		case INCOMMING:
			incomming.IncommingAttackDraw(canvas);
			break;
		default:
			break;
		}
		// 땅크
		drawTank(canvas);
		
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		int action = event.getAction();
		switch (actionFlag) {
		case MOVE:
			if (!attackingFlag && !moveingFlag)
				move.MoveEvent(event, action);
			break;
		case ATTACK:
			if (!attackingFlag && !moveingFlag)
				attack.AttackEvent(event, action);
			break;
		default:
			break;
		}
		return true;
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (actionFlag) {
			case MOVE:
				move.MoveHandler();
				break;
			case ATTACK:
				attack.AttackHandler();
				break;
			case INCOMMING:
				incomming.initIncomming();
				incomming.IncommingAttackHandler();
				break;
			case INIT:
				sendx = (int) realCx;
				sendy = (int) realCy;
				sendMessageWithFlag(0);
				actionFlag = GameView.ACTIONFLAG.MOVE;
				break;
			case LOSE:
				sendMessageWithFlag(9);
				 lose();
				break;
			case VIB:
				sendMessageWithFlag(6);
				break;
			}
		}

	}
	
	void lose(){
		
		Toast.makeText(context, "패배", Toast.LENGTH_LONG).show();
		Intent lose = new Intent(context,Lose.class); 
		((GameActivity)context).startActivity(lose);
		((GameActivity)context).finish();
	}
	
	static void tmpWin(){
		Toast.makeText(context, "승리", Toast.LENGTH_LONG).show();

		Intent win = new Intent(context,Win.class); 
		((GameActivity)context).startActivity(win);
		((GameActivity)context).finish();
	}

	// /////////////////////////////////ACTION_MOVE///////////////////////////////////////////////////////////////////////////
	class ActionMove {
		int animationcount = 0;
		int animationSlower = 0;
		boolean moveFlag = false;

		public void MoveEvent(MotionEvent event, int action) {
			float dx = 0;
			float dy = 0;
			double r = 0;
			float dx2 = 0;
			float dy2 = 0;
			double r2 = 0;

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				tcx = realCx;
				tcy = realCy;
				dx = event.getX() - realCx;
				dy = event.getY() - realCy;
				r = Math.sqrt(dx * dx + dy * dy);
				if (r <= 80) {
					isVisible = true;
					moveFlag = true;
					invalidate();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				dx2 = realCx - tcx;
				dy2 = realCy - tcy;
				r2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
				if (r2 <= moveableRange) {
					tcx = event.getX();
					tcy = event.getY();
				}

				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				isVisible = false;
				if (moveFlag) {
					MoveTo(tcx, tcy);
				} else {
					invalidate();
				}
				if (!attackingFlag && !moveingFlag && moveFlag)
					moveingFlag = true;
				moveFlag = false;
				break;
			}
		}

		public void MoveDraw(Canvas canvas) {
			paint.setColor(Color.GREEN);
			if (isVisible && isMyturn) {
				paint.setColor(Color.CYAN);
				paint.setAlpha(300);
				canvas.drawCircle(realCx, realCy, moveableRange, paint); // 이동반경
				canvas.drawCircle(tcx, tcy, 30, paint); // 이동도착점1
				canvas.drawCircle(tcx, tcy, 50, paint); // 이동 도착점2
				paint.setColor(Color.RED);
				canvas.drawLine(realCx, realCy, tcx, tcy, paint); // 이동경로 선
			}
		}

		public void MoveHandler() {
			if (moveingFlag) {
				switch (tankflag) {
				case 1:
					tankflag++;
					break;
				case 2:
					tankflag++;
					break;
				case 3:
					tankflag = 1;
					break;
				}
			}

			// 화면 밖으로 못나가게하기
			if (realCx <= 40) {
				realCx += 1;
			} else if (realCx >= width - 40) {
				realCx -= 1;
			}
			if (realCy <= marginTop) {
				realCy += 1;
			} else if (realCy >= height - 40) {
				realCy -= 1;
			}

			// Move Animation
			if (moveingFlag && isMyturn)
				if (animationcount < 100) {
					if (animationSlower % 4 == 0) {
						realCx += (realTocx - realCx) / 100;
						realCy += (realTocy - realCy) / 100;
						animationcount++;
					}
					animationSlower++;
					handler.sendEmptyMessage(1);
				} else {
					moveingFlag = false;
					animationcount = 0;

					sendx = (int) realCx;
					sendy = (int) realCy;
					sendMessageWithFlag(0);
					actionFlag = GameView.ACTIONFLAG.ATTACK;
				}
			invalidate();
		}

		public void MoveTo(float x, float y) {
			realTocx = x;
			realTocy = y;
			handler.sendEmptyMessage(1);
			invalidate();
		}
	}

	// /////////////////////////////////ACTION_ATTACK/////////////////////////////////////////////////////////////////////////

	class ActionAttack {
		boolean attackFlag = false;
		float acx;
		float acy;
		int animationcount = 0;
		int attflag = 1;

		public void AttackEvent(MotionEvent event, int action) {
			float dx = 0;
			float dy = 0;
			double r = 0;
			float dx2 = 0;
			float dy2 = 0;
			double r2 = 0;

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				tcx = realCx;
				tcy = realCy;
				acx = realCx;
				acy = realCy;
				dx = event.getX() - realCx;
				dy = event.getY() - realCy;
				r = Math.sqrt(dx * dx + dy * dy);
				if (r <= 80) {
					isVisible = true;
					invalidate();
					attackFlag = true;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				dx2 = realCx - tcx;
				dy2 = realCy - tcy;
				r2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
				if (r2 <= attackRange) {
					tcx = event.getX();
					tcy = event.getY();
				}
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				isVisible = false;
				if (attackFlag) {
					attackingFlag = true;
					AttackTo(tcx, tcy);
				} else {
					invalidate();
				}
				break;
			}

		}

		public void AttackDraw(Canvas canvas) {

			// 공격 오브젝트
			switch (attackType) {
			case BOOM:
				attflag = 1;
				if (isVisible) {
					paint.setColor(Color.RED);
					paint.setAlpha(30);
					canvas.drawCircle(realCx, realCy, attackRange, paint); // 공격반경
					canvas.drawCircle(tcx, tcy, 30, paint); // 공격 도착점
					paint.setColor(Color.BLUE);
					canvas.drawLine(realCx, realCy, tcx, tcy, paint); // 공격경로 선
				}

				if (attackFlag) {
					paint.setColor(Color.WHITE);
					canvas.drawCircle(acx, acy, 10, paint);
					if (animationcount >= 98) {
						paint.setColor(Color.RED);
						canvas.drawCircle(acx, acy, 40, paint); // 폭파이펙트
						pool.play(effect_boom, 1, 1, 0, 0, 1);
					}
					
				}
				break;
			case GUNSHOT:
				attflag = 2;
				if (isVisible) {
					paint.setColor(Color.RED);
					paint.setAlpha(30);
					canvas.drawCircle(realCx, realCy, attackRange, paint); // 공격반경
					canvas.drawCircle(tcx, tcy, 30, paint); // 공격 도착점
					paint.setColor(Color.BLUE);
					canvas.drawLine(realCx, realCy, tcx, tcy, paint); // 공격 경로 선

				}

				if (attackFlag) {
					paint.setColor(Color.WHITE);
					canvas.drawCircle(acx, acy, 10, paint);
				}
				break;
			}

		}

		public void AttackHandler() {

			// Attack Animation
			if (animationcount < 100) {
				acx += (realTocx - realCx) / 100;
				acy += (realTocy - realCy) / 100;
				animationcount++;
				handler.sendEmptyMessage(1);
			} else {
				attackingFlag = false;
				attackFlag = false;
				animationcount = 0;
				sendx = (int) acx;
				sendy = (int) acy;
				sendMessageWithFlag(7);
			}
			invalidate();
		}

		public void AttackTo(float x, float y) {
			realTocx = x;
			realTocy = y;
			handler.sendEmptyMessage(1);
			invalidate();
			pool.play(effect_gunshot, 1, 1, 0, 0, 1);
		}
	}

	// /////////////////////////////////Incomming/////////////////////////////////////////////////////////////////////////////
	class Incomming {
		boolean attackFlag = true;
		float acx = width / 2;
		float acy = height / 2;
		int animationcount = 0;
		float mtcx, mtcy;
		boolean initIncommingFlag = true;
		double r;
		Vibrator vibe;
		Incomming(Context context){
			vibe = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		}

		public void initIncomming() {
			if (initIncommingFlag) {
				this.acx = GameActivity.getEnemy_cx() * width / 2000;
				this.acy = GameActivity.getEnemy_cy() * width / 2000;
				incomming.IncommingAttackTo(GameActivity.getEnemy_x(), GameActivity.getEnemy_y());
				initIncommingFlag = false;
				attackFlag = true;
			}
		}

		public void IncommingAttackDraw(Canvas canvas) {
			// 공격 오브젝트
			switch (attackType) {
			case BOOM:
				if (attackFlag) {
					paint.setColor(Color.WHITE);
					r = Math.sqrt((double)((realCx-acx)*(realCx-acx)+(realCy-acx)*(realCy-acx)));
					if(r<width/3)
						canvas.drawCircle(this.acx, this.acy, 10, paint);
					if (animationcount >= 98) {
						paint.setColor(Color.RED);
						if(r<width/3)
							canvas.drawCircle(this.acx, this.acy, 40, paint); // 폭파
						pool.play(effect_boom, 1, 1, 0, 0, 1);
					}
				}
				break;
			case GUNSHOT:
				if (attackFlag) {
					paint.setColor(Color.WHITE);
					canvas.drawCircle(this.acx, this.acy, 10, paint);
				}
				break;
			}
		}

		public void IncommingAttackHandler() {
			//충돌 처리
		
			r = Math.sqrt((double)((realCx-mtcx)*(realCx-mtcx)+(realCy-mtcy)*(realCy-mtcy)));
			if(r<scale/3){
				actionFlag = GameView.ACTIONFLAG.VIB;
				handler.sendEmptyMessage(0);
		        vibe.vibrate(1000);
				actionFlag = GameView.ACTIONFLAG.LOSE;
				handler.sendEmptyMessage(0);
			}
			
			//
			
			
			if (animationcount < 100) {
				acx += (mtcx - GameActivity.getEnemy_cx() * width / 2000) / 100;
				acy += (mtcy - GameActivity.getEnemy_cy() * width / 2000) / 100;
				animationcount++;
				handler.sendEmptyMessage(0);
			} else {
				animationcount = 0;
				attackFlag = false;
				initIncommingFlag = true;
				actionFlag = GameView.ACTIONFLAG.INIT;
				Toast.makeText(getContext(),
						"내 차례 입니다. 이동 후 공격 하십시오.", Toast.LENGTH_LONG)
						.show();
			}
			invalidate();
		}

		public void IncommingAttackTo(float x, float y) {
			mtcx = x * width / 2000;
			mtcy = y * width / 2000;
			handler.sendEmptyMessage(0);
			invalidate();
			pool.play(effect_gunshot, 1, 1, 0, 0, 1);
		}

	}
}
