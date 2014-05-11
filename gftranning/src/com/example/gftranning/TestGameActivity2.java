package com.example.gftranning;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TestGameActivity2 extends Activity implements OnClickListener {
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
	String mCheckResultsForTest;

	IGameUI mGraphUI = new IGameUI() {

		@Override
		public void onSucceed(int lastResult) {
			mImageView.setBackgroundColor(Color.GREEN);
			mCheckResultsForTest += "[V]";
			mAnswerText.setText(mCheckResultsForTest);
		}

		@Override
		public void onGameEnd() {
			mCheckResultsForTest += "end";
			mAnswerText.setText(mCheckResultsForTest);
		}

		@Override
		public void onError(int excepted, int errorResult) {
			mImageView.setBackgroundColor(Color.RED);
			mImageMatchButton.startAnimation(mShakeAnimation);
			mCheckResultsForTest += "[X]";
			mAnswerText.setText(mCheckResultsForTest);
		}

		@Override
		public void onAlreadyMarked() {
			Toast.makeText(getApplicationContext(), "Already",
					Toast.LENGTH_SHORT).show();
			mImageView.setBackgroundColor(Color.RED);
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
			mImageView.setVisibility(View.VISIBLE);
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
		}
	};
	private GameTimer mGameTimer;
	private ISequenceSource mImageSeq;
	private View mImageMatchButton;
	private View mAudioMatchButton;
	private Animation mShakeAnimation;
	private String mTotalProgressContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		mTotalTestCount = 5;
		mTestDistance = 0;

		mAnswerText = (TextView) findViewById(R.id.answer_text);
		mCheckResultsForTest = "";

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

		init();
		mImageGame = new Game(300, 2500, mTestDistance, mGameTimer, mImageSeq,
				mGraphUI);
		mImageGame.setId(1);
		mImageGame.setTestTimes(mTotalTestCount);
		mTotalProgressContent = "total " + mTotalTestCount;
		mTotalProgress.setText(mTotalProgressContent);

		mAnimationPlayer = new StartAnimationPlayer();
		mAnimationPlayer.initAnimations(this);
		mAnimationPlayer.play();
	}

	void init() {
		mShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
		mGameTimer = new GameTimerAndroidImpl();
		mImageSeq = new ISequenceSource() {
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
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAnimationPlayer.isPlaying()) {
			mAnimationPlayer.cancel();
		}
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
				TestGameActivity2.this.startGame();
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
		mImageGame.start();
	}

	@Override
	public void onClick(View view) {
		if (view == mImageMatchButton) {
			if (mImageGame != null) {
				mImageGame.checkAndRememberUserInput(true);
			}
		} else if (view == mAudioMatchButton) {
		}

	}

}
