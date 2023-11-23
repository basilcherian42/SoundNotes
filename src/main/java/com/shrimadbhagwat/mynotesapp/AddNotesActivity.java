package com.shrimadbhagwat.mynotesapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.Manifest;

import android.content.pm.PackageManager;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.content.Context;
import android.media.AudioManager;

import android.widget.CompoundButton;
import android.widget.ProgressBar;

import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.ArrayList;

import android.os.Handler;

public class AddNotesActivity extends Activity implements View.OnClickListener,RecognitionListener{

    private ProgressBar progressBar;



    private Handler handler = new Handler();
    private static final long RESTART_DELAY_MS = 100;
    private static final int REQUEST_RECORD_PERMISSION = 100;

    private ToggleButton toggleButton;
    private ToggleButton toggleButton2;

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private AudioManager audioManager;
    private String LOG_TAG = "VoiceRecognitionActivity";







    private Button addButton;
    ImageView backButton;
    public String folderName;
    private EditText subjectEditText,returnedText;
    private DBManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Record");
        setContentView(R.layout.activity_add_notes_acrivity);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        progressBar = findViewById(R.id.progress1);
        subjectEditText = findViewById(R.id.subject_edittext);
        returnedText = findViewById(R.id.description_edittext);
        addButton = findViewById(R.id.add_record_btn);
        Intent intent=getIntent();
        folderName=intent.getStringExtra("key2");


        dbManager = new DBManager(this);
        dbManager.open(folderName);
        addButton.setOnClickListener(this);
        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity();
            }
        });



        progressBar.setIndeterminate(true);

        toggleButton = findViewById(R.id.toggleButton1);
        toggleButton2 = findViewById(R.id.toggleButton2);
        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"US-en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");



        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {



                    toggleButton.setChecked(false);
                    toggleButton2.setBackgroundColor(Color.parseColor("#ffa31a"));

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unmute();
                        }
                    }, 1000);


                } else if (isChecked) {
                    mute();
                    toggleButton.setChecked(true);
                    toggleButton2.setBackgroundColor(Color.GREEN);
                }
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    ActivityCompat.requestPermissions (AddNotesActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });
    }
    private void mute() {
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);

    }

    private void unmute() {

        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);

    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_record_btn:
                final String name = subjectEditText.getText().toString();
                final String desc = returnedText.getText().toString();
                dbManager.insert(name,desc);

                //Intent main = new Intent(AddNotesActivity.this, MainActivity.class)
                        //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(main);
                Intent intent=new Intent(this,SecondActivity.class);
                intent.putExtra("key",folderName);
                startActivity(intent);
                break;
        }
    }
    public void changeActivity() {
        //startActivity(new Intent(this, MainActivity.class));
        Intent intent=new Intent(this,SecondActivity.class);
        intent.putExtra("key",folderName);
        startActivity(intent);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(AddNotesActivity.this, "Permission Denied!", Toast .LENGTH_SHORT).show();
                }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }
    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }
    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }
    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);
    }
    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(toggleButton2.isChecked() == true) {
                    toggleButton.setChecked(true);
                }
            }
        }, RESTART_DELAY_MS);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }
    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }
    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }
    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        // Get the current text from the TextView
        String currentText = returnedText.getText().toString();

        // Iterate through the recognized results and append them to the current text

        currentText += matches.get(0) + "." + "\n"; // Append the recognized text with a newline character


        // Set the updated text in the TextView
        returnedText.setText(currentText);

        // Automatically turn on the ToggleButton after a delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(toggleButton2.isChecked() == true) {
                    toggleButton.setChecked(true);
                }
            }
        }, RESTART_DELAY_MS);
    }
    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }
    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SpannableString spannableMessage = new SpannableString("Are you sure you want to exit?");
        spannableMessage.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableMessage.length(), 0);

        builder.setMessage(spannableMessage);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // If the user clicks "Yes", close the activity
                finish();
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
}