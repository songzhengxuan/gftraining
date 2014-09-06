package com.example.gftranning.serial;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;

import com.example.gftranning.R;

public class SerialGameControlView implements OnScrollListener {
	public interface INewGameStartAction {
		void startNewGame(int testCount, int distance);
	}

	private GameState mState;

	private Activity mHostActivityRef;
	private SerialGameController mController;
	private NumberPicker mTestNumberPicker;
	private NumberPicker mTestDistancePicker;
	private NumberPicker mGameCountPicker;
	private Button mBeginGameButton;

	private ViewGroup mGameConfigLayout;
	private ViewGroup mGameProgressLayout;

	private int mTestDistance;
	private int mTestNumber;
	private int mGameCount;

	private INewGameStartAction mNewGameStarter;

	public void findAllViews(Activity hostActivity) {
		mHostActivityRef = hostActivity;

		mGameConfigLayout = (ViewGroup) hostActivity.findViewById(R.id.game_config_layout);
		mGameProgressLayout = (ViewGroup) hostActivity.findViewById(R.id.game_progress_layout);

		mTestNumberPicker = (NumberPicker) hostActivity.findViewById(R.id.test_number_picker);
		mTestNumberPicker.setOnScrollListener(this);
		mTestDistancePicker = (NumberPicker) hostActivity.findViewById(R.id.test_distance_picker);
		mTestDistancePicker.setOnScrollListener(this);
		mGameCountPicker = (NumberPicker) hostActivity.findViewById(R.id.serial_game_count_picker);
		mGameCountPicker.setOnScrollListener(this);

		mBeginGameButton = (Button) hostActivity.findViewById(R.id.btn_begin_serial_game);
		mBeginGameButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mState == GameState.GameEnd) {
					mNewGameStarter.startNewGame(getUserSetGameCount(), getUserSetTestDistance());
				}
			}
		});

		mState = GameState.GameEnd;
	}

	public GameState getState() {
		return mState;
	}

	public void updateUI() {
		SerialGameController controller = mController;
		mState = controller.getSerialGameState();
		switch (mState) {
		case NewGame:
			mGameConfigLayout.setVisibility(View.VISIBLE);
			mGameProgressLayout.setVisibility(View.GONE);
			mTestNumberPicker.setMinValue(1);
			mTestNumberPicker.setMaxValue(controller.getMaxTestTimeInEachTest());
			mTestDistancePicker.setMinValue(0);
			mTestDistancePicker.setMaxValue(controller.getMaxTestDistance());
			mGameCountPicker.setMinValue(1);
			mGameCountPicker.setMaxValue(controller.getSerialGameMaxTotal());
			break;
		case InGame:
			mGameConfigLayout.setVisibility(View.GONE);
			mGameProgressLayout.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	public int getUserSetTestDistance() {
		return mTestDistance;
	}

	public void setTestDistance(int testDistance) {
		mTestDistance = testDistance;
	}

	public int getUserSetTestNumber() {
		return mTestNumber;
	}

	public void setTestNumber(int testNumber) {
		mTestNumber = testNumber;
	}

	public int getUserSetGameCount() {
		return mGameCount;
	}

	public void setGameCount(int gameCount) {
		mGameCount = gameCount;
	}

	public void setNewGameStarter(INewGameStartAction starter) {
		mNewGameStarter = starter;
	}

	public void setController(SerialGameController serialGameControll) {
		mController = serialGameControll;
	}

	@Override
	public void onScrollStateChange(NumberPicker arg0, int arg1) {

	}

}
