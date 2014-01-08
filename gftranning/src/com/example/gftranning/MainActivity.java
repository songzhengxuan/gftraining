package com.example.gftranning;

import java.util.ArrayDeque;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Callback, OnClickListener {

    private ImageView mImageView;
    private View mImageAnswerView;
    private Button mImageMatchButton;
    private Button mAudioMatchButton;
    private Handler mHandler;
    private ArrayDeque<Integer> mImageDeque = new ArrayDeque<Integer>();
    private Random mRandom = new Random();
    private boolean mImageUserHasPressed;
    private int mCorrectCount = 0;
    private int mErrorCount = 0;
    private int mDistance = 2;
    private TextView mDistanceText;

    private static final int MSG_WAIT_TO_START = 0;
    private static final int MSG_SHOW_IMAGE = 1;
    private static final int MSG_HIDE_IMAGE = 2;
    private static final int MSG_SHOW_IMAGE_ANSWER = 3; //arg1: 0 for error, 1 for coreect
    private static final int MSG_HIDE_IMAGE_ANSWER = 4;
    private static final int MSG_SET_DISTANCE = 5; // arg1 for distance 
    private static final int MSG_IMAGE_WAIT_PRESS_END = 6;

    private static final int ANSWER_SHOW_TIME_LENGTH = 800;
    private static final long SHOW_TIME_LENGTH = 500;
    private static final long WAIT_PRESS_LENGTH = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image);
        mDistanceText = (TextView) findViewById(R.id.distance_text);
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
            int next = mRandom.nextInt(8);
            mImageDeque.addLast(next);
            while (mImageDeque.size() > mDistance) {
                mImageDeque.removeFirst();
            }
            int resId = getResources()
                    .getIdentifier(getPackageName() + ":drawable/" + "image" + (next + 1), null, null);
            mImageView.setImageResource(resId);
            mImageView.setVisibility(View.VISIBLE);
            mImageUserHasPressed = false;
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_IMAGE, SHOW_TIME_LENGTH);
            break;
        case MSG_HIDE_IMAGE:
            mImageView.setVisibility(View.INVISIBLE);
            mHandler.sendEmptyMessageDelayed(MSG_IMAGE_WAIT_PRESS_END, WAIT_PRESS_LENGTH);
            break;
        case MSG_IMAGE_WAIT_PRESS_END:
            if (!mImageUserHasPressed) {
                if (mImageDeque.size() == mDistance) {
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
            mHandler.sendEmptyMessageDelayed(MSG_HIDE_IMAGE_ANSWER, ANSWER_SHOW_TIME_LENGTH);
            break;
        case MSG_HIDE_IMAGE_ANSWER:
            mImageAnswerView.setVisibility(View.INVISIBLE);
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
            mImageUserHasPressed = true;
            if (mImageDeque.size() != mDistance || (mImageDeque.getFirst() != mImageDeque.getLast())) {
                mErrorCount++;
                mHandler.obtainMessage(MSG_SHOW_IMAGE_ANSWER, 0, 0).sendToTarget();
            } else {
                mCorrectCount++;
                mHandler.obtainMessage(MSG_SHOW_IMAGE_ANSWER, 1, 0).sendToTarget();
            }
        }
    }
}
