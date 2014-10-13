package com.example.gftranning.highscore;

import com.example.gftranning.GFTApplication;
import com.example.gftranning.highscore.DatabaseContract.HighScore;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class ScoreProvider extends ContentProvider {
	public static final String AUTHORITY = "content://" + GFTApplication.PKGNAME + ".score";
	private static final String TAG = ScoreProvider.class.getSimpleName();
	public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
	private static final int DB_VER = 1;

	static final int HIGH_SCORE_DIRECTORY = 1;
	static final int HIGH_SCORE_ID = 2;

	private final class ScoreDbHelper extends SQLiteOpenHelper {

		public ScoreDbHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DatabaseContract.HighScore.SQL_CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		}

		public void onDowngrade(SQLiteDatabase db, int oldVer, int newVer) {
		}
	}

	private SQLiteOpenHelper mOpenHelper;

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new ScoreDbHelper(getContext(), "score", null, DB_VER);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projections, String selections, String[] selectionArg, String order) {

		Log.d(TAG, "query called");
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		switch (sUriMatcher.match(uri)) {
		case HIGH_SCORE_DIRECTORY:
			Log.d(TAG, "query called directory");

			break;
		case HIGH_SCORE_ID:
			break;

		default:
			break;
		}
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(0);
		sUriMatcher.addURI(AUTHORITY, "highscore", HIGH_SCORE_DIRECTORY);
		sUriMatcher.addURI(AUTHORITY, "highscore/#", HIGH_SCORE_DIRECTORY);
	}

}
