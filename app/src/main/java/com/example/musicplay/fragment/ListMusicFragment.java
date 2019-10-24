package com.example.musicplay.fragment;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.musicplay.MainActivity;
import com.example.musicplay.R;
import com.example.musicplay.domain.Audio;
import com.example.musicplay.domain.PLayList;
import com.example.musicplay.file.StorageUtil;
import com.example.musicplay.repository.DBRepository;
import com.example.musicplay.service.MusicService;
import com.example.musicplay.file.FileUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ListMusicFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private static final String RETURN_CHARACTERS = "...";

    private String album = "";
    private Long listId;
    private MusicService player;
    public static List<Audio> audioList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        String playListJson = mBundle.getString("playList");
        if (playListJson != null) {
            PLayList playList = new Gson().fromJson(playListJson, PLayList.class);
            DBRepository dbRepository = new DBRepository(getContext());
            audioList = dbRepository.getAudioFromPlayList(playList);
            StorageUtil storageUtil = new StorageUtil(getContext());
            storageUtil.storeAudio(audioList);
            listId = playList.getId();
        } else {
            album = mBundle.getString("album");
            loadAudio(album);
        }
        return inflater.inflate(R.layout.list_music_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, loadMusic());
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Audio value = (Audio) parent.getAdapter().getItem(position);
        if (value.getTitle().equals(RETURN_CHARACTERS)) {
            FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
            fragmentTransaction.remove(this);
            Bundle bundle = new Bundle();
            ListAlbumFragment fragment = new ListAlbumFragment();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.downFragment, fragment);
            fragmentTransaction.commit();
        } else {
            for (int index = 0; index < audioList.size(); index++) {
                Audio audio = audioList.get(index);
                if (audio.getAlbum().equals(value.getAlbum())
                        && audio.getTitle().equals(value.getTitle())
                        && audio.getData().equals(value.getData())) {
                    playAudio(index);
                }
            }
        }

    }

    private void loadAudio(String album) {
        StorageUtil storageUtil = new StorageUtil(getContext());
        audioList = storageUtil.loadAudio(getActivity(), album, false);
    }

    private List<Audio> loadMusic() {
        List<Audio> music = new ArrayList<>();
        music.add(new Audio("", RETURN_CHARACTERS, "", ""));
        for (Audio audio : audioList) {
            if (audio.getAlbum().equals(album) ||
                    (listId != null &&
                            listId == audio.getListId())
                            && FileUtils.isAudio(audio.getData())) {
                music.add(audio);
            }
        }
        return music;
    }

    private void playAudio(int audioIndex) {
        StorageUtil storage = new StorageUtil(getContext());
        storage.storeAudioIndex(audioIndex);
        if (!MainActivity.serviceBound) {
            Intent playerIntent = new Intent(getContext(), MusicService.class);
            getContext().startService(playerIntent);
            getContext().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            Intent broadcastIntent = new Intent(MainActivity.PLAY_NEW_AUDIO);
            getContext().sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MainActivity.serviceBound) {
            try {
                getActivity().unbindService(serviceConnection);
            } catch (IllegalArgumentException e) { }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicPlayBinder binder = (MusicService.MusicPlayBinder) service;
            player = binder.getService();
            MainActivity.serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MainActivity.serviceBound = false;
        }
    };
}
