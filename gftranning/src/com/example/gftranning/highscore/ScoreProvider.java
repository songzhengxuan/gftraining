package com.example.gftranning.highscore;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.gftranning.GFTApplication;

public class ScoreProvider extends ContentProvider {
	public static final String AUTHORITY = GFTApplication.PKGNAME + ".score";
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
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long id = -1;
		switch (sUriMatcher.match(uri)) {
		case HIGH_SCORE_DIRECTORY:
			id = db.insert(DatabaseContract.HighScore.TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("unsupported inert");
		}
		if (id != -1) {
			uri = uri.buildUpon().appendPath("" + id).build();
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		return null;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new ScoreDbHelper(getContext(), "score", null, DB_VER);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs, String order) {

		Log.d(TAG, "query called");
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		case HIGH_SCORE_DIRECTORY:
			Log.d(TAG, "query called directory");
			queryBuilder.setTables(DatabaseContract.HighScore.TABLE_NAME);
			queryBuilder.setProjectionMap(DatabaseContract.HighScore.sProjectionMap);
			break;
		case HIGH_SCORE_ID:
			Log.d(TAG, "query called id");
			queryBuilder.setTables(DatabaseContract.HighScore.TABLE_NAME);
			queryBuilder.setProjectionMap(DatabaseContract.HighScore.sProjectionMap);
			String id = uri.getLastPathSegment();
			queryBuilder.appendWhere(BaseColumns._ID + "=" + id);
			break;
		default:
			throw new IllegalArgumentException("unsupported inert");
		}
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor cursor = queryBuilder.query(db, projectionIn, selection, selectionArgs, null, null, order);
		if (cursor != null) {
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return cursor;
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
