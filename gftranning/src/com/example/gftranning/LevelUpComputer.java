
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
        final int total;

        final int eachGameCount;

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

    private static final String SP_KEY_LEVEL_PROGRESS = "luc_lp";

    public void init(Context context) {
        int currentLevel = getCurrentLevel(context);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        mLevels[currentLevel].progress = sp.getInt(SP_KEY_LEVEL_PROGRESS + currentLevel, 0);
    }

    public int getCurrentLevel(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(SP_KEY_CURRNET_LEVEL, 0);
    }

    public boolean goToNextLevel(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int currentLevel = sp.getInt(SP_KEY_CURRNET_LEVEL, 0);
        if (currentLevel + 1 < mLevels.length) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(SP_KEY_CURRNET_LEVEL, currentLevel + 1);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

    public int getCurrentLevelTotal(Context context) {
        return getLevelTotalGameCount(context, getCurrentLevel(context));
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

    public int getLevelTotalGameCount(Context context, int level) {
        return mLevels[level].total;
    }
 
    public int getEachGameTestCount(Context context, int level) {
    	return mLevels[level].eachGameCount;
    }

    public int getLevelProgress(Context context, int level) {
        return mLevels[level].progress;
    }

    public boolean incLevelSucceedCount(Context context, int level) {
        mLevels[level].progress += 1;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(SP_KEY_LEVEL_PROGRESS + "level", mLevels[level].progress);
        editor.commit();

        boolean levelup = mLevels[level].progress >= mLevels[level].total;
        return levelup;
    }
}
