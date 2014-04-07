package com.example.gftranning;

import java.util.ArrayList;

import android.util.Log;

public class Game {
	public static final int TIMER_EVENT_SHOW_START = 0;
	public static final int TIMER_EVENT_SHOW_END = 1;
	public static final int TIMER_EVENT_WAIT_END = 2;

	public static final int STATUS_IDLE = 0;
	public static final int STATUS_INIT_SHOWING = 1;
	public static final int STATUS_SHOWING = 2;
	public static final int STATUS_HIDING = 3;
	private static final String TAG = Game.class.getSimpleName();

	private int mTypeCount;
	private long mDisplayTime;
	private long mWaitTime;
	private int mDistance;
	ArrayList<Integer> mResults = new ArrayList<Integer>();
	ArrayList<Boolean> mInputs = new ArrayList<Boolean>();

	public Game(int typeCount, long displayTime, long waitTime, int distance) {
		this.mTypeCount = typeCount;
		this.mDisplayTime = displayTime;
		this.mWaitTime = waitTime;
		this.mDistance = distance;
	}

	public int getNext() {
		int tmp = generate();
		mResults.add(tmp);
		return tmp;
	}

	public boolean checkInput(boolean input) {
		int size = mResults.size();
		if (size < (mDistance + 2)) {
			return false;
		} else {
			return (mResults.get(size - 1) == mResults
					.get(size - 2 - mDistance)) == input;
		}
	}

	private int generate() {
		return 1;
	}

	public long getmDisplayTime() {
		return mDisplayTime;
	}

	public void setmDisplayTime(long mDisplayTime) {
		this.mDisplayTime = mDisplayTime;
	}

	public long getmWaitTime() {
		return mWaitTime;
	}

	public void setmWaitTime(long mWaitTime) {
		this.mWaitTime = mWaitTime;
	}

	public int getmTypeCount() {
		return mTypeCount;
	}

	public void setmTypeCount(int mTypeCount) {
		this.mTypeCount = mTypeCount;
	}

	public void debugPrint() {
		StringBuilder builder = new StringBuilder();
		int inputCount = mInputs.size();
		int resultCount = mResults.size();
		Log.d(TAG, "inputCount " + inputCount + ", resultCount " + resultCount);
	}

}
