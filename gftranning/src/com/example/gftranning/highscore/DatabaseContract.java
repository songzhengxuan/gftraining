package com.example.gftranning.highscore;

import java.util.HashMap;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DatabaseContract {

	public static final class HighScore implements BaseColumns {

		static final String TABLE_NAME = "highscore";
		public static final Uri CONTENT_URI = Uri.parse("content://" + ScoreProvider.AUTHORITY + "/" + TABLE_NAME);
		public static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS highscore ("
				+ " _id INTEGER PRIMARY KEY, total INTEGER NOT NULL DEFAULT 0, correct INTEGER NOT NULL DEFAULT 0, date TEXT)";

		public static final String HIGH_SCORE_TOTAL = "total";
		public static final String HIGH_SCORE_CORRECT = "correct";
		public static final String HIGH_SCORE_DATE = "date";

		public static final HashMap<String, String> sProjectionMap = new HashMap<String, String>();
		static {
			sProjectionMap.put(_ID, _ID);
			sProjectionMap.put(HIGH_SCORE_TOTAL, HIGH_SCORE_TOTAL);
			sProjectionMap.put(HIGH_SCORE_CORRECT, HIGH_SCORE_CORRECT);
			sProjectionMap.put(HIGH_SCORE_DATE, HIGH_SCORE_DATE);
		}
	}

}
