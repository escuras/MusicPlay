package com.example.musicplay.file;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.musicplay.domain.Audio;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorageUtil {

    private final String STORAGE = " com.example.musicplay.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    public StorageUtil(Context context) {
        this.context = context;
    }

    public void storeAudio(List<Audio> audios) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(audios);
        editor.putString("audios", json);
        editor.apply();
    }


    public List<Audio> loadAudio(Activity activity, String folder, boolean firstCall) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("audios", null);
        Type type = new TypeToken<ArrayList<Audio>>() {}.getType();
        List<Audio> audioList = gson.fromJson(json, type);
        if(firstCall || audioList == null || audioList.size() == 0) {
            ContentResolver contentResolver = activity.getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
            Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
            audioList = new ArrayList<>();
            if (cursor != null) {
                while (cursor.getCount() > 0 && cursor.moveToNext()) {
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    if(folder != null && folder.equals(album)) {
                        audioList.add(new Audio(data, title, album, artist));
                    } else if (folder == null) {
                        audioList.add(new Audio(data, title, album, artist));
                    }
                }
                cursor.close();
            }
            this.storeAudio(audioList);
        }
        return audioList;
    }

    public void storeAudioIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    public int loadAudioIndex() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -1 if no data found
    }

    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}