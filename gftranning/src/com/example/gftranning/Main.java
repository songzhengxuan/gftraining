package com.example.gftranning;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.gftranning.highscore.ScoreProvider;
import com.example.gftranning.serial.SerialGameControlView;
import com.example.gftranning.serial.SerialGameControlView.INewGameStartAction;
import com.example.gftranning.serial.SerialGameController;

public class Main extends Activity {
	private static final String STATE_KEY_SERIAL_GAME_CONTROL_DATA = "g_ctrl_data";
	private SerialGameController mSerialGameControll = new SerialGameController();
	private SerialGameControlView mSerialGameUI = new SerialGameControlView();

	private static final int START_REQ_SERIAL_GAME = 0;
	protected static final String TAG = Main.class.getSimpleName();
	private static final String SP_KEY_GAME_TOTAL = "game_total";
	private static final String SP_KEY_EACH_TEST_TIME = "each_test_time";
	private static final String SP_KEY_TEST_DISTANCE = "test_distance";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (BuildConfig.DEBUG) {
			Cursor cursor = getContentResolver().query(ScoreProvider.CONTENT_URI, null, null, null, null);
		}

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
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Main.this);
				Editor editor = sp.edit();
				editor.putInt(SP_KEY_GAME_TOTAL, mSerialGameControll.getSerialGameTotal());
				editor.putInt(SP_KEY_EACH_TEST_TIME, mSerialGameControll.getTestTimeInEachTest());
				editor.putInt(SP_KEY_TEST_DISTANCE, mSerialGameControll.getTestDistance());
				editor.commit();

				Intent intent = new Intent(Main.this, GameActivity.class);
				intent.putExtra(GameActivity.EXTRA_INT_TEST_COUNT, testCount);
				intent.putExtra(GameActivity.EXTRA_INT_TEST_DISTANCE, distance);
				intent.putExtra(GameActivity.EXTRA_INT_MODE, GameActivity.MODE_SERIAL_GAME);
				startActivityForResult(intent, START_REQ_SERIAL_GAME);
			}
		});

		if (mSerialGameControll != null) {
			if (savedInstanceState != null) {
				mSerialGameControll.setCurrentStateData(this,
						savedInstanceState.getBundle(STATE_KEY_SERIAL_GAME_CONTROL_DATA));
			} else {
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
				int serialGameTotal = sp.getInt(SP_KEY_GAME_TOTAL, 3);
				int testTimeInEachTest = sp.getInt(SP_KEY_EACH_TEST_TIME, 5);
				int testDistance = sp.getInt(SP_KEY_TEST_DISTANCE, 1);
				mSerialGameControll.setCurrentStateData(this, null);
				mSerialGameControll.setTestDistance(testDistance);
				mSerialGameControll.setTestTimeInEachTest(testTimeInEachTest);
				mSerialGameControll.setSerialGameTotal(serialGameTotal);
			}
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
