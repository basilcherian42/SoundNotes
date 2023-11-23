package com.shrimadbhagwat.mynotesapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 102;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_PROFILE_IMAGE = "profileImage";

    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.randomImage);
        registerForContextMenu(profileImage);

        // Retrieve the username from SharedPreferences
        String username = getSavedUsername();

        // Display the username in a TextView or any other UI element
        TextView welcomeMessage = findViewById(R.id.welcomeMessage);
        // Check if the username is not null before displaying
        if (username != null && !username.isEmpty()) {
            welcomeMessage.setText("Hello, " + username+"\n\nWelcome to SoundNotes");
        } else {
            welcomeMessage.setText("Hello, Guest");
        }

        // Load the profile image from SharedPreferences
        String imageString = getProfileImage();
        if (imageString != null) {
            displayProfileImage(imageString);
        }
    }

    private String getSavedUsername() {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        return preferences.getString("username", "");
    }

    private void displayProfileImage(String imageString) {
        if (imageString != null) {
            Bitmap bitmap = decodeBase64(imageString);
            if (bitmap != null) {
                profileImage.setImageBitmap(bitmap);
            } else {
                // Handle the case when the bitmap cannot be loaded
                profileImage.setImageResource(R.drawable.me2); // Set a default image or handle as needed
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case when the image string is null
            profileImage.setImageResource(R.drawable.me2); // Set a default image or handle as needed
        }
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.editPhoto) {
            if (requestPermission()) {
                openGallery();
            }
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private boolean requestPermission() {
        boolean request = true;
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        // Check if any permissions need to be requested
        if (permissions.length != 0) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            request = true;
        } else {
            toastMsg("Permissions Denied");
            println("Permissions not allowed by User...");
            request = false;
        }

        return request;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                InputStream imageStream = getContentResolver().openInputStream(data.getData());
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // Save the image to SharedPreferences
                String imageString = encodeToBase64(selectedImage);
                saveProfileImage(imageString);

                profileImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Handle permission results here if needed
        }
    }

    private void saveProfileImage(String imageString) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_PROFILE_IMAGE, imageString);
        editor.apply();
    }

    private String getProfileImage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_PROFILE_IMAGE, null);
    }

    private void toastMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void println(String message) {
        System.out.println(message);
    }

    private String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}