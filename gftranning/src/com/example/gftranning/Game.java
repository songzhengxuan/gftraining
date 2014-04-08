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

    private int mStatus = STATUS_IDLE;
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
        this.mStatus = STATUS_IDLE;
    }

    public int generateNext() {
        return 1;
    }

    public void setNextResults(int result) {
        mResults.add(result);
        if (mResults.size() <= mDistance + 1) {
        	mStatus = STATUS_INIT_SHOWING;
        } else {
        	mStatus = STATUS_SHOWING;
        }
    }
    
    public int getNeedCheckResultCount() {
    	int result = mResults.size() - 1 - mDistance;
    	result = result < 0 ? 0 : result;
    	return result;
    }

    public boolean checkInput(boolean input) {
        checkNumberForNewInput();
        int size = mResults.size();
        if (size < (mDistance + 2)) {
            return false;
        } else {
            return (mResults.get(size - 1) == mResults.get(size - 2 - mDistance)) == input;
        }
    }

    public void rememberInput(boolean input) {
        checkNumberForNewInput();
        switch (mStatus) {
        case STATUS_IDLE:
        case STATUS_INIT_SHOWING:
            if (BuildConfig.DEBUG) {
                throw new IllegalStateException("invalid state");
            }
            break;
        case STATUS_SHOWING:
        case STATUS_HIDING:
            mInputs.add(Boolean.valueOf(input));
            break;
        default:
            break;
        }
        mInputs.add(input);
    }

    public boolean checkAndRememberUserInput(boolean input) {
        boolean ret = checkInput(input);
        rememberInput(input);
        return ret;
    }

    private boolean checkNumberForNewInput() {
        boolean result = (mInputs.size() + mDistance + 2) == mResults.size();
        if (BuildConfig.DEBUG) {
            if (!result) {
                throw new IllegalStateException("number error");
            }
        }
        return result;
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
        int inputCount = mInputs.size();
        int resultCount = mResults.size();
        Log.d(TAG, "inputCount " + inputCount + ", resultCount " + resultCount);
    }
}
