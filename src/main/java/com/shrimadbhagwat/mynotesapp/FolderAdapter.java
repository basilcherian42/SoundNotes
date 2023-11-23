package com.shrimadbhagwat.mynotesapp;


import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<String> folderContents;  // List of folder names

    public FolderAdapter(List<String> folderContents) {
        this.folderContents = folderContents;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        String folderName = folderContents.get(position);
        holder.folderName.setText(folderName);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SecondActivity.class);
            intent.putExtra("key", folderName);
            v.getContext().startActivity(intent);
        });
        holder.itemView.setOnLongClickListener(v -> {
            Log.d("LongClick", "Long click detected");
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Folder")
                    .setMessage("Do you want to delete this folder?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // User confirmed deletion
                        folderContents.remove(position);
                        notifyItemRemoved(position);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        if(folderContents!=null)
        {
            return folderContents.size();
        }
        return 0;
    }
    public void updateData(List<String> newData) {
        folderContents.clear();
        folderContents.addAll(newData);
        notifyDataSetChanged();
    }


    static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
        }
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, R.id.menu_delete, 0, "Delete");
        }
    }
}
