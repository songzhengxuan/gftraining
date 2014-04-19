package com.example.gftranning;

public abstract class GameTimer {
	public static interface GameTimerCallback {
		public void onTimer(int tag);
	}
	
	public abstract void setNextTimerEvent(long absTime, int tag);

	public abstract void setNextTimerEventDelayed(long delay, int tag);

	public abstract void setCallback(GameTimerCallback callback);
	
	public abstract void removeTimerEventForTag(int tag);

	public abstract void clearAllPendingTimerEvent();

}
