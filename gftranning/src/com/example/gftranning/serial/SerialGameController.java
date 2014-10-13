package com.example.gftranning.serial;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.gftranning.GameActivity;

/**
 * onCreate and onActivityResult, which come first
 */
public class SerialGameController {
	private GameState mCurGameState;

	private int mSerialGameTotal;

	private int mTestTimeInEachTest;

	private int mTestDistance;

	private int mSerialGameProgress;

	private int mTotalTestTimeInSerial;

	private int mTotalCorrectTimeInSerial;

	private SerialGameControlView mSerialGameUI;

	private volatile boolean mUIAutoUpdate = true;

	private static final String STATE_KEY_GAME_STATE = "SerialGameController.game_state";

	private static final String STATE_KEY_GAME_PROGRESS = "SerialGameController.game_progress";

	private static final String STATE_KEY_TOTAL_TEST_TIME = "SerialGameController.total_test_time";

	private static final String STATE_KEY_TOTAL_CORRECT_TIME = "SerialGameController.total_correct_time";

	private static final String STATE_KEY_GAME_TOTAL = "SerialGameController.game_total";

	private static final String STATE_KEY_EACH_TEST_TIME = "SerialGameController.each_test_time";

	private static final String STATE_KEY_TEST_DISTANCE = "SerialGameController.test_distance";

	public Bundle getCurrentStateData(Context context) {
		Bundle bundle = new Bundle();
		bundle.putInt(STATE_KEY_GAME_STATE, mCurGameState.ordinal());
		bundle.putInt(STATE_KEY_GAME_TOTAL, mSerialGameTotal);
		bundle.putInt(STATE_KEY_EACH_TEST_TIME, mTestTimeInEachTest);
		bundle.putInt(STATE_KEY_TEST_DISTANCE, mTestDistance);
		bundle.putInt(STATE_KEY_GAME_PROGRESS, mSerialGameProgress);
		bundle.putInt(STATE_KEY_TOTAL_TEST_TIME, mTotalTestTimeInSerial);
		bundle.putInt(STATE_KEY_TOTAL_CORRECT_TIME, mTotalCorrectTimeInSerial);
		return bundle;
	}

	public void setCurrentStateData(Context context, Bundle data) {
		if (data == null) {
			mCurGameState = GameState.NewGame;
		} else {
			checkAllStateKeyOrThrow(data);
			mCurGameState = GameState.values()[data.getInt(STATE_KEY_GAME_STATE)];
			mSerialGameTotal = data.getInt(STATE_KEY_GAME_TOTAL);
			mTestTimeInEachTest = data.getInt(STATE_KEY_EACH_TEST_TIME);
			mTestDistance = data.getInt(STATE_KEY_TEST_DISTANCE);
			mSerialGameProgress = data.getInt(STATE_KEY_GAME_PROGRESS);
			mTotalTestTimeInSerial = data.getInt(STATE_KEY_TOTAL_TEST_TIME);
			mTotalCorrectTimeInSerial = data.getInt(STATE_KEY_TOTAL_CORRECT_TIME);
		}
		if (mUIAutoUpdate) {
			updateUI();
		}
	}

	private void checkAllStateKeyOrThrow(Bundle data) {
		if (data.containsKey(STATE_KEY_EACH_TEST_TIME) && data.containsKey(STATE_KEY_GAME_PROGRESS)
				&& data.containsKey(STATE_KEY_GAME_STATE) && data.containsKey(STATE_KEY_GAME_TOTAL)
				&& data.containsKey(STATE_KEY_TEST_DISTANCE) && data.containsKey(STATE_KEY_TOTAL_CORRECT_TIME)
				&& data.containsKey(STATE_KEY_TOTAL_TEST_TIME)) {
			return;
		}
		throw new IllegalArgumentException("invalid state");
	}

	public void updateUI() {
		if (mSerialGameUI != null) {
			mSerialGameUI.updateUI();
		}
	}

