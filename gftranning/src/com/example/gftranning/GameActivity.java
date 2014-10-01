package com.example.gftranning;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnClickListener {
	private static final String TAG = GameActivity.class.getSimpleName();

	public static final String EXTRA_INT_TEST_COUNT = "count";

	public static final String EXTRA_INT_TEST_DISTANCE = "distance";
	public static final String EXTRA_INT_MODE = "mode";
	public static final String EXTRA_INT_CARRER_CURRNET_LEVEL = "current_level";
	public static final String RESULT_KEY_TEST_TOTAL = "test_total";
	public static final String RESULT_KEY_TEST_CORRECT = "test_correct";
	public static final String RESULT_KEY_TEST_START_TIME_MILLIS = "start_time_millis";
	public static final String RESULT_KEY_TEST_END_TIME_MILLIS = "start_time_millis";
	public static final int MODE_QUICKGAME = 0;
	public static final int MODE_CAREER = 1;
	public static final int MODE_SERIAL_GAME = 2;

	Handler mHandler;

	Animation mReadyScalInAnimation;

	Animation mNumberScleInAnimation;

	Animation mGoScalOutAnimation;

	StartAnimationPlayer mAnimationPlayer;

	TextView mTotalProgress;

	TextView mGraphProgress;

	TextView mAudioProgress;

	View mGamePanel;

	TextView mCenterText;

	ImageView mImageView;

	Game mImageGame;

	int mTotalTestCount;

	int mTestDistance;

	// for test use
	TextView mAnswerText;

	private static final String STATE_KEY_TEST_TOTAL = "test_total";
	private static final String STATE_KEY_TEST_CORRECT = "test_correct";
	private static final String STATE_KEY_TEST_START_TIME_MILLIS = "sk_start_time_millis";
	private ArrayList<Integer> mTestTotalCounts = new ArrayList<Integer>();
	private ArrayList<Integer> mTestCorrectCounts = new ArrayList<Integer>();
	private long mStartTimeMillis;

	private class DebugClass {
		String images = "";

		String audios = "";

		void addImage(String msg) {
			images += msg;
		}

		void addAudio(String msg) {
			audios += msg;
		}

		void clear() {
			images = "";
			audios = "";
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Image:" + images).append("\n");
			builder.append("Audio:" + audios).append("\n");
			return builder.toString();
		}
	}

	private final Runnable mUpdateDebugMsgTask = new Runnable() {

		@Override
		public void run() {
			mAnswerText.setText(mDebugMsg.toString());
		}
	};

	private final Runnable mShowResultTask = new Runnable() {

		@Override
		public void run() {
		}
	};

	private final DebugClass mDebugMsg = new DebugClass();

	IGameUI mGraphUI = new IGameUI() {

		@Override
		public void onSucceed(int lastResult) {
			mImageView.setBackgroundColor(Color.GREEN);
			mDebugMsg.addImage("[V]");
			mHandler.post(mUpdateDebugMsgTask);
		}

		@Override
		public void onGameEnd() {
			mDebugMsg.addImage("end" + Arrays.toString(mImageGame.getCorrectRatio()));
			mHandler.post(mUpdateDebugMsgTask);
			mProgressBar.setProgress(mTotalTestCount);
			GameActivity.this.onGameEnd(1);
		}

		@Override
		public void onError(int excepted, int errorResult, boolean isTimeout) {
			mImageMatchButton.startAnimation(mShakeAnimation);
			if (isTimeout) {
				mDebugMsg.addImage("[M]");
			} else {
				mDebugMsg.addImage("[X]");
			}
			mHandler.post(mUpdateDebugMsgTask);
		}

		@Override
		public void onAlreadyMarked() {
			Toast.makeText(getApplicationContext(), "Already", Toast.LENGTH_SHORT).show();
			mImageView.setBackgroundColor(Color.RED);
			mImageMatchButton.startAnimation(mShakeAnimation);
		}

		@Override
		public void hide() {
			mImageView.setVisibility(View.INVISIBLE);
		}

		@Override
		public void display(int result, boolean isPreparaing) {
			if (result < 0 || result > 8) {
				throw new IllegalArgumentException("invalid result id");
			}
			int id = getResources()
					.getIdentifier("image" + (result + 1), "drawable", getBaseContext().getPackageName());
			if (id == 0) {
				throw new IllegalStateException("invalid state");
			}
			mImageView.setImageResource(id);

			if (mImageMatchButton.isEnabled() == isPreparaing) {
				mImageMatchButton.setEnabled(!isPreparaing);
			}
			mTotalProgressContent = mImageGame.getInputSize() + " of total " + mTotalTestCount;
			mTotalProgress.setText(mTotalProgressContent);
			mProgressBar.setProgress(mImageGame.getInputSize());
			mImageView.setVisibility(View.VISIBLE);
		}
	};

	private final IGameUI mAudioGameUI = new IGameUI() {

		@Override
		public void display(int result, boolean isPreparaing) {
			String last = mAudioTestText.getText().toString();
			last += TextUtils.isEmpty(last) ? "" : "," + result;
			mAudioTestText.setText(last);
			mSoundPool.play(mSoundIds.get(result), 1.0f, 1.0f, 1, 0, 1.0f);
			mAudioTestText.setVisibility(View.VISIBLE);
			mAudioMatchButton.setEnabled(!isPreparaing);
		}

		@Override
		public void hide() {
			mAudioTestText.setVisibility(View.GONE);
		}

		@Override
		public void onSucceed(int lastResult) {
			mDebugMsg.addAudio("[V]");
			mHandler.post(mUpdateDebugMsgTask);
		}

		@Override
		public void onError(int excepted, int errorResult, boolean isTimeOut) {
			if (isTimeOut) {
				mDebugMsg.addAudio("[M]");
			} else {
				mDebugMsg.addAudio("[X]");
			}
			mAudioMatchButton.startAnimation(mShakeAnimation);
			mHandler.post(mUpdateDebugMsgTask);
		}

		@Override
		public void onAlreadyMarked() {
			Toast.makeText(getApplicationContext(), "Already", Toast.LENGTH_SHORT).show();
			mAudioTestText.setBackgroundColor(Color.RED);
			mAudioMatchButton.startAnimation(mShakeAnimation);
		}

		@Override
		public void onGameEnd() {
			mDebugMsg.addAudio("end" + Arrays.toString(mAudioGame.getCorrectRatio()));
			mHandler.post(mUpdateDebugMsgTask);
			GameActivity.this.onGameEnd(2);
		}
	};
	private ProgressBar mProgressBar;

	private GameTimer mGameTimer;

	private ISequenceSource mImageSeq;

	private View mImageMatchButton;

	private View mAudioMatchButton;

	private Animation mShakeAnimation;

	private String mTotalProgressContent;

	private TextView mAudioTestText;

	private ISequenceSource mAudioSeq;

	private Game mAudioGame;

	private SoundPool mSoundPool;

	private ArrayList<Integer> mSoundIds;

	private int mMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mAnswerText = (TextView) findViewById(R.id.answer_text);

		mHandler = new Handler();

		mGamePanel = findViewById(R.id.game_panel);
		mCenterText = (TextView) findViewById(R.id.center_text);
		mTotalProgress = (TextView) findViewById(R.id.total_progress_text);
		mGraphProgress = (TextView) findViewById(R.id.progress_text_graph);
		mAudioProgress = (TextView) findViewById(R.id.progress_text_audio);
		mImageMatchButton = findViewById(R.id.image_matched);
		mAudioMatchButton = findViewById(R.id.audio_matched);
		mImageMatchButton.setOnClickListener(this);
		mAudioMatchButton.setOnClickListener(this);
		mImageView = (ImageView) findViewById(R.id.image);
		mAudioTestText = (TextView) findViewById(R.id.test_audio);

		init();
		File testFlagFile = new File(Environment.getExternalStorageDirectory(), "test");
		if (testFlagFile.isFile()) {
			initFakeSequence();
		}
		initGame();

		mStartTimeMillis = System.currentTimeMillis();
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(STATE_KEY_TEST_TOTAL)) {
				mTestTotalCounts = savedInstanceState.getIntegerArrayList(STATE_KEY_TEST_TOTAL);
			}
			if (savedInstanceState.containsKey(STATE_KEY_TEST_CORRECT)) {
				mTestCorrectCounts = savedInstanceState.getIntegerArrayList(STATE_KEY_TEST_CORRECT);
			}
			if (savedInstanceState.containsKey(STATE_KEY_TEST_START_TIME_MILLIS)) {
				mStartTimeMillis = savedInstanceState.getLong(STATE_KEY_TEST_START_TIME_MILLIS);
			}
		}
	}

	void init() {
		mShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
		mAnimationPlayer = new StartAnimationPlayer();
		mAnimationPlayer.initAnimations(this);
		mAnimationPlayer.play();
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		mSoundIds = new ArrayList<Integer>();
		int id = mSoundPool.load(getApplicationContext(), R.raw.ant, 1);
		mSoundIds.add(id);
		id = mSoundPool.load(getApplicationContext(), R.raw.bee, 1);
		mSoundIds.add(id);
		id = mSoundPool.load(getApplicationContext(), R.raw.cow, 1);
		mSoundIds.add(id);
		id = mSoundPool.load(getApplicationContext(), R.raw.dog, 1);
		mSoundIds.add(id);
		id = mSoundPool.load(getApplicationContext(), R.raw.eel, 1);
		mSoundIds.add(id);
		id = mSoundPool.load(getApplicationContext(), R.raw.fish, 1);
		mSoundIds.add(id);
		id = mSoundPool.load(getApplicationContext(), R.raw.goose, 1);
		mSoundIds.add(id);
	}

	void initGame() {
		mMode = getIntent().getIntExtra(EXTRA_INT_MODE, MODE_QUICKGAME);
		if (mMode == MODE_QUICKGAME) {
			mTotalTestCount = getIntent().getIntExtra(EXTRA_INT_TEST_COUNT, 5);
			mTestDistance = getIntent().getIntExtra(EXTRA_INT_TEST_DISTANCE, 1) - 1;
		} else if (mMode == MODE_CAREER) {
			int currentLevel = getIntent().getIntExtra(EXTRA_INT_CARRER_CURRNET_LEVEL, 0);
			mTotalTestCount = LevelUpComputer.getInstance().getEachGameTestCount(this, currentLevel);
			mTestDistance = currentLevel;
		} else if (mMode == MODE_SERIAL_GAME) {
			mTotalTestCount = getIntent().getIntExtra(EXTRA_INT_TEST_COUNT, 5);
			mTestDistance = getIntent().getIntExtra(EXTRA_INT_TEST_DISTANCE, 1) - 1;
		}
		mGameTimer = new GameTimerAndroidImpl();
		if (mImageSeq == null) {
			RandomSequence.Builder builder = new RandomSequence.Builder().setRange(8).setRepeatDistance(mTestDistance)
					.setRepeatRatio(0.3);
			mImageSeq = builder.build();
		}

		mImageGame = new Game(300, 2500, mTestDistance, mGameTimer, mImageSeq, mGraphUI);
		mImageGame.setId(1);
		mImageGame.setTestTimes(mTotalTestCount);

		if (mAudioSeq == null) {
			RandomSequence.Builder builder2 = new RandomSequence.Builder().setRange(7).setRepeatDistance(mTestDistance)
					.setRepeatRatio(0.3);
			mAudioSeq = builder2.build();
		}
		mAudioGame = new Game(300, 2500, mTestDistance, mGameTimer, mAudioSeq, mAudioGameUI);
		mAudioGame.setId(2);
		mAudioGame.setTestTimes(mTotalTestCount);

		mTotalProgressContent = "total " + mTotalTestCount;
		mTotalProgress.setText(mTotalProgressContent);
		mProgressBar.setMax(mTotalTestCount);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putIntegerArrayList(STATE_KEY_TEST_TOTAL, mTestTotalCounts);
		outState.putIntegerArrayList(STATE_KEY_TEST_CORRECT, mTestCorrectCounts);
	}

	@SuppressWarnings("unused")
	private void initFakeSequence() {
		mImageSeq = new ISequenceSource() {
			int[] seqs = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };

			int pos = 0;
			int replayStart = -1;

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
		mAudioSeq = new ISequenceSource() {
			int[] seqs = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, };

			int pos = 0;
			int replayStart = -1;

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
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAnimationPlayer.isPlaying()) {
			mAnimationPlayer.cancel();
		}
		mImageGame.stop();
		mAudioGame.stop();
	}

	private class StartAnimationPlayer implements AnimationListener {
		Animation playingAnimation;

		int number;

		boolean canceled;

		void play() {
			mCenterText.setText(R.string.ready);
			mCenterText.startAnimation(mReadyScalInAnimation);
		}

		void cancel() {
			canceled = true;
			if (playingAnimation == null) {
				return;
			}
			if (mCenterText.getAnimation() == playingAnimation) {
				playingAnimation.cancel();
			}
		}

		boolean isPlaying() {
			return playingAnimation == null;
		}

		@Override
		public void onAnimationEnd(Animation anim) {
			playingAnimation = null;
			if (GameActivity.this.isFinishing() || canceled) {
				return;
			}
			if (anim == mReadyScalInAnimation) {
				number = 3;
				mCenterText.setText("" + number);
				mCenterText.startAnimation(mNumberScleInAnimation);
			} else if (anim == mNumberScleInAnimation) {
				if (number > 1) {
					number--;
					mCenterText.setText("" + number);
					mCenterText.startAnimation(mNumberScleInAnimation);
				} else {
					mCenterText.setText("Go!");
					mCenterText.startAnimation(mGoScalOutAnimation);
				}
			} else if (anim == mGoScalOutAnimation) {
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						GameActivity.this.startGame();
					}
				}, 1000);
			}
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
			// do nothing
		}

		@Override
		public void onAnimationStart(Animation anim) {
			playingAnimation = anim;
		}

		void initAnimations(GameActivity testGameActivity2) {
			testGameActivity2.mReadyScalInAnimation = AnimationUtils.loadAnimation(testGameActivity2,
					R.anim.ready_scale_in);

			testGameActivity2.mReadyScalInAnimation.setAnimationListener(this);

			testGameActivity2.mNumberScleInAnimation = AnimationUtils.loadAnimation(testGameActivity2,
					R.anim.number_scale_in);
			testGameActivity2.mNumberScleInAnimation.setAnimationListener(this);

			testGameActivity2.mGoScalOutAnimation = AnimationUtils
					.loadAnimation(testGameActivity2, R.anim.go_scale_out);
			testGameActivity2.mGoScalOutAnimation.setAnimationListener(this);

		}
	}

	public void startGame() {
		mAudioGame.start();
		mImageGame.start();
	}

	@Override
	public void onClick(View view) {
		if (view == mImageMatchButton) {
			if (mImageGame != null) {
				mImageGame.checkAndRememberUserInput(true);
			}
		} else if (view == mAudioMatchButton) {
			if (mAudioGame != null) {
				mAudioGame.checkAndRememberUserInput(true);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "onKeyDown");
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			Log.d(TAG, "onKeyDown left ");
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			Log.d(TAG, "onKeyDown right");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * notifiy game end
	 * 
	 * @param type
	 *            1 for image, 2 for graph
	 */
	void onGameEnd(int type) {
		mEndedGameFlag = (mEndedGameFlag | type);
		if (mEndedGameFlag == 3) {
			int[] a = mImageGame.getCorrectRatio();
			int[] b = mAudioGame.getCorrectRatio();
			if (a[0] == a[1] && b[0] == b[1]) {
				Toast.makeText(this, "good", Toast.LENGTH_LONG).show();
			}
			mTestTotalCounts.add(a[1] + b[1]);
			mTestCorrectCounts.add(a[0] + b[0]);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "current summary:");
				Log.d(TAG, "mTestTotalCounts " + mTestTotalCounts.toString());
				Log.d(TAG, "mTestCor" + mTestCorrectCounts.toString());
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (mEndedGameFlag == 3 && mMode == MODE_SERIAL_GAME) {
			Intent intent = new Intent();
			intent.putExtra(RESULT_KEY_TEST_TOTAL, mTestTotalCounts);
			intent.putExtra(RESULT_KEY_TEST_CORRECT, mTestCorrectCounts);
			setResult(RESULT_OK, intent);
			finish();
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	private int mEndedGameFlag = 0;
}
