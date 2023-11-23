package com.shrimadbhagwat.mynotesapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class AddFolderDialogFragment extends DialogFragment {
    public String folderName;
    public interface AddFolderDialogListener {
        void onFolderAdded(String folderName);
    }

    private AddFolderDialogListener mListener;
    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Enter Folder Name");

        // Set up the input field
        final EditText input = new EditText(requireActivity());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                folderName = input.getText().toString().trim();
                // Handle the folder name (e.g., add it to your data structure)
                if (!folderName.isEmpty() && mListener != null) {
                    mListener.onFolderAdded(folderName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel button clicked, do nothing or handle accordingly
            }
        });

        return builder.create();
    }
    public void setNoteInputListener(AddFolderDialogListener listener) {
        this.mListener = listener;
    }
}

