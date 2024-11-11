package com.example.reto.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reto.R;

import java.io.File;

public class MostrarAudio extends AppCompatActivity {

    private Button btnSalir;
    private Button btnVolver;

    private String audio;

    private MediaController mediaController;
    private VideoView vvAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mostrar_audio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this::cerrarApp);

        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(this::volverAtras);

        Intent intentRecoger = getIntent();
        audio = intentRecoger.getStringExtra("AUDIO");

        vvAudio = findViewById(R.id.vvAudio);
        mediaController = new MediaController(this);

        vvAudio.setMediaController(mediaController);
        mediaController.setAnchorView(vvAudio);

        File archivoVideo = new File(getFilesDir(), "AUDIOS/" + audio);
        if (archivoVideo.exists()) {
            vvAudio.setVideoURI(Uri.fromFile(archivoVideo));

            vvAudio.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaController.show(20000);
                    vvAudio.start();
                }
            });

            vvAudio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaController.show(20000);
                }
            });
        } else{
            Intent intentEnviar = new Intent();
            setResult(RESULT_CANCELED, intentEnviar);
            finish();
        }
    }

    private void cerrarApp(View view) {
        finishAffinity();
    }

    private void volverAtras(View view){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}