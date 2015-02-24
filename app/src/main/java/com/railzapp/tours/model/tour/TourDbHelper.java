package com.railzapp.tours.model.tour;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TourDbHelper extends SQLiteOpenHelper {

    public static final String TABLE_TOURS = "tour";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_COMPLEXITY = "complexity";
    public static final String COLUMN_SHORT_DESC = "short_desc";
    public static final String COLUMN_LONG_DESC = "long_desc";

    private static final String DATABASE_NAME = "ocular.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TOURS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CODE + " text not null, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_DURATION + " text not null, "
            + COLUMN_COMPLEXITY + " text not null, "
            + COLUMN_SHORT_DESC + " text not null, "
            + COLUMN_LONG_DESC + " text null "
            + ");";

    public TourDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TourDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURS);
        onCreate(db);
    }
}