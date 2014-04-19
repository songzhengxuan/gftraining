package com.example.gftranning;

import java.util.ArrayList;

import android.util.Log;

public class Game implements GameTimer.GameTimerCallback {
	public static final int TIMER_EVENT_SHOW_START = 0;
	public static final int TIMER_EVENT_SHOW_END = 1;
	public static final int TIMER_EVENT_WAIT_END = 2;

	public static final int STATUS_IDLE = 0;
	public static final int STATUS_INIT_SHOWING = 1;
	public static final int STATUS_SHOWING = 2;
	public static final int STATUS_HIDING = 3;
	public static final int STATUS_END = 4;
	private static final String TAG = Game.class.getSimpleName();

	private int mStatus = STATUS_IDLE;
	private int mTypeCount;
	private long mDisplayTime;
	private long mWaitTime;
	private int mDistance;
	private GameTimer mTimer;
	private ISequenceSource mGenerator;
	private IGameUI mGameUI;
	private int mCount;
	ArrayList<Integer> mResults = new ArrayList<Integer>();
	ArrayList<Boolean> mInputs = new ArrayList<Boolean>();

	static enum ResultAndInputStatus {
		ReadyForNewResult, ReadyForNewInput
	}

	public Game(int typeCount, long displayTime, long waitTime, int distance,
			GameTimer timer, ISequenceSource generator, IGameUI gameUI) {
		this.mTypeCount = typeCount;
		this.mDisplayTime = displayTime;
		this.mWaitTime = waitTime;
		this.mDistance = distance;
		this.mStatus = STATUS_IDLE;
		this.mTimer = timer;
		this.mTimer.setCallback(this);
		this.mGenerator = generator;
		this.mGameUI = gameUI;
	}

	public void start() {
		if (mStatus != STATUS_IDLE) {
			throw new IllegalStateException("start error");
		}
		mStatus = STATUS_INIT_SHOWING;
		onTimer(TIMER_EVENT_SHOW_START);
		mTimer.setNextTimerEvent(mDisplayTime, TIMER_EVENT_SHOW_END);
	}

	public void restart() {
		mGenerator.reset();
		mInputs.clear();
		mResults.clear();
		mStatus = STATUS_IDLE;
		mTimer.clearAllPendingTimerEvent();
		start();
	}

	public int getStatus() {
		return mStatus;
	}

	private int generateNext() {
		return mGenerator.getNext();
	}

	public void setNextResultForTest(int result) {
		if (BuildConfig.DEBUG) {
			setNextResult(result);
		}
	}

	private void setNextResult(int result) {
		mResults.add(result);
	}

	public int getNeedCheckResultCount() {
		int result = mResults.size() - 1 - mDistance;
		result = result < 0 ? 0 : result;
		return result;
	}

	/**
	 * @param input
	 * @return true if user input is correct
	 */
	private boolean checkInput(boolean input) {
		int size = mResults.size();
		if (size < (mDistance + 2)) {
			return false;
		} else {
			return (mResults.get(size - 1) == mResults
					.get(size - 2 - mDistance)) == input;
		}
	}

	private void rememberInput(boolean input) {
		checkNumberForNewInput();
		/*
		 * switch (mStatus) { case STATUS_IDLE: case STATUS_INIT_SHOWING: if
		 * (BuildConfig.DEBUG) { throw new
		 * IllegalStateException("invalid state"); } break; case STATUS_SHOWING:
		 * case STATUS_HIDING: mInputs.add(Boolean.valueOf(input)); break;
		 * default: break; }
		 */
		mInputs.add(input);
	}

	public boolean checkAndRememberUserInput(boolean input) {
		if (currentResultIsAlreadyMarked()) {
			mGameUI.onAlreadyMarked();
			return false;
		}
		boolean ret = checkInput(input);
		if (ret) {
			mGameUI.onSucceed(mResults.get(mResults.size() - 1));
		} else {
			mGameUI.onError(mResults.get(mResults.size() - 1),
					mResults.get(mResults.size() - 2 - mDistance));
		}
		rememberInput(input);
		if (mInputs.size() == mCount) {
			mTimer.clearAllPendingTimerEvent();
			mStatus = STATUS_END;
			mGameUI.onGameEnd();
		} else {
			mTimer.removeTimerEventForTag(TIMER_EVENT_WAIT_END);
			mTimer.setNextTimerEventDelayed(300, TIMER_EVENT_SHOW_START);
		}
		return ret;
	}

	private boolean currentResultIsAlreadyMarked() {
		return getResultAndInputMatchStatus() == ResultAndInputStatus.ReadyForNewResult;
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

	private ResultAndInputStatus getResultAndInputMatchStatus() {
		if ((mInputs.size() + mDistance + 2) == mResults.size()) {
			return ResultAndInputStatus.ReadyForNewInput;
		} else if ((mInputs.size() + mDistance + 1) == mResults.size()) {
			return ResultAndInputStatus.ReadyForNewResult;
		} else {
			throw new IllegalStateException("results " + mResults.size()
					+ " and input " + mInputs.size() + " number mismatch");
		}
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

	/**
	 * set the total number of checks before the end of game
	 * 
	 * @param count
	 *            if is 0, then is endless game
	 */
	public void setTestTimes(int count) {
		this.mCount = count;
	}

	public void debugPrint() {
		int inputCount = mInputs.size();
		int resultCount = mResults.size();
		Log.d(TAG, "inputCount " + inputCount + ", resultCount " + resultCount);
	}

	@Override
	public void onTimer(int tag) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onTimer " + tag);
		}
		switch (tag) {
		case TIMER_EVENT_SHOW_START:
			setNextResult(generateNext());
			if (mResults.size() <= 1 + mDistance) {
				mStatus = STATUS_INIT_SHOWING;
				mGameUI.display(mResults.get(mResults.size() - 1), true);
			} else {
				if (mStatus == STATUS_INIT_SHOWING) {
					mStatus = STATUS_SHOWING;
				}
				mGameUI.display(mResults.get(mResults.size() - 1), false);
			}
			mTimer.setNextTimerEventDelayed(mDisplayTime, TIMER_EVENT_SHOW_END);
			break;
		case TIMER_EVENT_SHOW_END:
			if (mStatus == STATUS_INIT_SHOWING) {
				// do nothing
			} else {
				mStatus = STATUS_HIDING;
			}
			mGameUI.hide();
			mTimer.setNextTimerEventDelayed(mWaitTime, TIMER_EVENT_WAIT_END);
			break;
		case TIMER_EVENT_WAIT_END:
			if (mStatus == STATUS_INIT_SHOWING) {
				mTimer.setNextTimerEvent(0, TIMER_EVENT_SHOW_START);
			} else {
				ResultAndInputStatus checkResult = getResultAndInputMatchStatus();
				if (checkResult == ResultAndInputStatus.ReadyForNewInput) {
					checkAndRememberUserInput(false);
				} else {
					throw new IllegalStateException(
							"should not receive TIMER_EVENT_WAIT_END when ReadyForNewResult");
				}
			}
			break;
		default:
			break;
		}
	}
}
