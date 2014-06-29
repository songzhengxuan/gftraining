package com.example.gftranning;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public class GameTimerAndroidImpl extends GameTimer implements Handler.Callback {

	private Handler mHandler;
	private final Object mLock = new Object();
	// absolute time to event tag, one time point corresponding an array of
	// TimerEvent to invoke callback method
	private HashMap<Long, ArrayList<TimerEvent>> mTimerEvents = new HashMap<Long, ArrayList<TimerEvent>>();

	// each game id can only schedule one timer event
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, TimerEvent> mGameIdToTimerEventMap = new HashMap<Integer, TimerEvent>();

	// all the timer callback will receive the same TimerEvent when the event's
	// time is coming
	private ArrayList<GameTimerCallback> mCallbacks = new ArrayList<GameTimerCallback>();

	private final static class TimerEvent {
		private final int id;
		private final int tag;
		private final long absTime;

		TimerEvent(int id, int tag, long absTime) {
			this.id = id;
			this.tag = tag;
			this.absTime = absTime;
		}

		static final TimerEvent createFor(int id, int tag, long absTime) {
			return new TimerEvent(id, tag, absTime);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof TimerEvent)) {
				return false;
			}
			final TimerEvent o = (TimerEvent) obj;
			return id == o.id && tag == o.tag && absTime == o.absTime;
		}

		@Override
		public int hashCode() {
			int result = 0;
			result = 31 * result + id;
			result = 31 * result + tag;
			result = 31 * result + (int) absTime;
			return result;
		}
	}

	private static final int TIMER_MSG = 0;

	@SuppressWarnings("unused")
	private static final String TAG = GameTimerAndroidImpl.class
			.getSimpleName();

	public GameTimerAndroidImpl() {
		mHandler = new Handler(this);
	}

	@Override
	public void setNextTimerEvent(long absTime, int id, int tag) {
		synchronized (mLock) {
			Long key = Long.valueOf(absTime);
			ArrayList<TimerEvent> tags = mTimerEvents.get(key);
			TimerEvent event = TimerEvent.createFor(id, tag, absTime);
			if (tags == null) {
				tags = new ArrayList<TimerEvent>();
				tags.add(event);
				mTimerEvents.put(key, tags);
				mGameIdToTimerEventMap.put(Integer.valueOf(id), event);
				Message msg = mHandler.obtainMessage(TIMER_MSG);
				msg.obj = Long.valueOf(absTime);
				mHandler.sendMessageAtTime(msg, absTime);
			} else {
				if (!tags.contains(event)) {
					tags.add(event);
				}
				mGameIdToTimerEventMap.put(Integer.valueOf(id), event);
			}
		}
	}

	@Override
	public void setNextTimerEventDelayed(long delay, int id, int tag) {
		long absTime = SystemClock.uptimeMillis() + delay;
		setNextTimerEvent(absTime, id, tag);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case TIMER_MSG: {
			// keep the timer event need to notify
			ArrayList<TimerEvent> tags = null;
			Long absTime = (Long) msg.obj;
			synchronized (mLock) {
				if (msg.obj != null) {
					tags = mTimerEvents.get(absTime);
					mTimerEvents.remove(absTime);
					if (tags != null) {
						// for each game id, check its current next timer event
						// is valid
						for (TimerEvent aTag : tags) {
							if (absTime.longValue() != mGameIdToTimerEventMap
									.get(aTag.id).absTime) {
								tags.remove(aTag);
							} else {
								mGameIdToTimerEventMap.remove(aTag);
							}
						}
					}
				}
			}
			if (tags != null && mCallbacks != null) {
				synchronized (mCallbacks) {
					for (GameTimerCallback callback : mCallbacks) {
						for (TimerEvent aTag : tags) {
							callback.onTimer(aTag.id, aTag.tag);
						}
					}
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
	public void addCallback(int tag, GameTimerCallback callback) {
		synchronized (mCallbacks) {
			mCallbacks.add(callback);
		}
	}

	@Override
	public void clearAllPendingTimerEvent(int id) {
		synchronized (mLock) {
			mGameIdToTimerEventMap.remove(Integer.valueOf(id));
		}
	}

	@Override
	public void removeTimerEventForGame(int id) {
		synchronized (mLock) {
			mGameIdToTimerEventMap.remove(Integer.valueOf(id));
		}
	}
}
