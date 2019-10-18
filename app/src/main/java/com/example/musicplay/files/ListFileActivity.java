package com.example.musicplay.files;

import android.Manifest;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.musicplay.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListFileActivity extends ListActivity {

    private String storagePath;
    private  List values = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.show();
        setContentView(R.layout.activity_list_files);

        // Use the current directory as title
        storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (getIntent().hasExtra("path")) {
            storagePath = getIntent().getStringExtra("path");
        }
        setTitle(storagePath);

        File dir = new File(storagePath);
        if (!dir.canRead()) {
            setTitle(getTitle() + " - inacessível.");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    values.add(file);
                }
            }
        }
        Collections.sort(values);

        // Put the data into the list
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_checked, android.R.id.text1, values);
        setListAdapter(adapter);
        addLongClickAdapter();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String filename = (String) getListAdapter().getItem(position);
        if (storagePath.endsWith(File.separator)) {
            filename = storagePath + filename;
        } else {
            filename = storagePath + File.separator + filename;
        }
        if (new File(filename).isDirectory()) {
            Intent intent = new Intent(this, ListFileActivity.class);
            intent.putExtra("path", filename);
            startActivity(intent);
        } else {
            Toast.makeText(this, filename + " is not a directory", Toast.LENGTH_LONG).show();
        }
    }

    public void addLongClickAdapter() {
        ListView listView = (ListView) getListView();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = (String) getListAdapter().getItem(position);
                if (new File(filename).isDirectory()) {

                }

                Toast.makeText(ListFileActivity.this, "somethind", Toast.LENGTH_LONG).show();

                return true;
            }
        });
    }
}
