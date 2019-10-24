package com.example.musicplay.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.musicplay.R;
import com.example.musicplay.domain.PLayList;
import com.example.musicplay.fragment.ListPlaylistFragment;
import com.example.musicplay.repository.DBRepository;

public class PlayListDialog extends DialogFragment {

    private ViewGroup view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(buildReproductionLists());
        builder.setMessage(R.string.addToList)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = (EditText)view.getChildAt(1);
                        String name = editText.getText().toString();
                        if(name != null && !name.trim().isEmpty()) {
                            DBRepository dbRepository = new DBRepository(getContext());
                            PLayList pLayList = new PLayList(name);
                            dbRepository.savePlayList(pLayList);
                            FragmentManager fragmentManager =((AppCompatActivity)getActivity()).getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.upFragment, new ListPlaylistFragment());
                            fragmentTransaction.commit();
                        }
                    }
                });
        return builder.create();
    }

    private ViewGroup buildReproductionLists() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = (ViewGroup) inflater.inflate(R.layout.play_list_create, null);
        return view;
    }
}
