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

    public PLayList savePlayList(PLayList pLayList){
        DBAudioListManager dbAudioListManager = new DBAudioListManager(context);
        return dbAudioListManager.insert(pLayList);
    }

    public Audio saveAudio(PLayList pLayList, Audio audio){
        audio.setListId(pLayList.getId());
        DBAudioManager dbAudioManager = new DBAudioManager(context);
        return dbAudioManager.insert(audio);
    }

    public List<Audio> getAudioFromPlayList(PLayList pLayList){
        DBAudioManager dbAudioManager = new DBAudioManager(context);
        return dbAudioManager.getByList(pLayList);
    }
    public void deletePlayList(PLayList pLayList){
        DBAudioManager dbAudioManager = new DBAudioManager(context);
        List<Audio> audios = dbAudioManager.getByList(pLayList);
        for(Audio audio : audios) {
            dbAudioManager.delete(audio);
        }
        DBAudioListManager dbAudioListManager = new DBAudioListManager(context);
        dbAudioListManager.delete(pLayList);
    }

}
