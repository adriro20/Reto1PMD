package com.example.reto.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reto.R;

import java.io.File;

public class MostrarImagen extends AppCompatActivity {

    private ImageView ivMostrar;

    private Button btnSalir;
    private Button btnVolver;

    private String imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mostrar_imagen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this::cerrarApp);

        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(this::volverAtras);

        ivMostrar = findViewById(R.id.ivImagen);

        Intent intentRecoger = getIntent();
        imagen = intentRecoger.getStringExtra("IMAGEN");

        File archivoImagen = new File(getFilesDir(), "IMAGENES/" + imagen);
        if(archivoImagen.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(archivoImagen.getAbsolutePath());
            ivMostrar.setImageBitmap(bitmap);
        }else{
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