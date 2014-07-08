package com.example.gftranning;

import java.util.ArrayList;
import java.util.Random;

public class RandomSequence implements ISequenceSource {
	Random mRandom = new Random();
	double mRepeatRatio;
	int mRepeatDistance;
	int mRange;
	long mSeed;
	private static final int MODE_NORMAL = 0;
	private static final int MODE_RECORDING = 1;
	private static final int MODE_REPLAYING = 2;

	int mMode;
	int mRecordStart = -1;
	private ArrayList<Integer> mHistory = new ArrayList<Integer>();

	public static class Builder {
		double repeatRatio = 0.0;
		int repeatDistance = 0;
		int range = -1;

		public Builder setRepeatRatio(double ratio) {
			if (ratio < 0 || ratio > 1.0) {
				throw new IllegalArgumentException("invalid ratio");
			}
			this.repeatRatio = ratio;
			return this;
		}

		public Builder setRepeatDistance(int distance) {
			if (distance < 0) {
				throw new IllegalArgumentException("invalid ratio");
			}
			repeatDistance = distance;
			return this;
		}

		public Builder setRange(int range) {
			if (range <= 0) {
				throw new IllegalArgumentException("invlaid range");
			}
			this.range = range;
			return this;
		}

		public RandomSequence build() {
			RandomSequence result = new RandomSequence();
			result.mRandom = new Random();
			result.mRepeatRatio = this.repeatRatio;
			result.mRange = this.range;
			result.mSeed = System.currentTimeMillis();
			result.mRandom.setSeed(result.mSeed);
			result.mMode = MODE_NORMAL;
			return result;
		}
	}

	@Override
	public int getNext() {
		int next = -1;
		if (mMode == MODE_REPLAYING) {
			if (mRecordStart < (mHistory.size() - 1)) {
				next = mHistory.get(mRecordStart++);
			}
		} else {
			if (mHistory.size() > mRepeatDistance
					&& mRandom.nextInt(100) <= mRepeatRatio * 100) {
				next = mHistory.get(mHistory.size() - mRepeatDistance - 1);
			}
			if (next == -1) {
				next = mRandom.nextInt(mRange);
			}
			mHistory.add(next);

		}
		return next;
	}

	@Override
	public void replay() {
		if (mMode == MODE_RECORDING) {
			mMode = MODE_REPLAYING;
		}
	}

	@Override
	public void record() {
		mMode = MODE_RECORDING;
		mRecordStart = mHistory.size();
	}

}
