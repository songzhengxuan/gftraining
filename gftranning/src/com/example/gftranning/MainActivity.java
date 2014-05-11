package com.example.gftranning;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Callback, OnClickListener {
    private static final boolean DEBUG = BuildConfig.DEBUG && true;
    private ImageView mImageView;
    private View mImageAnswerView;
    private TextView mImageArrayText;
    private Button mImageMatchButton;
    @SuppressWarnings("unused")
	private Button mAudioMatchButton;
    private Handler mHandler;
    private ArrayDeque<Integer> mImageDeque = new ArrayDeque<Integer>();
    private Random mRandom = new Random();
    private boolean mImageUserHasPressed;
    private int mTotalCount = 0;
    private int mErrorCount = 0;
    private int mDistance = 2;
    private int mForcePercent = 50;
    private TextView mDistanceText;

    private static final int MSG_WAIT_TO_START = 0;
    private static final int MSG_SHOW_IMAGE = 1;
    private static final int MSG_HIDE_IMAGE = 2;
    private static final int MSG_SHOW_IMAGE_ANSWER = 3; //arg1: 0 for error, 1 for coreect
    private static final int MSG_HIDE_IMAGE_ANSWER = 4;
    private static final int MSG_SET_DISTANCE = 5; // arg1 for distance 
    private static final int MSG_IMAGE_WAIT_PRESS_END = 6;
    private static final int MSG_HIDE_IMAGE_ANSWER_TEXT = 8;

    private static final int ANSWER_SHOW_TIME_LENGTH = 800;
    private static final long SHOW_TIME_LENGTH = 500;
    private static final long WAIT_PRESS_LENGTH = 2500;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image);
        mDistanceText = (TextView) findViewById(R.id.distance_text);
        mImageArrayText = (TextView) findViewById(R.id.image_array_text);
        mImageAnswerView = findViewById(R.id.answer_image);
        mImageMatchButton = (Button) findViewById(R.id.image_matched);
        mAudioMatchButton = (Button) findViewById(R.id.audio_matched);
        mImageMatchButton.setOnClickListener(this);
        mRandom.setSeed(System.currentTimeMillis());
        mHandler = new Handler(this);

        mHandler.sendEmptyMessage(MSG_WAIT_TO_START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case MSG_WAIT_TO_START:
            Toast.makeText(getApplicationContext(), R.string.will_start_in_3_seconds, Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessageDelayed(MSG_SHOW_IMAGE, 3000);
            break;
        case MSG_SHOW_IMAGE:
            int next = -1;
            if (shouldForceRate()) {
                int dest = 0;
                int t = -1;
                Iterator<Integer> iter = mImageDeque.descendingIterator();
                while (iter.hasNext() && ++dest < (mDistance + 1)) {
                    t = iter.next();
                }
                if (dest == (mDistance + 1)) {
                    next = t;
                }
            }
            if (next == -1) {
                next = mRandom.nextInt(8);
            }
            mImageDeque.addLast(next);
            if (mImageDeque.size() >= mDistance + 1) {
                ++mTotalCount;
            }
            while (mImageDeque.size() > mDistance + 1) {
                mImageDeque.removeFirst();
            }
            int resId = getResources()
                    .getIdentifier(getPackageName() + ":drawable/" + "image" + (next + 1), null, null);
            mImageView.setImageResource(resId);
            mImageView.setVisibility(View.VISIBLE);
            mImageUserHasPressed = false;
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_IMAGE, SHOW_TIME_LENGTH);
            if (DEBUG) {
                Toast.makeText(getApplicationContext(), Arrays.toString(mImageDeque.toArray()), 300).show();
            }
            break;
        case MSG_HIDE_IMAGE:
            mImageView.setVisibility(View.INVISIBLE);
            mHandler.sendEmptyMessageDelayed(MSG_IMAGE_WAIT_PRESS_END, WAIT_PRESS_LENGTH);
            break;
        case MSG_IMAGE_WAIT_PRESS_END:
            if (!mImageUserHasPressed) {
                if (mImageDeque.size() == (mDistance + 1) && mImageDeque.getFirst() == mImageDeque.getLast()) {
                    Message answerMsg = mHandler.obtainMessage(MSG_SHOW_IMAGE_ANSWER);
                    if ((mImageDeque.getFirst() == mImageDeque.getLast())) {
                        mErrorCount++;
                        answerMsg.arg1 = 0;
                    }
                    answerMsg.sendToTarget();
                }
            }
            mHandler.sendEmptyMessage(MSG_SHOW_IMAGE);
            break;
        case MSG_SET_DISTANCE:
            mDistance = msg.arg1;
            mDistanceText.setText("distance is " + mDistance);
            break;
        case MSG_SHOW_IMAGE_ANSWER:
            mImageAnswerView.setBackgroundColor(msg.arg1 == 1 ? Color.GREEN : Color.RED);
            mImageAnswerView.setVisibility(View.VISIBLE);
            mImageArrayText.setText(Arrays.toString(mImageDeque.toArray()) + "\n" + (mTotalCount - mErrorCount) + ":"
                    + mTotalCount);
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_IMAGE_ANSWER_TEXT, 1000);
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_IMAGE_ANSWER, ANSWER_SHOW_TIME_LENGTH);
            break;
        case MSG_HIDE_IMAGE_ANSWER:
            mImageAnswerView.setVisibility(View.INVISIBLE);
            break;
        case MSG_HIDE_IMAGE_ANSWER_TEXT:
            mImageArrayText.setText("");
            break;
        default:
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == mImageMatchButton) {
            if (mImageUserHasPressed) {
                return;
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "mImageDeque is :" + Arrays.toString(mImageDeque.toArray()));
            }
            mImageUserHasPressed = true;
            if (mImageDeque.size() != (mDistance + 1) || (mImageDeque.getFirst() != mImageDeque.getLast())) {
                mErrorCount++;
                mHandler.obtainMessage(MSG_SHOW_IMAGE_ANSWER, 0, 0).sendToTarget();
            } else {
                mHandler.obtainMessage(MSG_SHOW_IMAGE_ANSWER, 1, 0).sendToTarget();
            }
        }
    }

    private boolean shouldForceRate() {
        return (mRandom.nextInt(100) < mForcePercent);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
