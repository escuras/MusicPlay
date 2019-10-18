package com.example.musicplay.repository;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.musicplay.domain.AudioList;

public class DBManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    protected DBManager(Context context){
        this.context = context;
    }

    protected DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    protected void close(){
        dbHelper.close();
    }

    protected SQLiteDatabase getDataBase(){
        return database;
    }
}
