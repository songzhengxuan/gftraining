package com.example.gftranning.serial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * onCreate and onActivityResult, which come first
 * 
 */
public class SerialGameController {
	private GameState mCurGameState;
	private int mSerialGameTotal;
	private int mTestTimeInEachTest;
	private int mTestDistance;
	private static final String STATE_KEY_GAME_TOTAL = "SerialGameController.game_total";
	private static final String STATE_KEY_EACH_TEST_TIME = "SerialGameController.each_test_time";
	private static final String STATE_KEY_TEST_DISTANCE = "SerialGameController.test_distance";

	private int mSerialGameProgress;
	private int mTotalTestTimeInSerial;
	private int mTotalCorrectTimeInSerial;
	private SerialGameControlView mSerialGameUI;
	private static final String STATE_KEY_GAME_PROGRESS = "SerialGameController.game_progress";
	private static final String STATE_KEY_TOTAL_TEST_TIME = "SerialGameController.total_test_time";
	private static final String STATE_KEY_TOTAL_CORRECT_TIME = "SerialGameController.total_correct_time";

	public Bundle getCurrentStateData(Context context) {
		return null;
	}

	public void setCurrentStateData(Context context, Bundle data) {
		if (data == null) {
			mCurGameState = GameState.NewGame;
		} else {
			// TODO: restore old state of serial game
		}
		mSerialGameUI.updateUI();
	}

	public void setCurrentState(Context context, GameState state) {
		mCurGameState = state;
	}

	public GameState getCurrentState(Context context) {
		return mCurGameState;
	}

	/**
	 * @param context
	 * @param data
	 */
	public void updateFromGameActivityResult(Context context, int resultCode, Intent data) {
	}

	/**
	 * change after update
	 * 
	 * @return the finished game count in a serial
	 */
	public int getSerialProgress() {
		return 0;
	}

	/**
	 * change after update
	 * 
	 * @return the count of test time in all finished games in a serial
	 */
	public int getTotalTestTimeInSerial() {
		return 0;
	}

	/**
	 * change after update
	 * 
	 * @return the count of correct time in all finished games in a serial
	 */
	public int getTotalCorrectTimeInSerial() {
		return 0;
	}

	public int getSerialGameTotal() {
		return mSerialGameTotal;
	}

	public int getSerialGameMaxTotal() {
		return 10;
	}

	public void setSerialGameTotal(int mSerialGameTotal) {
		this.mSerialGameTotal = mSerialGameTotal;
	}

	public int getTestTimeInEachTest() {
		return mTestTimeInEachTest;
	}

	public void setTestTimeInEachTest(int testTimeInEachTest) {
		mTestTimeInEachTest = testTimeInEachTest;
	}

	public int getMaxTestTimeInEachTest() {
		return 10;
	}

	public int getTestDistance() {
		return mTestDistance;
	}

	public void setTestDistance(int testDistance) {
		mTestDistance = testDistance;
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
