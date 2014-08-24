package com.example.gftranning;

import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Stack;

import android.util.Pair;

public class CombinedSequence {
	public static final int EASY = -1;
	public static final int NORMAL = -2;
	public static final int HARD = -3;

	private SecureRandom mRandom = new SecureRandom(String.valueOf(
			System.currentTimeMillis()).getBytes());

	public ArrayList<Pair<Integer, Integer>> generateSequence(int distance,
			int testTime, int video, int audio, int difficulty) {
		ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
		switch (difficulty) {
		case EASY:
			computeEasy(result, distance, testTime, video, audio);
			break;
		}
		return result;
	}

	private static final int DEFAULT = 0;
	private static final int MATCH_IMAGE = 1;
	private static final int MATCH_AUDIO = 2;
	private static final int MATCH_TAIL = 4;
	private static final int MATCH_HEAD = 8;

	public int computeDifficulty(ArrayList<Pair<Integer, Integer>> sequences,
			int distance, int testTime, int video, int audio) {
		int result = 0;
		if (sequences.size() != (distance + 1 + testTime)) {
			throw new IllegalArgumentException("invalid size");
		}

		Deque<Integer> imageMatch = new ArrayDeque<Integer>();
		Deque<Integer> audioMatch = new ArrayDeque<Integer>();

		for (int i = 0; i < testTime; ++i) {
			int currentImage = sequences.get(i).first;
			int currentAudio = sequences.get(i).second;
			int nextTargetImage = sequences.get(i + distance + 1).first;
			int nextTargetAudio = sequences.get(i + distance + 1).second;

			// compute the past for score
			{
				boolean isImageMatch = false;
				if (!imageMatch.isEmpty()) {
					int imageMatchStart = imageMatch.peek();
					if ((imageMatchStart + distance) == i) {
						result += imageMatch.size();
						isImageMatch = true;
					}
				}

				boolean isAudioMatch = false;
				if (!audioMatch.isEmpty()) {
					int audioMatchStart = audioMatch.peek();
					if ((audioMatchStart + distance) == i) {
						result += audioMatch.size();
						isAudioMatch = true;
					}
				}

				if (isAudioMatch && isImageMatch) {
					result -= 1;
				}
			}

			// check for future usage
			{
			}

		}

		return result = 0;
	}

	/**
	 * compute
	 * 
	 * @param result
	 * @param distance
	 * @param testTime
	 * @param video
	 * @param audio
	 */
	private void computeEasy(ArrayList<Pair<Integer, Integer>> result,
			int distance, int testTime, int video, int audio) {
		final int eachTestLength = distance + 1 + 1;
		final int totalDisplayNum = distance + 1 + testTime;
		int[] occupiedFlag = new int[totalDisplayNum];
		for (int i = 0; i < totalDisplayNum; ++i) {
			occupiedFlag[i] = -1;
		}
		while (true) {

		}
	}

	private void computeNormal(ArrayList<Pair<Integer, Integer>> result,
			int distance, int testTime, int video, int audio) {
	}

	private void computeHard(ArrayList<Pair<Integer, Integer>> result,
			int distance, int testTime, int video, int audio) {
	}

	private void computeHell(ArrayList<Pair<Integer, Integer>> result,
			int distance, int testTime, int video, int audio) {
	}

}
