package com.jnsapps.workshiftcalendar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Joaquín Navarro Salmerón
 * 
 */
public class DataBaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_TABLE_WORKSHIFT = "workshift";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_COLOR = "color";
	public static final String KEY_HOURS = "hours";
	public static final String KEY_MINUTES = "minutes";
	public static final String KEY_DATE = "date";
	public static final String KEY_SHIFT_FINAL = "shift_final";
	public static final String KEY_SHIFT_PREVIOUS = "shift_previous";

	public static final String DATABASE_TABLE_NOTES = "notes";
	public static final String KEY_NOTE = "note";

	public static final String DATABASE_TABLE_OVERTIME = "overtime";

	private static final String TAG = "DbAdapter";

	// Singleton test
	private static DataBaseHelper mInstance;

	public static DataBaseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DataBaseHelper(context.getApplicationContext());
		}
		return mInstance;
	}

	private DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE_WORKSHIFT = "create table "
			+ DATABASE_TABLE_WORKSHIFT + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_COLOR + " numeric not null, "
			+ KEY_HOURS + " numeric not null, " + KEY_DATE
			+ " text unique not null, " + KEY_SHIFT_FINAL + " text not null, "
			+ KEY_SHIFT_PREVIOUS + " text);";

	private static final String DATABASE_CREATE_NOTES = "create table "
			+ DATABASE_TABLE_NOTES + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_DATE
			+ " text unique not null, " + KEY_NOTE + " text not null);";

	private static final String DATABASE_CREATE_OVERTIME = "create table "
			+ DATABASE_TABLE_OVERTIME + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_DATE
			+ " text unique not null, " + KEY_MINUTES
			+ " numeric not null default 0);";

	private static final String DATABASE_NAME = "data";

	public static final String DATABASE_TABLE_DAILY_NOTES = "dailynotes";

	private static final String DATABASE_CREATE_DAILY_NOTES = "create table "
			+ DATABASE_TABLE_DAILY_NOTES + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_DATE
			+ " text unique not null, " + KEY_NOTE + " text not null);";

	/**
	 * Version 4 = first version Version 5 = add overtime table Version 6 = add
	 * daily notes table
	 */
	private static final int DATABASE_VERSION = 6;

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_WORKSHIFT);
		db.execSQL(DATABASE_CREATE_NOTES);
		db.execSQL(DATABASE_CREATE_OVERTIME);
		db.execSQL(DATABASE_CREATE_DAILY_NOTES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion);
		if (oldVersion < 6)
			db.execSQL(DATABASE_CREATE_DAILY_NOTES);
		if (oldVersion < 5)
			db.execSQL(DATABASE_CREATE_OVERTIME);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Downgrading database from version " + oldVersion + " to "
				+ newVersion);
		if (newVersion < 6)
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_DAILY_NOTES);
		if (newVersion < 5)
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OVERTIME);
	}

}