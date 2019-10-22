package com.example.musicplay.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.example.musicplay.R;
import com.example.musicplay.domain.Audio;
import com.example.musicplay.domain.PLayList;
import com.example.musicplay.domain.WayPath;
import com.example.musicplay.file.FolderAdapter;
import com.example.musicplay.repository.DBRepository;
import com.example.musicplay.util.FileUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileDialog extends DialogFragment {

    private DBRepository dbRepository;
    private String path = "";
    private List<Audio> audios = new ArrayList<>();
    private PLayList pLayList;
    private String authority = "com.example.musicplay";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(buildReproductionLists());
        builder.setMessage(R.string.addToList)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(audios.size() > 0) {
                            DBRepository dbRepository = new DBRepository(getContext());
                            for(Audio audio : audios) {
                                dbRepository.saveAudio(pLayList, audio);
                            }
                        }  else if (!path.equals("")) {
                            addContentToPlayList(pLayList);
                        }
                    }
                });
        return builder.create();
    }

    private ListView buildReproductionLists() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.list_menu, null);
        ListView listView = (ListView) view.getChildAt(0);
        view.removeView(listView);
        List<PLayList> values = new ArrayList<>();
        initDatabase(getContext());
        for (PLayList list : dbRepository.getPlayLists()) {
            values.add(list);
        }
        ArrayAdapter adapter = new ArrayAdapter(getContext(),
                android.R.layout.select_dialog_singlechoice, values.toArray());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedItem = (CheckedTextView) view;
                checkedItem.toggle();
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (i != position) {
                        CheckedTextView other = (CheckedTextView) parent.getChildAt(i);
                        other.setChecked(false);
                    }
                }
                pLayList = (PLayList) parent.getAdapter().getItem(position);
            }
        });
        return listView;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setListAudios(List<Audio> audios) {
        this.audios = audios;
    }

    private void initDatabase(Context context){
        dbRepository = new DBRepository(context);
    }

    private void addContentToPlayList(PLayList pLayList) {
        File dir = new File(path);
        if (!dir.canRead()) {
            return;
        }
        String[] list = dir.list();
        List<WayPath> values = new ArrayList<>();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    WayPath path = new WayPath(file, dir.getAbsolutePath() + "/" + file, dir.getName());
                    values.add(path);
                }
            }
        }
        if(values.size() > 0) {
            for(WayPath wayPath : values){
                if(wayPath.isDirectory()) {
                    continue;
                }
                if (FileUtils.isAudio(wayPath.getAbsolutePath())) {
                    DBRepository dbRepository = new DBRepository(getContext());
                    Audio audio = new Audio(wayPath.getAbsolutePath(), wayPath.getName(), wayPath.getFolder(), "");
                    dbRepository.saveAudio(pLayList, audio);
                }
            }
        }
    }
}
