package com.example.gftranning.highscore;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DatabaseContract {

	public static final class HighScore implements BaseColumns {

		static final String TABLE_NAME = "highscore";
		public static final String AUTHORITY = ScoreProvider.AUTHORITY + "/" + TABLE_NAME;
		public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
		public static final String SQL_CREATE_TABLE = "CREATE TABLE high_score IF NOT EXISTS ("
				+ " _id INTEGER PRIMARY KEY, total INTEGER NOT NULL DEFAULT 1, correct INTEGER NOT NULL DEFAULT 0, date TEXT)";

		public static final String HIGH_SCORE_TOTAL = "total";
		public static final String HIGH_SCORE_CORRECT = "correct";
		public static final String HIGH_SCORE_DATE = "date";
	}

}
