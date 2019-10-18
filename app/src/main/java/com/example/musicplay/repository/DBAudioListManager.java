package com.example.musicplay.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.musicplay.domain.AudioList;

import java.util.ArrayList;
import java.util.List;

public class DBAudioListManager extends DBManager {

    public DBAudioListManager(Context context) {
        super(context);
    }

    public AudioList insert(AudioList audiolist) {
        super.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AudioList.NAME, audiolist.getName());
        AudioList list = getByNameInside(audiolist.getName());
        if (list != null) {
            return list;
        }
        long id = super.getDataBase().insert(DatabaseHelper.TABLE_NAME_LIST,
                null, contentValues);
        super.close();
        audiolist.setId(id);
        return audiolist;
    }

    public List<AudioList> getAll() {
        super.open();
        String[] columns = new String[]{AudioList.ID, AudioList.NAME};
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_LIST, columns,
                null, null, null, null, null);
        List<AudioList> audioLists = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(AudioList.ID));
                String name = cursor.getString(cursor.getColumnIndex(AudioList.NAME));
                AudioList audioList = new AudioList(id, name);
                audioLists.add(audioList);
            }
        }
        super.close();
        return audioLists;
    }

    public AudioList getById(long id) {
        super.open();
        AudioList audioList = getByIdInside(id);
        super.close();
        return audioList;
    }

    private AudioList getByIdInside(long id) {
        String[] columns = new String[]{AudioList.ID, AudioList.NAME};
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_LIST, columns,
                AudioList.ID + " = " + id, null, null, null, null);
        AudioList audioList = null;
        if (cursor != null && cursor.getCount() > 0) {
            id = cursor.getLong(cursor.getColumnIndex(AudioList.ID));
            String name = cursor.getString(cursor.getColumnIndex(AudioList.NAME));
            audioList = new AudioList(id, name);
        }
        return audioList;
    }

    private AudioList getByNameInside(String name) {
        String[] columns = new String[]{AudioList.ID, AudioList.NAME};
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_LIST, columns,
                "lower(" + AudioList.NAME + ") = lower('" + name.replaceAll("'","''") + "')",
                null, null, null, null);
        AudioList audioList = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            long id = cursor.getLong(cursor.getColumnIndex(AudioList.ID));
            name = cursor.getString(cursor.getColumnIndex(AudioList.NAME));
            audioList = new AudioList(id, name);
        }
        return audioList;
    }

    public AudioList getByName(String name) {
        super.open();
        AudioList audioList = getByNameInside(name);
        super.close();
        return audioList;
    }

    public void update(AudioList audioList) {
        if (audioList == null || audioList.getId() == -1) {
            super.close();
            return;
        }
        super.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(AudioList.NAME, audioList.getName());
        if (getByIdInside(audioList.getId()) != null) {
            super.getDataBase().update(DatabaseHelper.TABLE_NAME_LIST, contentValues,
                    AudioList.ID + " = " + audioList.getId(), null);
        }
        super.close();
    }

    public void delete(long id) {
        super.open();
        super.getDataBase().delete(DatabaseHelper.TABLE_NAME_LIST,
                AudioList.ID + "=" + id, null);
        super.close();
    }

    public void delete(AudioList audioList) {
        if (audioList == null || audioList.getId() == -1) {
            return;
        }
        super.open();
        super.getDataBase().delete(DatabaseHelper.TABLE_NAME_LIST,
                AudioList.ID + "=" + audioList.getId(), null);
        super.close();
    }

}