	public boolean setAutoUIUpdate(boolean enable) {
		boolean oldValue = mUIAutoUpdate;
		mUIAutoUpdate = enable;
		return oldValue;
	}

	public void setCurrentState(Context context, GameState state) {
		mCurGameState = state;
		if (mUIAutoUpdate) {
			updateUI();
		}
	}

	public GameState getCurrentState(Context context) {
		return mCurGameState;
	}

	/**
	 * @param context
	 * @param data
	 */
	public void updateFromGameActivityResult(Context context, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		ArrayList<Integer> testCorrectTime = data.getIntegerArrayListExtra(GameActivity.RESULT_KEY_TEST_CORRECT);
		ArrayList<Integer> testTotalTime = data.getIntegerArrayListExtra(GameActivity.RESULT_KEY_TEST_TOTAL);
		if (testCorrectTime == null || testTotalTime == null) {
			return;
		}
		if (testCorrectTime.size() != testTotalTime.size()) {
			throw new IllegalArgumentException("invalid game result");
		}
		for (Integer value : testCorrectTime) {
			mTotalCorrectTimeInSerial += value.intValue();
		}
		for (Integer value : testTotalTime) {
			mTotalTestTimeInSerial += value.intValue();
		}
		mSerialGameProgress += (testCorrectTime.size());
		if (mSerialGameProgress == mSerialGameTotal) {
			mCurGameState = GameState.GameEnd;
		} else if (mSerialGameProgress < mSerialGameTotal && mSerialGameProgress > 0) {
			mCurGameState = GameState.InGame;
		} else {
			throw new IllegalStateException("invalid progress");
		}
		updateUI();
	}

	/**
	 * change after update
	 * 
	 * @return the finished game count in a serial
	 */
	public int getSerialProgress() {
		return mSerialGameProgress;
	}

	/**
	 * change after update
	 * 
	 * @return the count of test time in all finished games in a serial
	 */
	public int getTotalTestTimeInSerial() {
		return mTotalTestTimeInSerial;
	}

	/**
	 * change after update
	 * 
	 * @return the count of correct time in all finished games in a serial
	 */
	public int getTotalCorrectTimeInSerial() {
		return mTotalCorrectTimeInSerial;
	}

	public int getSerialGameTotal() {
		return mSerialGameTotal;
	}

	public int getSerialGameMaxTotal() {
		return 10;
	}

	public void setSerialGameProgress(int progress) {
		mSerialGameProgress = progress;
		if (mUIAutoUpdate) {
			updateUI();
		}
	}

	public int getSerialGameProgress() {
		return mSerialGameProgress;
	}

	public void setSerialGameTotal(int serialGameTotal) {
		if (mSerialGameTotal == serialGameTotal) {
			return;
		}
		mSerialGameTotal = serialGameTotal;
		if (mUIAutoUpdate) {
			updateUI();
		}
	}

	public int getTestTimeInEachTest() {
		return mTestTimeInEachTest;
	}

	public void setTestTimeInEachTest(int testTimeInEachTest) {
		if (mTestTimeInEachTest == testTimeInEachTest) {
			return;
		}
		mTestTimeInEachTest = testTimeInEachTest;
		if (mUIAutoUpdate) {
			updateUI();
		}
	}

	public int getMaxTestTimeInEachTest() {
		return 10;
	}

	public int getTestDistance() {
		return mTestDistance;
	}

	public void setTestDistance(int testDistance) {
		if (mTestDistance == testDistance) {
			return;
		}
		mTestDistance = testDistance;
		if (mUIAutoUpdate) {
			updateUI();
		}
	}

	public int getMaxTestDistance() {
		return 5;
	}

	public GameState getSerialGameState() {
		return mCurGameState;
	}

	public void setUI(SerialGameControlView serialGameControlView) {
		mSerialGameUI = serialGameControlView;
	}
}
