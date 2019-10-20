package com.example.musicplay.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.musicplay.domain.PLayList;

import java.util.ArrayList;
import java.util.List;

public class DBAudioListManager extends DBManager {

    public DBAudioListManager(Context context) {
        super(context);
    }

    public PLayList insert(PLayList audiolist) {
        super.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLayList.NAME, audiolist.getName());
        PLayList list = getByNameInside(audiolist.getName());
        if (list != null) {
            return list;
        }
        long id = super.getDataBase().insert(DatabaseHelper.TABLE_NAME_LIST,
                null, contentValues);
        super.close();
        audiolist.setId(id);
        return audiolist;
    }

    public List<PLayList> getAll() {
        super.open();
        String[] columns = new String[]{PLayList.ID, PLayList.NAME};
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_LIST, columns,
                null, null, null, null, null);
        List<PLayList> audioLists = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(PLayList.ID));
                String name = cursor.getString(cursor.getColumnIndex(PLayList.NAME));
                PLayList audioList = new PLayList(id, name);
                audioLists.add(audioList);
            }
        }
        super.close();
        return audioLists;
    }

    public PLayList getById(long id) {
        super.open();
        PLayList audioList = getByIdInside(id);
        super.close();
        return audioList;
    }

    private PLayList getByIdInside(long id) {
        String[] columns = new String[]{PLayList.ID, PLayList.NAME};
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_LIST, columns,
                PLayList.ID + " = " + id, null, null, null, null);
        PLayList audioList = null;
        if (cursor != null && cursor.getCount() > 0) {
            id = cursor.getLong(cursor.getColumnIndex(PLayList.ID));
            String name = cursor.getString(cursor.getColumnIndex(PLayList.NAME));
            audioList = new PLayList(id, name);
        }
        return audioList;
    }

    private PLayList getByNameInside(String name) {
        String[] columns = new String[]{PLayList.ID, PLayList.NAME};
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_LIST, columns,
                "lower(" + PLayList.NAME + ") = lower('" + name.replaceAll("'","''") + "')",
                null, null, null, null);
        PLayList audioList = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            long id = cursor.getLong(cursor.getColumnIndex(PLayList.ID));
            name = cursor.getString(cursor.getColumnIndex(PLayList.NAME));
            audioList = new PLayList(id, name);
        }
        return audioList;
    }

    public PLayList getByName(String name) {
        super.open();
        PLayList audioList = getByNameInside(name);
        super.close();
        return audioList;
    }

    public void update(PLayList audioList) {
        if (audioList == null || audioList.getId() == -1) {
            super.close();
            return;
        }
        super.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLayList.NAME, audioList.getName());
        if (getByIdInside(audioList.getId()) != null) {
            super.getDataBase().update(DatabaseHelper.TABLE_NAME_LIST, contentValues,
                    PLayList.ID + " = " + audioList.getId(), null);
        }
        super.close();
    }

    public void delete(long id) {
        super.open();
        super.getDataBase().delete(DatabaseHelper.TABLE_NAME_LIST,
                PLayList.ID + "=" + id, null);
        super.close();
    }

    public void delete(PLayList audioList) {
        if (audioList == null || audioList.getId() == -1) {
            return;
        }
        super.open();
        super.getDataBase().delete(DatabaseHelper.TABLE_NAME_LIST,
                PLayList.ID + "=" + audioList.getId(), null);
        super.close();
    }

}
