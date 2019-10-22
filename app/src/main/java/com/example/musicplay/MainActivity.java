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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.musicplay.domain.Audio;
import com.example.musicplay.domain.PLayList;
import com.example.musicplay.activity.ListFileActivity;
import com.example.musicplay.file.StorageUtil;
import com.example.musicplay.fragment.ListPlayListsFragment;
import com.example.musicplay.repository.DBAudioListManager;
import com.example.musicplay.repository.DBAudioManager;
import com.example.musicplay.service.MusicService;
import com.example.musicplay.fragment.ListAlbunsFragment;
import com.example.musicplay.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SUCCES_REQUEST_PERMISSION_CODE = 1;
    private String path;
    private MusicService player;
    public static boolean serviceBound = false;
    private List<Audio> audioList;

    public static final String PLAY_NEW_AUDIO = "com.example.musicplay.PlayNewAudio";

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
        playAudio(4);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_files:
                goToFiles();
                break;
            case R.id.menu2:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
            if(player != null) {
                player.stopSelf();
            }
        }
    }

    public void goToFiles() {
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
        fragmentTransaction.add(R.id.downFragment, new ListAlbunsFragment());
        fragmentTransaction.add(R.id.upFragment, new ListPlayListsFragment());
        fragmentTransaction.commit();
    }

    private void playAudio(int audioIndex) {
        StorageUtil storage = new StorageUtil(this);
        storage.storeAudioIndex(audioIndex);
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MusicService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            Intent broadcastIntent = new Intent(MusicService.ACTION_PLAY);
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
        if (cursor != null) {
            while (cursor.getCount() > 0 && cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                audioList.add(new Audio(data, title, album, artist));
            }
            cursor.close();
            StorageUtil storageUtil = new StorageUtil(this);
            storageUtil.storeAudio(audioList);
        }

    }

    public void play(View view){
        SystemUtils.vibrate(this, 50);
        Intent broadcastIntent = new Intent(MusicService.ACTION_PLAY);
        sendBroadcast(broadcastIntent);
    }

    public void pause(View view){
        SystemUtils.vibrate(this, 50);
        Intent broadcastIntent = new Intent(MusicService.ACTION_PAUSE);
        sendBroadcast(broadcastIntent);
    }

    public void forward(View view){
        SystemUtils.vibrate(this, 50);
        Intent broadcastIntent = new Intent(MusicService.ACTION_NEXT);
        sendBroadcast(broadcastIntent);
    }

    public void previous(View view){
        SystemUtils.vibrate(this, 50);
        Intent broadcastIntent = new Intent(MusicService.ACTION_PREVIOUS);
        sendBroadcast(broadcastIntent);
    }

    public List<Audio> getAudioList(){
        return audioList;
    }




}
