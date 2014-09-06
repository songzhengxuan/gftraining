package com.example.gftranning.test;

import java.util.ArrayList;

import android.test.AndroidTestCase;
import android.util.Pair;

import com.example.gftranning.CombinedSequence;


public class TestGame extends AndroidTestCase {

	public void testTimer() {
	}

	public void testGame() {
		CombinedSequence seq = new CombinedSequence();
		ArrayList<Pair<Integer, Integer>> result = seq.generateSequence(2, 5, 7, 8, CombinedSequence.EASY);
		ArrayList<Integer> video = new ArrayList<Integer>();
		ArrayList<Integer> audio = new ArrayList<Integer>();
		for (Pair a : result) {
			video.add((Integer) a.first);
			audio.add((Integer) a.second);
		}
		assertTrue(result.size() == (2 + 1 + 5));
	}

}
