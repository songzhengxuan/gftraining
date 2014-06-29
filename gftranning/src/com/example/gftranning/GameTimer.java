package com.example.gftranning;

public abstract class GameTimer {
	public static interface GameTimerCallback {
		public void onTimer(int id, int tag);
	}

	public abstract void setNextTimerEvent(long absTime, int id, int tag);

	public abstract void setNextTimerEventDelayed(long delay, int id, int tag);

	public abstract void addCallback(int id, GameTimerCallback callback);

	public abstract void removeTimerEventForGame(int id);

	public abstract void clearAllPendingTimerEvent(int id);

}
