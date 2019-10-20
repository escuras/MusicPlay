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
import com.example.musicplay.repository.DBRepository;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FileDialog extends DialogFragment {

    private DBRepository dbRepository;
    private String path;
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
                        if(pLayList != null) {
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
                pLayList = (PLayList) parent.getAdapter().getItem(position);
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (i != position) {
                        CheckedTextView other = (CheckedTextView) parent.getChildAt(i);
                        other.setChecked(false);
                    }
                }

            }
        });
        return listView;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private void initDatabase(Context context){
        dbRepository = new DBRepository(context);
    }

    private void addContentToPlayList(PLayList pLayList) {
        if (path == null) {
            return;
        }
        Uri uri = Uri.fromFile(new File(path));
        if (uri == null) {
            return;
        }
        ContentResolver resolver = getContext().getContentResolver();
        try {
            resolver.openFileDescriptor(uri, "r");
            String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
            Cursor cursor = resolver.query(uri, null, selection, null, sortOrder);
            if (cursor != null) {
                if(cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        Audio audio = new Audio(data, title, album, artist);
                        audio.setListId(pLayList.getId());
                        dbRepository.saveAudio(pLayList, audio);
                    }
                }
                cursor.close();
            }
        } catch(FileNotFoundException fnf) {}
    }




/*
      if (checkedItem.isSelected())
    {
        checkedItem.setSelected(false);
        Drawable yourDrawable = MaterialDrawableBuilder.with(getContext()) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.CHECKBOX_BLANK_CIRCLE) // provide an icon
                .setColor(Color.WHITE) // set the icon color
                .setToActionbarSize() // set the icon size
                .build(); // Finally call build
        checkedItem.setCheckMarkDrawable (yourDrawable);
    }
                else
    {
        Drawable yourDrawable = MaterialDrawableBuilder.with(getContext()) // provide a context
                .setIcon(MaterialDrawableBuilder.IconValue.CHECKBOX_MARKED_CIRCLE) // provide an icon
                .setColor(Color.WHITE) // set the icon color
                .setToActionbarSize() // set the icon size
                .build(); // Finally call build
        checkedItem.setSelected(true);
        checkedItem.setCheckMarkDrawable(yourDrawable);
    }*/
}
