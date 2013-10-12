package com.jnsapps.workshiftcalendar.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.jnsapps.workshiftcalendar.model.Shift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Joaquín Navarro Salmerón
 *
 */
public class WorkTableDbAdapter {
	
	private DataBaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
    
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
    public WorkTableDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public WorkTableDbAdapter open() throws SQLException {
        mDbHelper = DataBaseHelper.getInstance(mCtx);
        mDb = mDbHelper.getWritableDatabase(); 
        return this;
    }
    

    public void close() {
        mDbHelper.close();
    }
    
    public void setPattern(ArrayList<Shift> shiftPattern, Calendar start, Calendar end){
    	Calendar datePointer = (Calendar) start.clone();
    	int i = 0;
    	Object[] pattern = shiftPattern.toArray();
		int l = pattern.length;
    	mDb.beginTransaction();
    	try {
    		while (datePointer.compareTo(end) < 1) {
				Shift s = (Shift) pattern[i];
				ContentValues values = new ContentValues();
		    	values.put(DataBaseHelper.KEY_DATE, df.format(datePointer.getTime()));
		    	values.put(DataBaseHelper.KEY_SHIFT_FINAL, s.getAbbreviation());
		    	values.put(DataBaseHelper.KEY_NAME, s.getName());
		    	values.put(DataBaseHelper.KEY_COLOR, s.getColor());
		    	values.put(DataBaseHelper.KEY_HOURS, s.getHours());
		    	mDb.replace(DataBaseHelper.DATABASE_TABLE_WORKSHIFT, null, values);
				datePointer.add(Calendar.DAY_OF_MONTH, 1);
				i = ++i % l;
			}
    		mDb.setTransactionSuccessful();
    	} finally {
    		mDb.endTransaction();
    	}

    }
    
    public long setWorkShift(Calendar date, String shift_abbreviation, String shift_name, int color, float hours){
    	ContentValues values = new ContentValues();
    	values.put(DataBaseHelper.KEY_DATE, df.format(date.getTime()));
    	values.put(DataBaseHelper.KEY_SHIFT_FINAL, shift_abbreviation);
    	values.put(DataBaseHelper.KEY_NAME, shift_name);
    	values.put(DataBaseHelper.KEY_COLOR, color);
    	values.put(DataBaseHelper.KEY_HOURS, hours);
    	long insert_res = mDb.insert(DataBaseHelper.DATABASE_TABLE_WORKSHIFT, null, values);
    	if(insert_res==-1){
    		long update_res = mDb.update(DataBaseHelper.DATABASE_TABLE_WORKSHIFT, values, DataBaseHelper.KEY_DATE+"=\""+df.format(date.getTime())+"\"", null);
    		return update_res;
    	}else{
    		return insert_res;
    	}
    }
    
    private long setWorkShift(Calendar date, String shift, String shift_name, float hours, int color, String previousShift){
    	ContentValues values = new ContentValues();
    	values.put(DataBaseHelper.KEY_DATE, df.format(date.getTime()));
    	values.put(DataBaseHelper.KEY_SHIFT_FINAL, shift);
    	values.put(DataBaseHelper.KEY_NAME, shift_name);
    	values.put(DataBaseHelper.KEY_COLOR, color);
    	values.put(DataBaseHelper.KEY_HOURS, hours);
    	values.put(DataBaseHelper.KEY_SHIFT_PREVIOUS, previousShift);
    	return mDb.update(DataBaseHelper.DATABASE_TABLE_WORKSHIFT, values, DataBaseHelper.KEY_DATE+"=\""+df.format(date.getTime())+"\"", null);
    }
    
    public String changeWorkShift(Calendar date, String newShift, String newShiftName, int color, float newShiftHours){
    	//Get previousShift
    	String previousShift = getShift(date);
    	//Switch newShift and previousShift
    	if(previousShift != null){
    		setWorkShift(date, newShift, newShiftName, newShiftHours, color, previousShift);
    	}
    	return previousShift;
    }
    
    public String getShift(Calendar date){
    	Cursor mCursor =
            mDb.query(true, DataBaseHelper.DATABASE_TABLE_WORKSHIFT,
            		new String[] {
            		DataBaseHelper.KEY_SHIFT_FINAL}, 
            		DataBaseHelper.KEY_DATE + " = '" + 
            			df.format(date.getTime()) + "'" ,
            		null, null, null, null, null);
        if (mCursor != null && mCursor.getCount()==1) {
            mCursor.moveToFirst();
            int shiftFinalIndex = mCursor.getColumnIndex(DataBaseHelper.KEY_SHIFT_FINAL);
            String res = mCursor.getString(shiftFinalIndex);
            mCursor.close();
            return res;
        }else{
        	if(mCursor != null){
        		mCursor.close();
        	}
        	return null;
        }
    }
    
