package com.example.gftranning;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity implements View.OnClickListener {
	private final class IGameUIImplementation implements IGameUI {
		private TextView textView;

		public IGameUIImplementation(TextView text) {
			textView = text;
		}

		@Override
		public void display(int result, boolean isPreparaing) {
			String current = (String) textView.getText();
			current = current + " ," + result;
			textView.setText(current);
			textView.setEnabled(true);
			if (mButton1.isEnabled() != (!isPreparaing)) {
				mButton1.setEnabled(!isPreparaing);
			}
		}

		@Override
		public void hide() {
			textView.setEnabled(false);
		}

		@Override
		public void onSucceed(int lastResult) {
			String current = (String) textView.getText();
			current = current + "=";
			textView.setText(current);
		}

		@Override
		public void onError(int excepted, int errorResult) {
			String current = (String) textView.getText();
			current = current + "X";
			textView.setText(current);
		}

		@Override
		public void onAlreadyMarked() {
			Toast.makeText(TestActivity.this, "already", 500).show();
		}

		@Override
		public void onGameEnd() {
			String current = (String) textView.getText();
			current = current + "  GAME END";
			textView.setText(current);
		}
	}

	protected static final String TAG = TestActivity.class.getSimpleName();
	GameTimerAndroidImpl mTimer;
	TextView mText1;
	Button mButton1;
	Button mRestartButton1;
	Game mGame1;
	IGameUI mGameUI1;

	TextView mText2;
	Button mButton2;
	Button mRestartButton2;
	Game mGame2;
	IGameUI mGameUI2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		int distance = 2;
		mText1 = (TextView) findViewById(R.id.text1);
		mButton1 = (Button) findViewById(R.id.button1);
		mRestartButton1 = (Button) findViewById(R.id.restart1);
		mButton1.setOnClickListener(this);
		mRestartButton1.setOnClickListener(this);
		mGameUI1 = new IGameUIImplementation(mText1);

		mText2 = (TextView) findViewById(R.id.text2);
		mButton2 = (Button) findViewById(R.id.button2);
		mRestartButton2 = (Button) findViewById(R.id.restart2);
		mGameUI2 = new IGameUIImplementation(mText2);
		mButton2.setOnClickListener(this);
		mRestartButton2.setOnClickListener(this);
		ISequenceSource seq1 = new RandomSequence.Builder().setRange(8)
				.setRepeatDistance(distance).setRepeatRatio(0.5).build();

		mTimer = new GameTimerAndroidImpl();

		mGame1 = new Game(300, 2500, distance, mTimer, seq1, mGameUI1);
		mGame1.setId(1);
		mGame1.setTestTimes(20);
		mGame1.start();

		ISequenceSource fixedSeq = new ISequenceSource() {
			int[] seqs = new int[] { 0, 1, 2, 3, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1,
					1, 1, 1, };
			int pos = 0;
			private int replayStart = -1;

			@Override
			public int getNext() {
				return seqs[pos++];
			}

			@Override
			public void replay() {
				if (replayStart >= 0) {
					pos = replayStart;
				}
				replayStart = -1;
			}

			@Override
			public void record() {
				replayStart = pos;
			}
		};
		mGame2 = new Game(300, 2500, distance, mTimer, fixedSeq, mGameUI2);
		mGame2.setId(2);
		mGame2.setTestTimes(5);
		mGame2.start();

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.button1:
			mGame1.checkAndRememberUserInput(true);
			break;
		case R.id.restart1:
			mGame1.restart();
			break;
		case R.id.button2:
			mGame2.checkAndRememberUserInput(true);
			break;
		case R.id.restart2:
			mGame2.restart();
			break;
		}
	}
}
