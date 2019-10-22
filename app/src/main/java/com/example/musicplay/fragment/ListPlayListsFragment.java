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
import com.example.musicplay.domain.PLayList;
import com.example.musicplay.file.StorageUtil;
import com.example.musicplay.repository.DBRepository;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListPlayListsFragment  extends ListFragment implements AdapterView.OnItemClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_music_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, loadPlayLists());
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PLayList value = (PLayList) parent.getAdapter().getItem(position);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(this);
        Bundle bundle = new Bundle();
        bundle.putString("playList", new Gson().toJson(value));
        ListMusicFragment fragment = new ListMusicFragment();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.downFragment, fragment);
        fragmentTransaction.commit();

    }

    private List<PLayList> loadPlayLists() {
        DBRepository dbRepository = new DBRepository(getContext());
        return dbRepository.getPlayLists();
    }


}
