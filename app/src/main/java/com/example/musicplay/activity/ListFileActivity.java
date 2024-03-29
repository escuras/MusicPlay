package com.example.musicplay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.musicplay.R;
import com.example.musicplay.dialog.FileDialog;
import com.example.musicplay.domain.Audio;
import com.example.musicplay.domain.WayPath;
import com.example.musicplay.adapter.FolderAdapter;
import com.example.musicplay.file.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListFileActivity extends AppCompatActivity {

    private String storagePath;
    private List<WayPath> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);
        values = new ArrayList();
        if (getIntent().hasExtra("path")) {
            storagePath = getIntent().getStringExtra("path");
        } else {
            storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        File dir = new File(storagePath);
        if (!dir.canRead()) {
            return;
        }
        setTitle(storagePath);
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    WayPath path = new WayPath(file, dir.getAbsolutePath() + "/" + file, dir.getName());
                    if(path.isDirectory()) {
                        values.add(path);
                    } else if(FileUtils.isAudio(path.getAbsolutePath())) {
                        values.add(path);
                    }
                }
            }
        }
        Collections.sort(values);
        FolderAdapter folderAdapter = new FolderAdapter(this, values);
        ListView listView = findViewById(R.id.list_files);
        listView.setAdapter(folderAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        addClickListeners();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                ListView listView = findViewById(R.id.list_files);
                int val = listView.getChildCount();
                ListAdapter adapter = listView.getAdapter();
                List<Audio> audios = new ArrayList<>();
                for (int value = 0; value < val; value++) {
                    View view = listView.getChildAt(value).findViewById(R.id.itemcheckBox);
                    if (view instanceof CheckBox) {
                        if (((CheckBox) view).isChecked()) {
                            WayPath wayPath = (WayPath) adapter.getItem(value);
                            Audio audio = new Audio(wayPath.getAbsolutePath(), wayPath.getName(), wayPath.getFolder(), "");
                            audios.add(audio);
                        }
                    }
                }
                if (audios.size() > 0) {
                    FileDialog fileDialog = new FileDialog();
                    fileDialog.setListAudios(audios);
                    fileDialog.show(getFragmentManager(), "");
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addClickListeners() {
        this.addClickAdapter();
        this.addLongClickAdapter();
    }

    private void addClickAdapter() {
        ListView listView = (ListView) findViewById(R.id.list_files);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) findViewById(R.id.list_files);
                WayPath file = (WayPath) listView.getAdapter().getItem(position);
                if (new File(file.getAbsolutePath()).isDirectory()) {
                    Intent intent = new Intent(view.getContext(), ListFileActivity.class);
                    intent.putExtra("path", file.getAbsolutePath());
                    startActivity(intent);
                }
            }
        });
    }

    public void addLongClickAdapter() {
        ListView listView = (ListView) findViewById(R.id.list_files);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) findViewById(R.id.list_files);
                WayPath filename = (WayPath) listView.getAdapter().getItem(position);
                if (new File(filename.getAbsolutePath()).isDirectory()) {
                    FileDialog fileDialog = new FileDialog();
                    fileDialog.setPath(filename.getAbsolutePath());
                    fileDialog.show(getFragmentManager(), "");
                }
                return true;
            }
        });
    }
}
