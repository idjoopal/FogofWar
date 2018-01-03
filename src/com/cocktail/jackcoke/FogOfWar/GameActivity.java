
package com.cocktail.jackcoke.FogOfWar;

import com.cocktail.jackcoke.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class GameActivity extends Activity {
	

	MediaPlayer mplayer;
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService mChatService = null;
    
    public int flag;
    
    
    int my_x, my_y;
    public static int enemy_cx,enemy_cy;	// 상대 탱크 위치
    public static int enemy_x, enemy_y;	//상대가 가르키는 좌표
	public static int getEnemy_cx() {
		return enemy_cx;
	}
	public static int getEnemy_cy() {
		return enemy_cy;
	}
	public static int getEnemy_x() {
		return enemy_x;
	}
	public static int getEnemy_y() {
		return enemy_y;
	}
	


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the window layout
        setContentView(R.layout.main);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        
        // 화면 안꺼지게 하기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
        
        //게임다이얼로그 띄우기
        mplayer = new MediaPlayer();
    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mplayer.stop();
		mplayer.release();
		mplayer = null;
		super.onPause();
	}
	public void setatDialog(){
		GameDialog startdlg = new GameDialog(this,this);
        LayoutParams params = startdlg.getWindow().getAttributes();
        params.width = 1500;
        params.height = 800;
        startdlg.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    	startdlg.show();
		
	}
	
	public void mySetText(){
		my_x = GameView.getSendx();
		my_y = GameView.getSendy();
	}

    @Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        
        mplayer = MediaPlayer.create(GameActivity.this, R.raw.bgm_game);
        mplayer.setVolume(0.5f, 0.5f);
		mplayer.start();
		super.onResume();

        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }
    

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
    }

    
    
    public void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }


    public void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
	}

	public static final byte[] intToByteArray(int value){
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

	public static int byteToInt(byte[] src) {
		int newValue = 0;
		newValue |= (((int) src[0]) << 24) & 0xFF000000;
		newValue |= (((int) src[1]) << 16) & 0xFF0000;
		newValue |= (((int) src[2]) << 8) & 0xFF00;
		newValue |= (((int) src[3])) & 0xFF;
		return newValue;
	}
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
        	byte[] send = intToByteArray(my_x);
            mChatService.write(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }
    
    public void sendMessage(int message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message != 0) {
        	byte[] send = intToByteArray(message);
            mChatService.write(send);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    public TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };
    
    // The Handler that gets information back from the BluetoothChatService
    public final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //리스트 뷰에 써주는 것
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                int tmp = byteToInt(readBuf);
                flag = tmp%10;
                enemy_x = (tmp/10)/10000;	//tox,toy값
                enemy_y = (tmp/10)%10000;
           
                switch(flag){
                case 0:
                	GameView.setActionFlag(GameView.ACTIONFLAG.MOVE);
                	enemy_cx = enemy_x;	//move일땐 상대의 현재위치를 바꿔준다
                	enemy_cy = enemy_y;
                	break;
                case 1:
                	GameView.setAttackType(GameView.ATTACKTYPE.BOOM);
                	break;
                case 2:
                	GameView.setAttackType(GameView.ATTACKTYPE.GUNSHOT);
                	break;
                case 6://진동
                	Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(1000);
                	break;
                case 7:
                	GameView.setActionFlag(GameView.ACTIONFLAG.INCOMMING);
                	GameView.handler.sendEmptyMessage(0);
                	break;
                case 8:	//turn end
                	break;
                case 9:
                	GameView.tmpWin();
                	break;
                	
              
                }
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                GameView.setActionFlag(GameView.ACTIONFLAG.INIT);
                chooseTurn();
                GameView.handler.sendEmptyMessage(0);
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    
    void chooseTurn(){
    	if(GameView.getSendx() < GameView.getMyCx_send()){
    		GameView.setMyturn(true);
    	Toast.makeText(this,
				"내 차례 입니다. 이동 후 공격 하십시오.", Toast.LENGTH_LONG)
				.show();
    	}else{
    		Toast.makeText(this,
    				"상대의 공격이 끝난 후 이동하십시오.", Toast.LENGTH_LONG)
    				.show();
    		
    	}
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    
    //액션 바 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }
  
    //액션 바 메뉴 클릭 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.secure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        case R.id.insecure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        }
        return false;
    }

}
