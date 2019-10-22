package com.example.musicplay.file;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.musicplay.R;
import com.example.musicplay.domain.WayPath;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.List;

public class FolderAdapter extends ArrayAdapter<WayPath> {

    private LayoutInflater layoutInflater;
    private int color = Color.DKGRAY;
    private FloatingActionButton fab;

    public FolderAdapter(Context context,  List<WayPath> objects) {
        super(context, android.R.layout.simple_list_item_1, android.R.id.text1, objects);
        layoutInflater = LayoutInflater.from(context);
        fab = new FloatingActionButton(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        WayPath path = getItem(position);
        if (path.isDirectory()){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WayPath path = getItem(position);
        if(convertView != null) {
            return convertView;
        }
        if (path.isDirectory()) {
            convertView = layoutInflater.inflate(R.layout.item_folder, null);
            TextView textView = convertView.findViewById(R.id.itemFolder);
            textView.setText(path.getName());
            textView.setTextColor(toogleColor());
        } else {
            convertView = layoutInflater.inflate(R.layout.item, null);
            final CheckBox checkBox = convertView.findViewById(R.id.itemcheckBox);
            checkBox.setText(path.getName());
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout relativeLayout = ((Activity) getContext()).findViewById(R.id.layout_relative_files);
                    if(!checkBox.isChecked()) {
                        relativeLayout.removeView(fab);
                    } else {
                        RelativeLayout.LayoutParams rel = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        rel.setMargins(15, 15, 15, 15);
                        rel.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        rel.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        fab.setLayoutParams(rel);
                        fab.setImageResource(android.R.drawable.ic_dialog_email);
                        fab.setSize(FloatingActionButton.SIZE_NORMAL);
                        relativeLayout.removeView(fab);
                        relativeLayout.addView(fab);
                    }
                    relativeLayout.refreshDrawableState();
                }
            });
            //checkBox.setBackgroundColor(toogleColor());
        }
        return convertView;
    }


    private int toogleColor(){
        if(color == Color.BLACK) {
            color = Color.DKGRAY;
        } else {
            color = Color.BLACK;
        }
        return color;
    }



}
