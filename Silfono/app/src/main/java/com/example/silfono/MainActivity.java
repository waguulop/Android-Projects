package com.example.silfono;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sonido1(View view){
        mediaPlayer = MediaPlayer.create(this, R.raw.note1);
        mediaPlayer.start();
    }

    public void sonido2(View view){
        mediaPlayer = MediaPlayer.create(this, R.raw.note2);
        mediaPlayer.start();
    }

    public void sonido3(View view){
        mediaPlayer = MediaPlayer.create(this, R.raw.note3);
        mediaPlayer.start();
    }

    public void sonido4(View view){
        mediaPlayer = MediaPlayer.create(this, R.raw.note4);
        mediaPlayer.start();
    }

    public void sonido5(View view){
        mediaPlayer = MediaPlayer.create(this, R.raw.note5);
        mediaPlayer.start();
    }

    public void sonido6(View view){
        mediaPlayer = MediaPlayer.create(this, R.raw.note6);
        mediaPlayer.start();
    }

    public void sonido7(View view){
        mediaPlayer = MediaPlayer.create(this, R.raw.note7);
        mediaPlayer.start();
    }

}