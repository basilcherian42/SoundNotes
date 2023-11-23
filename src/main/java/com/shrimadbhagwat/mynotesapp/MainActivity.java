package com.shrimadbhagwat.mynotesapp;


import android.app.AlertDialog;
import android.content.Context;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AddFolderDialogFragment.AddFolderDialogListener{

    public List<String> folderContents = new ArrayList<>(Arrays.asList("Demo"));


    public RecyclerView recyclerView;
    private FolderAdapter folderAdapter;
    public FloatingActionButton folder_add;

    private TextView tv1;
    private int longClickedPosition;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (preferences.contains("folderContents")) {
            Set<String> folderContentsSet = preferences.getStringSet("folderContents", new HashSet<>());
            folderContents = new ArrayList<>(folderContentsSet);
        }
        folder_add=findViewById(R.id.folderadd);
        folderAdapter=new FolderAdapter(folderContents);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(folderAdapter);
        folder_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFolder();
            }
        });
        registerForContextMenu(recyclerView);
        Intent intent = getIntent();
        final String username = intent.getStringExtra("USERNAME_EXTRA");


        Button profileButton = findViewById(R.id.profileButton);
        Button logoutButton = findViewById(R.id.logoutButton); // Add this line

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                profileIntent.putExtra("msg_key", username);
                startActivity(profileIntent);
            }
        });


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLoginStatus(false);


                Intent mainIntent = new Intent(MainActivity.this, StartActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

        if (!isLoggedIn()) {
            Intent mainIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }
    public void addFolder()
    {
        AddFolderDialogFragment dialogFragment = new AddFolderDialogFragment();
        dialogFragment.setNoteInputListener(this);
        dialogFragment.show(getSupportFragmentManager(), "NoteInputDialog");
    }

    public void onFolderAdded(String folderaddname)
    {
        folderContents.add(folderaddname);
        Log.d("NotesListActivity", "Updated notesList: " + folderContents.toString());
        recyclerView.getAdapter().notifyDataSetChanged();

    }
    protected void onStop() {
        super.onStop();
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> folderContentsSet = new HashSet<>(folderContents);
        editor.putStringSet("folderContents", folderContentsSet);

        editor.apply();
    }
    private boolean isLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
    }

    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SpannableString spannableMessage = new SpannableString("Do you want to leave this app?");
        spannableMessage.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableMessage.length(), 0);

        builder.setMessage(spannableMessage);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.darker_gray)));
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.rgb(255, 165, 0));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.rgb(255, 165, 0));

    }
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                folderContents.remove(longClickedPosition);
                folderAdapter.notifyItemRemoved(longClickedPosition);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


}