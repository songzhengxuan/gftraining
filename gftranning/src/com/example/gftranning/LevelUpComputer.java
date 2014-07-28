package com.example.gftranning;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LevelUpComputer {
	private static LevelUpComputer sInstance = new LevelUpComputer();

	public static LevelUpComputer getInstance() {
		return sInstance;
	}

	static final class GameLevel {
		int total;
		int eachGameCount;
		int progress;

		public GameLevel(int total, int eachGameCount, int progress) {
			this.total = total;
			this.eachGameCount = eachGameCount;
			this.progress = progress;
		}
	}

	private GameLevel[] mLevels = new GameLevel[7];
	{
		mLevels[0] = new GameLevel(3, 5, 0);
		mLevels[1] = new GameLevel(4, 8, 0);
		mLevels[2] = new GameLevel(5, 10, 0);
		mLevels[3] = new GameLevel(6, 12, 0);
		mLevels[4] = new GameLevel(7, 15, 0);
		mLevels[5] = new GameLevel(8, 16, 0);
		mLevels[6] = new GameLevel(9, 17, 0);
	}

	private LevelUpComputer() {
	}

	private static final String SP_KEY_CURRNET_LEVEL = "luc_cl";

	public int getCurrentLevel(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getInt(SP_KEY_CURRNET_LEVEL, 0);
	}

	public int getCurrentLevelTotal(Context context) {
		return getLevelTotal(context, getCurrentLevel(context));
	}

	public int getCurrentLevelProgress(Context context) {
		return getLevelProgress(context, getCurrentLevel(context));
	}

	public boolean incCurrentLevelSucceedCount(Context context) {
		return incLevelSucceedCount(context, getCurrentLevel(context));
	}

	public int getMaxLevel(Context context) {
		return 7;
	}

	protected int getLevelTotal(Context context, int level) {
		return 0;
	}

	protected int getLevelProgress(Context context, int level) {
		return 0;
	}

	protected boolean incLevelSucceedCount(Context context, int level) {
		return true;
	}
}
