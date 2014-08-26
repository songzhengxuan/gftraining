package com.example.gftranning;

import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

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
			generateEasy(result, distance, testTime, video, audio);
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
	 * init a random sequence which has no match
	 * */
	private void initRandomSequence(ArrayList<Pair<Integer, Integer>> result,
			int distance, int testTime, int video, int audio) {
		Deque<Integer> videoSet = new ArrayDeque<Integer>();
		Deque<Integer> audioSet = new ArrayDeque<Integer>();
		result.clear();
		for (int i = 0; i < 1 + distance + testTime; ++i) {
			int newVideo = mRandom.nextInt(video);
			int triedTime = 0;
			while (videoSet.contains(newVideo) && triedTime < video) {
				newVideo = (newVideo + 1) % video;
				++triedTime;
			}
			videoSet.addLast(Integer.valueOf(newVideo));
			while (videoSet.size() >= (distance + 1)) {
				videoSet.pop();
			}

			int newAudio = mRandom.nextInt(audio);
			triedTime = 0;
			while (audioSet.contains(newAudio) && triedTime < audio) {
				newAudio = (newAudio + 1) % audio;
				++triedTime;
			}
			audioSet.addLast(Integer.valueOf(newAudio));
			while (audioSet.size() >= (distance + 1)) {
				audioSet.pop();
			}

			result.add(new Pair<Integer, Integer>(Integer.valueOf(newVideo),
					Integer.valueOf(newAudio)));
		}
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
	private void generateEasy(ArrayList<Pair<Integer, Integer>> result,
			int distance, int testTime, int video, int audio) {
		final int eachTestLength = distance + 1 + 1;
		final int totalDisplayNum = 1 + distance + testTime;
		final int maxAssignedMatchCount = (totalDisplayNum - 1)
				/ eachTestLength;
		int triedCount = 0;
		int assignedMatchCount = mRandom.nextInt(maxAssignedMatchCount + 1);
		while (assignedMatchCount == 0 && ++triedCount < 3) {
			assignedMatchCount = mRandom.nextInt(maxAssignedMatchCount + 1);
		}
		int freeCount = totalDisplayNum - assignedMatchCount * eachTestLength;
		int offset = 0;

		for (int i = 0; i < assignedMatchCount; ++i) {
			offset += mRandom.nextInt(freeCount);
		}
	}

	private void generateNormal(ArrayList<Pair<Integer, Integer>> result,
			int distance, int testTime, int video, int audio) {
	}

	private void computeHard(ArrayList<Pair<Integer, Integer>> result,
			int distance, int testTime, int video, int audio) {
	}

	private void computeHell(ArrayList<Pair<Integer, Integer>> result,
			int distance, int testTime, int video, int audio) {
	}

}
