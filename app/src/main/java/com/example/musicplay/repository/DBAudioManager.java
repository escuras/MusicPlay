package com.example.musicplay.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.musicplay.domain.Audio;
import com.example.musicplay.domain.PLayList;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

public class DBAudioManager extends DBManager {

    private static final String[] columns = new String[]{Audio.ID, Audio.ALBUM,
            Audio.ARTIST, Audio.DATA, Audio.TITLE, Audio.LIST_ID};
    private Context context;

    public DBAudioManager(Context context) {
        super(context);
        this.context = context;
    }

    public Audio insert(Audio audio) {
        super.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Audio.ALBUM, audio.getAlbum());
        contentValues.put(Audio.ARTIST, audio.getArtist());
        contentValues.put(Audio.DATA, audio.getData());
        contentValues.put(Audio.TITLE, audio.getTitle());
        contentValues.put(Audio.LIST_ID, audio.getListId());
        Audio audioInside = getByDataInside(audio.getData());
        if (audioInside != null) {
            audio.setId(audioInside.getId());
            this.update(audio);
            return audio;
        }
        long id = super.getDataBase().insert(DatabaseHelper.TABLE_NAME_AUDIO,
                null, contentValues);
        super.close();
        audio.setId(id);
        return audio;
    }

    public List<PLayList> getAll() {
        DBAudioListManager dbAudioListManager = new DBAudioListManager(context);
        List<PLayList> audioLists = dbAudioListManager.getAll();
        super.open();
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_AUDIO, columns,
                null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(cursor.getColumnIndex(Audio.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(Audio.ARTIST));
                String data = cursor.getString(cursor.getColumnIndex(Audio.DATA));
                Long id = cursor.getLong(cursor.getColumnIndex(Audio.ID));
                Long listId = cursor.getLong(cursor.getColumnIndex(Audio.LIST_ID));
                String title = cursor.getString(cursor.getColumnIndex(Audio.TITLE));
                Audio audio = new Audio(data, title, album, artist, id, listId);
                for(PLayList list : audioLists) {
                    if(list.getId() == audio.getId()) {
                        list.getAudios().add(audio);
                    }
                }
            }
        }
        super.close();
        return audioLists;
    }

    public Audio getById(long id) {
        super.open();
        Audio audio = getByIdInside(id);
        super.close();
        return audio;
    }

    private Audio getByIdInside(long id) {
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_AUDIO, columns,
                Audio.ID + " = " + id, null, null, null, null);
        Audio audio = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            String album = cursor.getString(cursor.getColumnIndex(Audio.ALBUM));
            String artist = cursor.getString(cursor.getColumnIndex(Audio.ARTIST));
            String data = cursor.getString(cursor.getColumnIndex(Audio.DATA));
            Long listId = cursor.getLong(cursor.getColumnIndex(Audio.LIST_ID));
            String title = cursor.getString(cursor.getColumnIndex(Audio.TITLE));
            audio = new Audio(data, title, album, artist, id, listId);
        }
        return audio;
    }

    private Audio getByDataInside(String data) {
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_AUDIO, columns,
                Audio.DATA + " = '" + data.replaceAll("'", "''") + "'", null, null, null, null);
        Audio audio = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            String album = cursor.getString(cursor.getColumnIndex(Audio.ALBUM));
            String artist = cursor.getString(cursor.getColumnIndex(Audio.ARTIST));
            Long id = cursor.getLong(cursor.getColumnIndex(Audio.ID));
            Long listId = cursor.getLong(cursor.getColumnIndex(Audio.LIST_ID));
            String title = cursor.getString(cursor.getColumnIndex(Audio.TITLE));
            audio = new Audio(data, title, album, artist, id, listId);
        }
        return audio;
    }

    public List<Audio> getByAlbum(String album) {
        super.open();
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_AUDIO, columns,
                "lower(" + Audio.ALBUM + ") = lower('" + album.replaceAll("'", "''") + "')",
                null, null, null, null);
        List<Audio> audios = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String artist = cursor.getString(cursor.getColumnIndex(Audio.ARTIST));
                String data = cursor.getString(cursor.getColumnIndex(Audio.DATA));
                Long id = cursor.getLong(cursor.getColumnIndex(Audio.ID));
                Long listId = cursor.getLong(cursor.getColumnIndex(Audio.LIST_ID));
                String title = cursor.getString(cursor.getColumnIndex(Audio.TITLE));
                Audio audio = new Audio(data, title, album, artist, id, listId);
                audios.add(audio);
            }
        }
        super.close();
        return audios;
    }

    public List<Audio> getByArtist(String artist) {
        super.open();
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_AUDIO, columns,
                "lower(" + Audio.ARTIST + ") = lower('" + artist.replaceAll("'","''") + "')",
                null, null, null, null);
        List<Audio> audios = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(cursor.getColumnIndex(Audio.ALBUM));
                String title = cursor.getString(cursor.getColumnIndex(Audio.TITLE));
                String data = cursor.getString(cursor.getColumnIndex(Audio.DATA));
                Long id = cursor.getLong(cursor.getColumnIndex(Audio.ID));
                Long listId = cursor.getLong(cursor.getColumnIndex(Audio.LIST_ID));
                Audio audio = new Audio(data, title, album, artist, id, listId);
                audios.add(audio);
            }
        }
        super.close();
        return audios;
    }

    public List<Audio> getByList(PLayList audioList) {
        super.open();
        Cursor cursor = super.getDataBase().query(DatabaseHelper.TABLE_NAME_AUDIO, columns,
                Audio.LIST_ID + " = " + audioList.getId(),
                null, null, null, null);
        List<Audio> audios = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(cursor.getColumnIndex(Audio.ALBUM));
                String title = cursor.getString(cursor.getColumnIndex(Audio.TITLE));
                String data = cursor.getString(cursor.getColumnIndex(Audio.DATA));
                String artist = cursor.getString(cursor.getColumnIndex(Audio.ARTIST));
                Long id = cursor.getLong(cursor.getColumnIndex(Audio.ID));
                Audio audio = new Audio(data, title, album, artist, id, audioList.getId());
                audios.add(audio);
            }
        }
        super.close();
        return audios;
    }

    public void update(Audio audio) {
        if (audio == null || audio.getId() == -1) {
            return;
        }
        super.open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Audio.ARTIST, audio.getArtist());
        contentValues.put(Audio.TITLE, audio.getTitle());
        contentValues.put(Audio.DATA, audio.getData());
        contentValues.put(Audio.ALBUM, audio.getAlbum());
        contentValues.put(Audio.LIST_ID, audio.getListId());
        if (getByIdInside(audio.getId()) != null) {
            super.getDataBase().update(DatabaseHelper.TABLE_NAME_AUDIO, contentValues,
                    Audio.ID + " = " + audio.getId(), null);
        }
        super.close();
    }

    public void delete(long id) {
        super.open();
        super.getDataBase().delete(DatabaseHelper.TABLE_NAME_AUDIO,
                Audio.ID + "=" + id, null);
        super.close();
    }

    public void delete(Audio audio) {

        if (audio == null || audio.getId() == -1) {
            return;
        }
        super.open();
        super.getDataBase().delete(DatabaseHelper.TABLE_NAME_AUDIO,
                Audio.ID + "=" + audio.getId(), null);
        super.close();
    }
}
