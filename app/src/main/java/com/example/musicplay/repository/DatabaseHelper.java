package com.example.musicplay.repository;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import org.jetbrains.annotations.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME_LIST = "LISTS";

    public static final String TABLE_NAME_AUDIO = "AUDIOS";
    private static final String DB_NAME = "MUSIC.DB";
    static final int DB_VERSION = 1;

    private static final String CREATE_TABLE_LIST = "create table " + TABLE_NAME_LIST +
            "(id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL);";
    private static final String CREATE_TABLE_AUDIO = "create table " + TABLE_NAME_AUDIO +
            "(id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "data TEXT NOT NULL, " +
            "title TEXT NOT NULL, " +
            "album TEXT DEFAULT '', " +
            "artist TEXT DEFAULT '', " +
            "listId INTEGER DEFAULT -1);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private String data;
    private String title;
    private String album;
    private String artist;
    private int id;
    private int listId;

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LIST);
        db.execSQL(CREATE_TABLE_AUDIO);
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_AUDIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LIST);
        onCreate(db);
    }
}
