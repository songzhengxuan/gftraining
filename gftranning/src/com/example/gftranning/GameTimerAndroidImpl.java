package com.example.gftranning;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public class GameTimerAndroidImpl extends GameTimer implements Handler.Callback {
	private GameTimerCallback mCallback;

	private Handler mHandler;
	private final Object mLock = new Object();
	private HashMap<Long, ArrayList<Integer>> mTimerEvents = new HashMap<Long, ArrayList<Integer>>();

	private static final int TIMER_MSG = 0;

	@SuppressWarnings("unused")
    private static final String TAG = GameTimerAndroidImpl.class
			.getSimpleName();

	public GameTimerAndroidImpl() {
		mHandler = new Handler(this);
	}

	@Override
	public void setNextTimerEvent(long absTime, int tag) {
		synchronized (mLock) {
			Long key = Long.valueOf(absTime);
			ArrayList<Integer> tags = mTimerEvents.get(key);
			if (tags == null) {
				tags = new ArrayList<Integer>();
				tags.add(Integer.valueOf(tag));
				mTimerEvents.put(key, tags);
				Message msg = mHandler.obtainMessage(TIMER_MSG);
				msg.obj = Long.valueOf(absTime);
				mHandler.sendMessageAtTime(msg, absTime);
			} else {
				if (!tags.contains(Integer.valueOf(tag))) {
					tags.add(Integer.valueOf(tag));
					Message msg = mHandler.obtainMessage(TIMER_MSG);
					msg.obj = Long.valueOf(absTime);
					mHandler.sendMessageAtTime(msg, absTime);
				}
			}
		}
	}

	@Override
	public void setNextTimerEventDelayed(long delay, int tag) {
		long absTime = SystemClock.uptimeMillis() + delay;
		setNextTimerEvent(absTime, tag);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case TIMER_MSG: {
			ArrayList<Integer> tags = null;
			synchronized (mLock) {
				if (msg.obj != null) {
					Long absTime = (Long) msg.obj;
					tags = mTimerEvents.get(absTime);
					mTimerEvents.remove(absTime);
				}
			}
			if (tags != null && mCallback != null) {
				for (Integer aTag : tags) {
					mCallback.onTimer(aTag.intValue());
				}
			}
			return true;
		}
		default:
			break;
		}
		return false;
	}

	@Override
	public void setCallback(GameTimerCallback callback) {
		mCallback = callback;
	}
}
