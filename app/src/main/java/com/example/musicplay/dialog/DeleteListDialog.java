package com.example.musicplay.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.musicplay.R;
import com.example.musicplay.domain.PLayList;
import com.example.musicplay.fragment.ListPlayListsFragment;
import com.example.musicplay.repository.DBRepository;

public class DeleteListDialog extends DialogFragment {

    private PLayList pLayList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.deleteFromList)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(pLayList != null) {
                            DBRepository dbRepository = new DBRepository(getActivity());
                            dbRepository.deletePlayList(pLayList);
                            FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.upFragment, new ListPlayListsFragment());
                            fragmentTransaction.commit();
                        }
                    }
                });
        return builder.create();
    }

    public void setPlayList(PLayList playList) {
        this.pLayList = playList;
    }

}
