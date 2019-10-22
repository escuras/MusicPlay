package com.example.musicplay.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.musicplay.R;
import com.example.musicplay.domain.Audio;
import com.example.musicplay.file.StorageUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListAlbunsFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private List<Audio> audioList;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loadAudio();
        return inflater.inflate(R.layout.list_music_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, loadAlbuns());
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String value = (String) parent.getAdapter().getItem(position);
        if (value != null && value.length() > 0) {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(this);
            Bundle bundle = new Bundle();
            bundle.putString("album",value);
            ListMusicFragment fragment = new ListMusicFragment();
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.downFragment, fragment);
            fragmentTransaction.commit();
        }
    }

    private void loadAudio() {
        StorageUtil storageUtil = new StorageUtil(getContext());
        audioList =  storageUtil.loadAudio();
    }

    private Object[] loadAlbuns() {
        Set<String> albuns = new HashSet<>();
        for (Audio audio : audioList) {
            albuns.add(audio.getAlbum());
        }
        return albuns.toArray();
    }
}

