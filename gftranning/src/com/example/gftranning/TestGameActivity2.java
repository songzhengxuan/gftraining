package com.example.gftranning;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
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

public class TestGameActivity2 extends Activity implements OnClickListener {
	private static final String TAG = null;

	public static final String EXTRA_INT_TEST_COUNT = "count";

	public static final String EXTRA_INT_TEST_DISTANCE = "distance";

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
			mDebugMsg.addImage("end"
					+ Arrays.toString(mImageGame.getCorrectRatio()));
			mHandler.post(mUpdateDebugMsgTask);
			mProgressBar.setProgress(mTotalTestCount);
		}

		@Override
		public void onError(int excepted, int errorResult) {
			mImageView.setBackgroundColor(Color.RED);
			mImageMatchButton.startAnimation(mShakeAnimation);
			mDebugMsg.addImage("[X]");
			mHandler.post(mUpdateDebugMsgTask);
		}

		@Override
		public void onAlreadyMarked() {
			Toast.makeText(getApplicationContext(), "Already",
					Toast.LENGTH_SHORT).show();
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
			int id = getResources().getIdentifier("image" + (result + 1),
					"drawable", getBaseContext().getPackageName());
			if (id == 0) {
				throw new IllegalStateException("invalid state");
			}
			mImageView.setImageResource(id);

			if (mImageMatchButton.isEnabled() == isPreparaing) {
				mImageMatchButton.setEnabled(!isPreparaing);
			}
			mTotalProgressContent = mImageGame.getInputSize() + " of total "
					+ mTotalTestCount;
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
		public void onError(int excepted, int errorResult) {
			mDebugMsg.addAudio("[X]");
			mAudioMatchButton.startAnimation(mShakeAnimation);
			mHandler.post(mUpdateDebugMsgTask);
		}

		@Override
		public void onAlreadyMarked() {
			Toast.makeText(getApplicationContext(), "Already",
					Toast.LENGTH_SHORT).show();
			mAudioTestText.setBackgroundColor(Color.RED);
			mAudioMatchButton.startAnimation(mShakeAnimation);
		}

		@Override
		public void onGameEnd() {
			mDebugMsg.addAudio("end"
					+ Arrays.toString(mAudioGame.getCorrectRatio()));
			mHandler.post(mUpdateDebugMsgTask);
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mTotalTestCount = getIntent().getIntExtra(EXTRA_INT_TEST_COUNT, 5);
		mTestDistance = getIntent().getIntExtra(EXTRA_INT_TEST_DISTANCE, 1) - 1;

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

		mProgressBar.setMax(mTotalTestCount);

		init();

		mTotalProgressContent = "total " + mTotalTestCount;
		mTotalProgress.setText(mTotalProgressContent);

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
		/*
		 * mHandler.post(new Runnable() { int i = 0;
		 * 
		 * @Override public void run() { if (i < mSoundIds.size()) {
		 * mSoundPool.play(mSoundIds.get(i), 1.0f, 1.0f, 1, 0, 1.0f); i++; }
		 * mHandler.postDelayed(this, 1000); } });
		 */
	}

	void init() {

		mShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
		mGameTimer = new GameTimerAndroidImpl();
		RandomSequence.Builder builder = new RandomSequence.Builder()
				.setRange(8).setRepeatDistance(mTestDistance)
				.setRepeatRatio(0.3);

		mImageGame = new Game(300, 2500, mTestDistance, mGameTimer,
				builder.build(), mGraphUI);
		mImageGame.setId(1);
		mImageGame.setTestTimes(mTotalTestCount);

		RandomSequence.Builder builder2 = new RandomSequence.Builder()
				.setRange(7).setRepeatDistance(mTestDistance)
				.setRepeatRatio(0.3);
		mAudioGame = new Game(300, 2500, mTestDistance, mGameTimer,
				builder2.build(), mAudioGameUI);
		mAudioGame.setId(2);
		mAudioGame.setTestTimes(mTotalTestCount);
	}

    @SuppressWarnings("unused")
    private void initFakeSequence() {
        mImageSeq = new ISequenceSource() {
			int[] seqs = new int[] { 0, 1, 2, 3, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1,
					1, 1, 1, };

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
			int[] seqs = new int[] { 0, 1, 2, 3, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1,
					1, 1, 1, };

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
			if (TestGameActivity2.this.isFinishing() || canceled) {
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
						TestGameActivity2.this.startGame();
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

		void initAnimations(TestGameActivity2 testGameActivity2) {
			testGameActivity2.mReadyScalInAnimation = AnimationUtils
					.loadAnimation(testGameActivity2, R.anim.ready_scale_in);

			testGameActivity2.mReadyScalInAnimation.setAnimationListener(this);

			testGameActivity2.mNumberScleInAnimation = AnimationUtils
					.loadAnimation(testGameActivity2, R.anim.number_scale_in);
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

}
