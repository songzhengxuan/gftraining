
package com.example.gftranning;

import android.app.Activity;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

public class TestGameActivity2 extends Activity implements OnClickListener {
    private static final String TAG = null;

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
            mDebugMsg.addImage("end");
            mHandler.post(mUpdateDebugMsgTask);
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
            mImageView.setVisibility(View.VISIBLE);
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
        }
    };

    private final IGameUI mAudioGameUI = new IGameUI() {

        @Override
        public void display(int result, boolean isPreparaing) {
            String last = mAudioTestText.getText().toString();
            last += TextUtils.isEmpty(last) ? "" : "," + result;
            mAudioTestText.setText(last);

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
            Toast.makeText(getApplicationContext(), "Already", Toast.LENGTH_SHORT).show();
            mAudioTestText.setBackgroundColor(Color.RED);
            mAudioMatchButton.startAnimation(mShakeAnimation);
        }

        @Override
        public void onGameEnd() {
            mDebugMsg.addAudio("end");
            mHandler.post(mUpdateDebugMsgTask);
        }
    };

    private GameTimer mGameTimer;

    private ISequenceSource mImageSeq;

    private View mImageMatchButton;

    private View mAudioMatchButton;

    private Animation mShakeAnimation;

    private String mTotalProgressContent;

    private TextView mAudioTestText;

    private ISequenceSource mAudioSeq;

    private Game mAudioGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        mTotalTestCount = 5;
        mTestDistance = 0;

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
        mImageGame = new Game(300, 2500, mTestDistance, mGameTimer, mImageSeq, mGraphUI);
        mImageGame.setId(1);
        mImageGame.setTestTimes(mTotalTestCount);

        mAudioGame = new Game(300, 2500, mTestDistance, mGameTimer, mAudioSeq, mAudioGameUI);
        mAudioGame.setId(2);
        mAudioGame.setTestTimes(mTotalTestCount);

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
            int[] seqs = new int[] {
                0, 1, 2, 3, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1,
            };

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
        mAudioSeq = new ISequenceSource() {
            int[] seqs = new int[] {
                0, 1, 2, 3, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1,
            };

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
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        TestGameActivity2.this.startGame();
                    }
                }, 200);
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

}
