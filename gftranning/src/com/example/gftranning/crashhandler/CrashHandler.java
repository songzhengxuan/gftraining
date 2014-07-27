
package com.example.gftranning.crashhandler;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {
    private Application mApp;

    public CrashHandler(Application app) {
        this.mApp = app;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        String stackTrace = Log.getStackTraceString(ex);
        Log.e("hello", "stackTrace:" + stackTrace);
        Toast.makeText(mApp, stackTrace, Toast.LENGTH_LONG).show();
        try {
            Thread.sleep(Toast.LENGTH_LONG);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent intent = new Intent(mApp.getApplicationContext(), CrashInfoActivity.class);
        intent.putExtra(CrashInfoActivity.EXTRA_KEY_STRING_STACKTRACE, stackTrace);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mApp.getApplicationContext().startActivity(intent);
        System.exit(1);
    }

}
