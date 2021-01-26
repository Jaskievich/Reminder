package com.example.Reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class RemindDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ReminderDB.db";
    public static final String TABLE_NAME = "Reminder";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCR = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_AFILE = "audio_file";

    private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                    COLUMN_NAME + " TEXT NOT NULL," +
                    COLUMN_DESCR + " TEXT,"+
                    COLUMN_DATE +" NUMERIC NOT NULL," +
                    COLUMN_AFILE + " TEXT)";

    private static final String sortOrder = COLUMN_DATE + " DESC";



    public RemindDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertItem (ReminderItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, item.getTitle());
        contentValues.put(COLUMN_DESCR, item.getDescription());
        contentValues.put(COLUMN_DATE, item.getDate().getTime());
        contentValues.put(COLUMN_AFILE, item.getAudio_file());
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateItem (ReminderItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, item.getTitle());
        contentValues.put(COLUMN_DESCR, item.getDescription());
        contentValues.put(COLUMN_DATE, item.getDate().getTime());
        contentValues.put(COLUMN_AFILE, item.getAudio_file());
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(item.getId()) } );
        return true;
    }

    public Cursor getAllTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.query(TABLE_NAME,null,null,null,null,null, sortOrder );
        return res;
    }
}