    public Shift getShiftObject(Calendar date){
    	Cursor mCursor =
            mDb.query(true, DataBaseHelper.DATABASE_TABLE_WORKSHIFT,
            		new String[] {
            		DataBaseHelper.KEY_SHIFT_FINAL,
            		DataBaseHelper.KEY_NAME,
            		DataBaseHelper.KEY_HOURS,
            		DataBaseHelper.KEY_COLOR}, 
            		DataBaseHelper.KEY_DATE + " = '" + 
            			df.format(date.getTime()) + "'" ,
            		null, null, null, null, null);
        if (mCursor != null && mCursor.getCount()==1) {
            mCursor.moveToFirst();
            int abbreviationIndex = mCursor.getColumnIndex(DataBaseHelper.KEY_SHIFT_FINAL);
            int nameIndex = mCursor.getColumnIndex(DataBaseHelper.KEY_NAME);
            int hoursIndex = mCursor.getColumnIndex(DataBaseHelper.KEY_HOURS);
            int colorIndex = mCursor.getColumnIndex(DataBaseHelper.KEY_COLOR);
            String abbr = mCursor.getString(abbreviationIndex);
            String name = mCursor.getString(nameIndex);
            float hours = mCursor.getFloat(hoursIndex);
            int color = mCursor.getInt(colorIndex);
            Shift res = new Shift();
            res.setAbbreviation(abbr);
            res.setName(name);
            res.setHours(hours);
            res.setColor(color);
            mCursor.close();
            return res;
        }else{
        	if(mCursor != null){
        		mCursor.close();
        	}
        	return null;
        }
    }
    
    public int deleteShift(Calendar date){
    	return mDb.delete(DataBaseHelper.DATABASE_TABLE_WORKSHIFT, DataBaseHelper.KEY_DATE + " = '" + 
            			df.format(date.getTime()) + "'", null);
    }
    
