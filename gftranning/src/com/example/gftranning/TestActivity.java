package com.example.gftranning;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.gftranning.GameTimer.GameTimerCallback;

public class TestActivity extends Activity {
	protected static final String TAG = TestActivity.class.getSimpleName();
	GameTimerAndroidImpl mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TextView text = new TextView(this);
		text.setText("Hello test");
		setContentView(text);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "start test");
		}

		mTimer = new GameTimerAndroidImpl();
		mTimer.setCallback(new GameTimerCallback() {

			@Override
			public void onTimer(int tag) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "onTimer " + System.currentTimeMillis()
							+ " for tag: " + tag);
				}
			}
		});

		mTimer.setNextTimerEventDelayed(500, 1);
		mTimer.setNextTimerEventDelayed(3000, 2);

	}

}
