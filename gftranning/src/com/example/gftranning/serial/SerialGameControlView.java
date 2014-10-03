package com.example.gftranning.serial;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.TextView;

import com.example.gftranning.BuildConfig;
import com.example.gftranning.R;

public class SerialGameControlView implements OnScrollListener {
	public interface INewGameStartAction {
		void startNewGame(int testCount, int distance);
	}

	private static final String TAG = SerialGameController.class.getSimpleName();

	private Context mContext;
	private SerialGameController mController;
	private NumberPicker mTestNumberPicker;
	private NumberPicker mTestDistancePicker;
	private NumberPicker mGameCountPicker;
	private Set<NumberPicker> mIdleNumberPicker = new HashSet<NumberPicker>();
	private Button mBeginGameButton;
	private ProgressBar mProgressBar;
	private TextView mScoreProgressTextView;
	private TextView mGameProgressTextView;
	private TextView mResultTextView;

	private ViewGroup mGameConfigLayout;
	private ViewGroup mGameProgressLayout;
	private ViewGroup mGameResultLayout;

	private INewGameStartAction mNewGameStarter;

	public void findAllViews(Activity hostActivity) {
		mContext = hostActivity;
		mGameConfigLayout = (ViewGroup) hostActivity.findViewById(R.id.game_config_layout);
		mGameProgressLayout = (ViewGroup) hostActivity.findViewById(R.id.game_progress_layout);
		mGameResultLayout = (ViewGroup) hostActivity.findViewById(R.id.game_result_layout);
		mProgressBar = (ProgressBar) hostActivity.findViewById(R.id.progress_serial_game_progress);
		mScoreProgressTextView = (TextView) hostActivity.findViewById(R.id.tv_serial_game_score_progress);
		mGameProgressTextView = (TextView) hostActivity.findViewById(R.id.tv_serial_game_progress);
		mResultTextView = (TextView) hostActivity.findViewById(R.id.tv_serial_game_result);

		mTestNumberPicker = (NumberPicker) hostActivity.findViewById(R.id.test_number_picker);
		mTestNumberPicker.setOnScrollListener(this);
		mTestDistancePicker = (NumberPicker) hostActivity.findViewById(R.id.test_distance_picker);
		mTestDistancePicker.setOnScrollListener(this);
		mGameCountPicker = (NumberPicker) hostActivity.findViewById(R.id.serial_game_count_picker);
		mGameCountPicker.setOnScrollListener(this);
		mIdleNumberPicker.add(mTestNumberPicker);
		mIdleNumberPicker.add(mTestDistancePicker);
		mIdleNumberPicker.add(mGameCountPicker);

		mBeginGameButton = (Button) hostActivity.findViewById(R.id.btn_begin_serial_game);
		mBeginGameButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				GameState state = mController.getCurrentState(mContext);
				if (state == GameState.NewGame) {
					state = GameState.InGame;
					boolean oldAutoUpdateFlag = mController.setAutoUIUpdate(false);
					mController.setTestDistance(getUserSetTestDistance());
					mController.setSerialGameTotal(getUserSetGameCount());
					mController.setTestTimeInEachTest(getUserSetTestNumber());
					mController.setSerialGameProgress(0);
					mController.setCurrentState(mContext, state);
					mController.setAutoUIUpdate(oldAutoUpdateFlag);
					mNewGameStarter.startNewGame(getUserSetTestNumber(), getUserSetTestDistance());
				} else if (state == GameState.InGame) {
					mController.setCurrentState(mContext, state);
					mNewGameStarter.startNewGame(getUserSetTestNumber(), getUserSetTestDistance());
				} else if (state == GameState.GameEnd) {
					state = GameState.NewGame;
					mController.setCurrentState(mContext, state);
				}
			}
		});
	}

	public void init(Activity hostActivity, SerialGameController controller) {
		findAllViews(hostActivity);
		setController(controller);
	}

	public void updateUI() {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "updateUI() called");
		}
		SerialGameController controller = mController;
		GameState state = controller.getSerialGameState();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "state is " + state);
		}
		switch (state) {
		case NewGame:
			mGameConfigLayout.setVisibility(View.VISIBLE);
			mGameProgressLayout.setVisibility(View.GONE);
			mGameResultLayout.setVisibility(View.GONE);
			mTestNumberPicker.setMinValue(1);
			mTestNumberPicker.setMaxValue(controller.getMaxTestTimeInEachTest());
			mTestNumberPicker.setValue(controller.getTestTimeInEachTest());
			mTestDistancePicker.setMinValue(1);
			mTestDistancePicker.setMaxValue(controller.getMaxTestDistance());
			mTestDistancePicker.setValue(controller.getTestDistance());
			mGameCountPicker.setMinValue(1);
			mGameCountPicker.setMaxValue(controller.getSerialGameMaxTotal());
			mGameCountPicker.setValue(controller.getSerialGameTotal());
			break;
		case InGame:
			mGameConfigLayout.setVisibility(View.GONE);
			mGameProgressLayout.setVisibility(View.VISIBLE);
			mGameResultLayout.setVisibility(View.GONE);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "progress and total is " + mController.getSerialProgress());
				Log.d(TAG, "progress and total is " + mController.getSerialGameTotal());
			}
			mGameProgressTextView
					.setText("" + mController.getSerialProgress() + "/" + mController.getSerialGameTotal());
			mScoreProgressTextView.setText("" + mController.getTotalCorrectTimeInSerial() + "/"
					+ mController.getTotalTestTimeInSerial());
			mProgressBar.setMax(mController.getSerialGameTotal() * mController.getTestTimeInEachTest() * 2);
			mProgressBar.setSecondaryProgress(mController.getTotalTestTimeInSerial());
			mProgressBar.setProgress(mController.getTotalCorrectTimeInSerial());
			break;
		case GameEnd:
			mGameConfigLayout.setVisibility(View.GONE);
			mGameProgressLayout.setVisibility(View.GONE);
			mGameResultLayout.setVisibility(View.VISIBLE);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "total correct is " + mController.getTotalCorrectTimeInSerial());
				Log.d(TAG, "total test time is " + mController.getTotalTestTimeInSerial());
			}
			mResultTextView.setText("" + mController.getTotalCorrectTimeInSerial() + "/"
					+ mController.getTotalTestTimeInSerial());
			mBeginGameButton.setText(R.string.start_new_game);
			break;
		default:
			break;
		}
	}

	public int getUserSetTestDistance() {
		return mTestDistancePicker.getValue();
	}

	public void setTestDistance(int testDistance) {
		mTestDistancePicker.setValue(testDistance);
	}

	public int getUserSetTestNumber() {
		return mTestNumberPicker.getValue();
	}

	public void setTestNumber(int testNumber) {
		mTestNumberPicker.setValue(testNumber);
	}

	public int getUserSetGameCount() {
		return mGameCountPicker.getValue();
	}

	public void setGameCount(int gameCount) {
		mGameCountPicker.setValue(gameCount);
	}

	public void setNewGameStarter(INewGameStartAction starter) {
		mNewGameStarter = starter;
	}

	public void setController(SerialGameController serialGameControll) {
		mController = serialGameControll;
	}

	@Override
	public void onScrollStateChange(NumberPicker picker, int state) {
		if (state == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
			mIdleNumberPicker.add(picker);
		} else {
			mIdleNumberPicker.remove(picker);
		}
	}

}
