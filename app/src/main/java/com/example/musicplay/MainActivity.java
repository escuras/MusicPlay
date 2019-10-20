package com.example.musicplay;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.musicplay.domain.Audio;
import com.example.musicplay.domain.PLayList;
import com.example.musicplay.activity.ListFileActivity;
import com.example.musicplay.file.StorageUtil;
import com.example.musicplay.repository.DBAudioListManager;
import com.example.musicplay.repository.DBAudioManager;
import com.example.musicplay.service.MusicService;
import com.example.musicplay.fragment.ListObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SUCCES_REQUEST_PERMISSION_CODE = 1;
    public static final String PLAY_NEW_AUDIO = "com.example.musicplay.PlayNewAudio";
    private String path;
    private MusicService player;
    private boolean serviceBound = false;
    private List<Audio> audioList;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicPlayBinder binder = (MusicService.MusicPlayBinder) service;
            player = binder.getService();
            serviceBound = true;
            Toast.makeText(MainActivity.this, "Ligado ao serviço", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        addFragment();
        loadAudio();
        getLists();
        playAudio(2);
       // playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            player.stopSelf();
        }
    }

    public void goToFiles(View view) {
        Intent intent = new Intent(this, ListFileActivity.class);
        startActivity(intent);
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SUCCES_REQUEST_PERMISSION_CODE);

        }
    }

    private void addFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainFragment, new ListObject());
        fragmentTransaction.commit();
    }

    private void playAudio(int audioIndex) {
        StorageUtil storage = new StorageUtil(getApplicationContext());
        if (!serviceBound) {
            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);
            Intent playerIntent = new Intent(this, MusicService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            storage.storeAudioIndex(audioIndex);
            Intent broadcastIntent = new Intent(PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    private List<PLayList> getLists(){
        DBAudioManager dbAudiomanager = new DBAudioManager(this);
        List<PLayList> audios = dbAudiomanager.getAll();
        return audios;
    }

    private PLayList saveList(PLayList audioList){
        DBAudioListManager dbAudioListManager = new DBAudioListManager(this);
        return dbAudioListManager.insert(audioList);
    }

    private Audio saveAudio(PLayList audioList, Audio audio){
        audio.setListId(audioList.getId());
        DBAudioManager dbAudioManager = new DBAudioManager(this);
        return dbAudioManager.insert(audio);
    }

    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
        audioList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                audioList.add(new Audio(data, title, album, artist));
            }
        }
        cursor.close();
    }




}
