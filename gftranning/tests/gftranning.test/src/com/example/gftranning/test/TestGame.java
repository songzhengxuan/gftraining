package com.example.gftranning.test;

import android.test.AndroidTestCase;

import com.example.gftranning.Game;
import com.example.gftranning.GameTimer;
import com.example.gftranning.IGameUI;
import com.example.gftranning.ISequenceSource;

public class TestGame extends AndroidTestCase {

	public void testTimer() {
	}

	public void testGame() {
		ISequenceSource seq = new ISequenceSource() {
			int[] seqs = new int[] { 1, 1, 1, 2, 1, 1, 2, 1, 1, 1, };
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
		GameTimer timer = new GameTimer() {

			@Override
			public void setNextTimerEvent(long absTime, int id,int tag) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setNextTimerEventDelayed(long delay, int id, int tag) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addCallback(int id, GameTimerCallback callback) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clearAllPendingTimerEvent(int id) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeTimerEventForGame(int id) {
				// TODO Auto-generated method stub
				
			}

		};
		IGameUI gameui = new IGameUI() {

			@Override
			public void onSucceed(int lastResult) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(int excepted, int errorResult) {
				// TODO Auto-generated method stub

			}

			@Override
			public void hide() {
				// TODO Auto-generated method stub

			}

			@Override
			public void display(int result, boolean isPreparaing) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAlreadyMarked() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGameEnd() {
				// TODO Auto-generated method stub

			}
		};
		Game game = new Game(0, 0, 1, timer, seq, gameui);
		assertTrue(game.checkAndRememberUserInput(true));
	}

}
