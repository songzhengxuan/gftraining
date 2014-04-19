package com.example.gftranning;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity implements IGameUI {
	protected static final String TAG = TestActivity.class.getSimpleName();
	GameTimerAndroidImpl mTimer;
	TextView mText;
	Button mButton;
	Button mRestartButton;
	Game mGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		mText = (TextView) findViewById(R.id.text);
		mButton = (Button) findViewById(R.id.button);
		mRestartButton = (Button) findViewById(R.id.restart);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "start test");
		}

		mTimer = new GameTimerAndroidImpl();
		ISequenceSource seq = new ISequenceSource() {
			int[] seqs = new int[] { 0, 1, 2, 3, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1,
					1, 1, 1, };
			int pos = 0;

			@Override
			public int getNext() {
				return seqs[pos++];
			}

			@Override
			public void reset() {
				pos = 0;
			}
		};
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mGame.checkAndRememberUserInput(true);
			}
		});
		mRestartButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mGame.restart();
			}
		});
		mGame = new Game(3, 2000, 5000, 1, mTimer, seq, this);
		mGame.setTestTimes(7);
		mGame.start();
	}

	@Override
	public void display(int result, boolean isPreparaing) {
		String current = (String) mText.getText();
		current = current + " ," + result;
		mText.setText(current);
		mText.setEnabled(true);
		if (mButton.isEnabled() != (!isPreparaing)) {
			mButton.setEnabled(!isPreparaing);
		}
	}

	@Override
	public void hide() {
		mText.setEnabled(false);
	}

	@Override
	public void onSucceed(int lastResult) {
		String current = (String) mText.getText();
		current = current + "=";
		mText.setText(current);
	}

	@Override
	public void onError(int excepted, int errorResult) {
		String current = (String) mText.getText();
		current = current + "X";
		mText.setText(current);
	}

	@Override
	public void onAlreadyMarked() {
		Toast.makeText(this, "already", 500).show();
	}

	@Override
	public void onGameEnd() {
		String current = (String) mText.getText();
		current = current + "  GAME END";
		mText.setText(current);
	}

}
