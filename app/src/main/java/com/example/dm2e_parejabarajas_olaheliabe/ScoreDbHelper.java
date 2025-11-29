package com.example.dm2e_parejabarajas_olaheliabe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoreDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GameScores.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SCORES = "scores";

    // Columns
    private static final String COL_NAME = "player_name";
    private static final String COL_SCORE = "score";

    public ScoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table: Name must be unique
        String createTable = "CREATE TABLE " + TABLE_SCORES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT UNIQUE, " +
                COL_SCORE + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    public void saveOrUpdateScore(String name, int newScore) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_SCORES, new String[]{COL_SCORE},
                COL_NAME + "=?", new String[]{name}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int savedScore = cursor.getInt(0);

            if (newScore < savedScore) {
                ContentValues values = new ContentValues();
                values.put(COL_SCORE, newScore);
                db.update(TABLE_SCORES, values, COL_NAME + "=?", new String[]{name});
            }
            cursor.close();
        } else {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, name);
            values.put(COL_SCORE, newScore);
            db.insert(TABLE_SCORES, null, values);
        }
        db.close();
    }
}