    public Cursor fetchShifts(Calendar startDate, Calendar endDate){
    	Cursor mCursor =
            mDb.query(true, DataBaseHelper.DATABASE_TABLE_WORKSHIFT,
            		new String[] {
            		DataBaseHelper.KEY_ROWID,
            		DataBaseHelper.KEY_DATE,
            		DataBaseHelper.KEY_SHIFT_FINAL,
            		DataBaseHelper.KEY_NAME,
            		DataBaseHelper.KEY_COLOR,
            		DataBaseHelper.KEY_HOURS,
            		DataBaseHelper.KEY_SHIFT_PREVIOUS}, 
            		DataBaseHelper.KEY_DATE + " BETWEEN '" + 
            			df.format(startDate.getTime()) + "' AND '" +
            			df.format(endDate.getTime()) + "'", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public long setOvertime(Calendar date, long minutes){
    	ContentValues values = new ContentValues();
    	values.put(DataBaseHelper.KEY_DATE, df.format(date.getTime()));
    	values.put(DataBaseHelper.KEY_MINUTES, minutes);
    	long res = mDb.insert(DataBaseHelper.DATABASE_TABLE_OVERTIME, null, values);
    	if(res==-1){
    		res = mDb.update(DataBaseHelper.DATABASE_TABLE_OVERTIME, values, DataBaseHelper.KEY_DATE+"=\""+df.format(date.getTime())+"\"", null);
    	}
    	if(minutes==0){
    		res = mDb.delete(DataBaseHelper.DATABASE_TABLE_OVERTIME, DataBaseHelper.KEY_DATE + " = '" + 
        			df.format(date.getTime()) + "'", null);
    	}
    	return res;
    }
    
    public long getOvertime(Calendar date){
    	Cursor mCursor =
            mDb.query(true, DataBaseHelper.DATABASE_TABLE_OVERTIME,
            		new String[] {
            		DataBaseHelper.KEY_MINUTES}, 
            		DataBaseHelper.KEY_DATE + " = '" + 
            			df.format(date.getTime()) + "'" ,
            		null, null, null, null, null);
        if (mCursor != null && mCursor.getCount()==1) {
            mCursor.moveToFirst();
            int minutesIndex = mCursor.getColumnIndex(DataBaseHelper.KEY_MINUTES);
            long res = mCursor.getLong(minutesIndex);
            mCursor.close();
            return res;
        }else{
        	if(mCursor != null){
        		mCursor.close();
        	}
        	return -1;
        }
    }
    
    public int deleteOvertime(Calendar date){
    	return mDb.delete(DataBaseHelper.DATABASE_TABLE_OVERTIME, DataBaseHelper.KEY_DATE + " = '" + 
    			df.format(date.getTime()) + "'", null);
    }
    
    public Cursor fetchOvertime(Calendar startDate, Calendar endDate){
    	Cursor mCursor =
            mDb.query(true, DataBaseHelper.DATABASE_TABLE_OVERTIME,
            		new String[] {
            		DataBaseHelper.KEY_ROWID,
            		DataBaseHelper.KEY_DATE,
            		DataBaseHelper.KEY_MINUTES}, 
            		DataBaseHelper.KEY_DATE + " BETWEEN '" + 
            			df.format(startDate.getTime()) + "' AND '" +
            			df.format(endDate.getTime()) + "'", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public long setNote(Calendar date, String note) {
    	ContentValues values = new ContentValues();
    	values.put(DataBaseHelper.KEY_DATE, df.format(date.getTime()));
    	values.put(DataBaseHelper.KEY_NOTE, note);
    	long insert_res = mDb.insert(DataBaseHelper.DATABASE_TABLE_NOTES, null, values);
    	if(insert_res==-1) {
    		long update_res = mDb.update(DataBaseHelper.DATABASE_TABLE_NOTES, values, DataBaseHelper.KEY_DATE+"=\""+df.format(date.getTime())+"\"", null);
    		return update_res;
    	}else {
    		return insert_res;
    	}
    }
    
    public String getNote(Calendar date){
    	Cursor mCursor =
            mDb.query(true, DataBaseHelper.DATABASE_TABLE_NOTES,
            		new String[] {
            		DataBaseHelper.KEY_NOTE}, 
            		DataBaseHelper.KEY_DATE + " = '" + 
            			df.format(date.getTime()) + "'" ,
            		null, null, null, null, null);
        if (mCursor != null && mCursor.getCount()==1) {
            mCursor.moveToFirst();
            int noteIndex = mCursor.getColumnIndex(DataBaseHelper.KEY_NOTE);
            return mCursor.getString(noteIndex);
        }else{
        	return null;
        }
    }
    
    public Cursor fetchShiftNames(){
    	String query = "SELECT DISTINCT " + DataBaseHelper.KEY_NAME + " FROM " + DataBaseHelper.DATABASE_TABLE_WORKSHIFT;
    	return mDb.rawQuery(query, null);
    }
    
    public long setDailyNote(Calendar date, String note) {
    	ContentValues values = new ContentValues();
    	values.put(DataBaseHelper.KEY_DATE, df.format(date.getTime()));
    	values.put(DataBaseHelper.KEY_NOTE, note);
    	long insert_res = mDb.insert(DataBaseHelper.DATABASE_TABLE_DAILY_NOTES, null, values);
    	if(insert_res==-1) {
    		long update_res = mDb.update(DataBaseHelper.DATABASE_TABLE_DAILY_NOTES, values, DataBaseHelper.KEY_DATE+"=\""+df.format(date.getTime())+"\"", null);
    		return update_res;
    	}else {
    		return insert_res;
    	}
    }
    
    public boolean existsDailyNote(Calendar date) {
    	String query = "SELECT 1 FROM ? WHERE ? = ?";
    	Cursor cursor = mDb.rawQuery(query, 
    	        new String[] { DataBaseHelper.DATABASE_TABLE_DAILY_NOTES,
    						   DataBaseHelper.KEY_DATE,
    						   df.format(date.getTime())});
    	boolean exists = (cursor.getCount() > 0);
    	cursor.close();
    	return exists;
    }
    
    public String getDailyNote(Calendar date){
    	Cursor mCursor =
            mDb.query(true, DataBaseHelper.DATABASE_TABLE_DAILY_NOTES,
            		new String[] {
            		DataBaseHelper.KEY_NOTE}, 
            		DataBaseHelper.KEY_DATE + " = '" + 
            			df.format(date.getTime()) + "'" ,
            		null, null, null, null, null);
        if (mCursor != null && mCursor.getCount()==1) {
            mCursor.moveToFirst();
            int noteIndex = mCursor.getColumnIndex(DataBaseHelper.KEY_NOTE);
            String dailyNote = mCursor.getString(noteIndex);
            mCursor.close();
            return dailyNote;
        }else{
        	return null;
        }
    }
    
    public Cursor fetchDailyNotes(Calendar startDate, Calendar endDate){
    	Cursor mCursor =
            mDb.query(true, DataBaseHelper.DATABASE_TABLE_DAILY_NOTES,
            		new String[] {DataBaseHelper.KEY_DATE,}, 
            		DataBaseHelper.KEY_DATE + " BETWEEN '" + 
            			df.format(startDate.getTime()) + "' AND '" +
            			df.format(endDate.getTime()) + "'", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public int deleteDailyNotes(Calendar date){
    	return mDb.delete(DataBaseHelper.DATABASE_TABLE_DAILY_NOTES, DataBaseHelper.KEY_DATE + " = '" + 
    			df.format(date.getTime()) + "'", null);
    }

}
