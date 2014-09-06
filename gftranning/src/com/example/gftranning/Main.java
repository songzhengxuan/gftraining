package com.example.gftranning;

import com.example.gftranning.serial.SerialGameControlView;
import com.example.gftranning.serial.SerialGameControlView.INewGameStartAction;
import com.example.gftranning.serial.SerialGameController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Main extends Activity {
	private static final String STATE_KEY_SERIAL_GAME_CONTROL_DATA = "g_ctrl_data";
	private SerialGameController mSerialGameControll = new SerialGameController();
	private SerialGameControlView mSerialGameUI = new SerialGameControlView();

	private static final int START_REQ_SERIAL_GAME = 0;
	protected static final String TAG = Main.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		View v1 = findViewById(R.id.text1);
		v1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Main.this, TestActivity.class);
				startActivity(intent);
			}
		});
		v1.setVisibility(View.INVISIBLE);

		View v2 = findViewById(R.id.text2);
		final EditText countEdit = (EditText) findViewById(R.id.test_count_edit);
		final EditText distanceEdit = (EditText) findViewById(R.id.test_distance_edit);
		v2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Main.this, GameActivity.class);
				intent.putExtra(GameActivity.EXTRA_INT_TEST_COUNT, Integer.parseInt(countEdit.getText().toString()));
				intent.putExtra(GameActivity.EXTRA_INT_TEST_DISTANCE,
						Integer.parseInt(distanceEdit.getText().toString()));
				startActivity(intent);
			}
		});

		mSerialGameUI.findAllViews(this);
		mSerialGameUI.setController(mSerialGameControll);
		mSerialGameControll.setUI(mSerialGameUI);
		mSerialGameUI.setNewGameStarter(new INewGameStartAction() {

			@Override
			public void startNewGame(int testCount, int distance) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "startNewGame with " + testCount + "," + distance);
				}
				Intent intent = new Intent(Main.this, GameActivity.class);
				intent.putExtra(GameActivity.EXTRA_INT_TEST_COUNT, testCount);
				intent.putExtra(GameActivity.EXTRA_INT_TEST_DISTANCE, distance);

			}
		});
		
		if (mSerialGameControll != null) {
			Bundle data = savedInstanceState != null ? savedInstanceState.getBundle(STATE_KEY_SERIAL_GAME_CONTROL_DATA)
					: null;
			mSerialGameControll.setCurrentStateData(this, savedInstanceState);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mSerialGameControll != null) {
			Bundle state = mSerialGameControll.getCurrentStateData(this);
			outState.putBundle(STATE_KEY_SERIAL_GAME_CONTROL_DATA, state);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case START_REQ_SERIAL_GAME:
			if (mSerialGameControll != null) {
				mSerialGameControll.updateFromGameActivityResult(this, resultCode, data);
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

}
