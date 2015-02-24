package com.railzapp.tours.model.tour;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ToursDataSource {

    // Database fields
    private SQLiteDatabase database;
    private TourDbHelper dbHelper;
    private String[] allColumns = {
            TourDbHelper.COLUMN_ID,
            TourDbHelper.COLUMN_CODE,
            TourDbHelper.COLUMN_NAME,
            TourDbHelper.COLUMN_DURATION,
            TourDbHelper.COLUMN_COMPLEXITY,
            TourDbHelper.COLUMN_SHORT_DESC,
            TourDbHelper.COLUMN_LONG_DESC
    };

    public ToursDataSource(Context context) {
        dbHelper = new TourDbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Tour createTour(String code, String name, String duration, String complexity, String short_desc, String long_desc) {
        ContentValues values = new ContentValues();
        values.put(TourDbHelper.COLUMN_CODE, code);
        values.put(TourDbHelper.COLUMN_NAME, name);
        values.put(TourDbHelper.COLUMN_DURATION, duration);
        values.put(TourDbHelper.COLUMN_COMPLEXITY, complexity);
        values.put(TourDbHelper.COLUMN_SHORT_DESC, short_desc);
        values.put(TourDbHelper.COLUMN_LONG_DESC, long_desc);

        long insertId = database.insert(TourDbHelper.TABLE_TOURS, null, values);
        Cursor cursor = database.query(TourDbHelper.TABLE_TOURS,
                allColumns,
                TourDbHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Tour newTour = cursorToTour(cursor);
        cursor.close();
        return newTour;
    }

    public void deleteTour(Tour comment) {
        long id = comment.getId();
        System.out.println("Tour deleted with id: " + id);
        database.delete(TourDbHelper.TABLE_TOURS,
                TourDbHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Tour> getAllTours() {
        List<Tour> comments = new ArrayList<Tour>();

        Cursor cursor = database.query(TourDbHelper.TABLE_TOURS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tour comment = cursorToTour(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private Tour cursorToTour(Cursor cursor) {
        Tour tour = new Tour();
        tour.setId(cursor.getLong(0));
        tour.setCode(cursor.getString(1));
        tour.setName(cursor.getString(2));
        tour.setDuration(cursor.getString(3));
        tour.setComplexity(cursor.getString(4));
        tour.setShortDesc(cursor.getString(5));
        tour.setLongDesc(cursor.getString(6));
        return tour;
    }
} 
