
package com.example.gftranning;

import com.example.gftrainning.utils.Utils;
import com.example.gftranning.crashhandler.CrashHandler;
import com.example.gftranning.levelup.LevelUpComputer;

import android.app.Application;

public class GFTApplication extends Application {
    private CrashHandler mCrashHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        if (getPackageName().equals(Utils.getCurrentProcessName())) {
            mCrashHandler = new CrashHandler(this);
            Thread.setDefaultUncaughtExceptionHandler(mCrashHandler);
        }
        LevelUpComputer.getInstance().init(getApplicationContext());
    }

}
