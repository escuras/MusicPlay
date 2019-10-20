package com.example.musicplay.repository;

import android.content.Context;

import com.example.musicplay.domain.Audio;
import com.example.musicplay.domain.PLayList;

import java.util.List;

public class DBRepository {

    private Context context;

    public DBRepository(Context context) {
        this.context = context;
    }

    public List<PLayList> getPlayLists() {
        DBAudioManager dbAudiomanager = new DBAudioManager(context);
        List<PLayList> audios = dbAudiomanager.getAll();
        return audios;
    }

    public PLayList savePlayList(PLayList audioList){
        DBAudioListManager dbAudioListManager = new DBAudioListManager(context);
        return dbAudioListManager.insert(audioList);
    }

    public Audio saveAudio(PLayList audioList, Audio audio){
        audio.setListId(audioList.getId());
        DBAudioManager dbAudioManager = new DBAudioManager(context);
        return dbAudioManager.insert(audio);
    }
}
