package com.example.reto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrearEjercicioActivity extends AppCompatActivity {

    private Ejercicio ejercicio;

    private EditText etNombre;
    private EditText etSeries;
    private EditText etRepeticiones;
    private EditText etDescripcion;

    private Spinner cbGrupo;

    private Button btnSalir;
    private Button btnVolver;
    private Button btnCrear;

    private ImageButton ibImagen;
    private ImageButton ibVideo;
    private ImageButton ibAudio;

    private List<String> grupos;

    private Boolean imgOK = false;
    private Boolean vidOK = false;
    private Boolean audOK = false;


    private DBAccesible dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_ejercicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dao = new DBAccess(this);

        etNombre = findViewById(R.id.etNombre);
        etSeries = findViewById(R.id.etSeries);
        etRepeticiones = findViewById(R.id.etRepeticiones);
        etDescripcion = findViewById(R.id.etDescripcion);

        cbGrupo = findViewById(R.id.cbGrupo);
        grupos = dao.getGrupos();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbGrupo.setAdapter(adapter);

        btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this::cerrarApp);

        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(this::volverAtras);

        btnCrear = findViewById(R.id.btnCrear);
        btnCrear.setOnClickListener(this::crearEjercicio);

        ibImagen = findViewById(R.id.ibImagen);
        ibImagen.setOnClickListener(this::subirImagen);

        ibVideo = findViewById(R.id.ibVideo);
        ibVideo.setOnClickListener(this::subirVideo);

        ibAudio = findViewById(R.id.ibAudio);
        ibAudio.setOnClickListener(this::subirAudio);

    }

    private void subirAudio(View view) {
        if(etNombre.getText().toString().isEmpty()){
            Toast.makeText(this, "Primero introduce el nombre del ejercicio" ,
                    Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String nombreArchivo = "AUD_"+etNombre.getText().toString();
            File dirAudio = new File(getFilesDir(), "Audios");
            if (!dirAudio.exists()) {
                dirAudio.mkdirs(); // Crea el directorio si no existe
            }

            File audio = new File(dirAudio, nombreArchivo + ".mp3");//DUDAS SOBRE EL FORMATO EN EL QUE SE GUARDAN LOS ARCHIVOS
            Uri uriAudio = FileProvider.getUriForFile(this, "com.example.reto.fileprovider", audio);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriAudio);
            startActivityForResult(intent, 103);
        }
    }

    private void subirVideo(View view) {
        if(etNombre.getText().toString().isEmpty()){
            Toast.makeText(this, "Primero introduce el nombre del ejercicio" ,
                    Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            String nombreArchivo = "VID_"+etNombre.getText().toString();
            File dirImg = new File(getFilesDir(), "Videos");
            if (!dirImg.exists()) {
                dirImg.mkdirs(); // Crea el directorio si no existe
            }

            File imagen = new File(dirImg, nombreArchivo + ".mp4");
            Uri uriVideo = FileProvider.getUriForFile(this, "com.example.reto.fileprovider", imagen);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriVideo);
            startActivityForResult(intent, 102);
        }
    }

    private void subirImagen(View view) {
        if(etNombre.getText().toString().isEmpty()){
            Toast.makeText(this, "Primero introduce el nombre del ejercicio" ,
                    Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String nombreArchivo = "IMG_"+etNombre.getText().toString();
            File dirImg = new File(getFilesDir(), "Imagenes");
            if (!dirImg.exists()) {
                dirImg.mkdirs(); // Crea el directorio si no existe
            }

            File imagen = new File(dirImg, nombreArchivo + ".jpg");
            Uri uriImagen = FileProvider.getUriForFile(this, "com.example.reto.fileprovider", imagen);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImagen);
            startActivityForResult(intent, 101);
        }
    }

    private void crearEjercicio(View view) {
        ejercicio = new Ejercicio();
        if(etNombre.getText().toString().isEmpty() || cbGrupo.getSelectedItem().equals(-1)
                || etSeries.getText().toString().isEmpty() || etRepeticiones.getText().toString().isEmpty()
                || etDescripcion.getText().toString().isEmpty() || !imgOK || !vidOK || !audOK){
            Toast.makeText(this, "Los campos tienen que estar llenos" ,
                    Toast.LENGTH_LONG).show();
        }else{
            if(etSeries.getText().toString().matches("\\d+") &&
                    etRepeticiones.getText().toString().matches("\\d+")){
                ejercicio.setNombre(etNombre.getText().toString());
                ejercicio.setGrupo(cbGrupo.getSelectedItem().toString());
                ejercicio.setSeries(Integer.parseInt(etSeries.getText().toString()));
                ejercicio.setRepeticiones(Integer.parseInt(etRepeticiones.getText().toString()));
                ejercicio.setDescripcion(etDescripcion.getText().toString());
                ejercicio.setImagen("IMG_"+etNombre.getText().toString());
                ejercicio.setVideo("VID_"+etNombre.getText().toString());
                ejercicio.setAudio("AUD_"+etNombre.getText().toString());

                dao.setEjercicio(ejercicio);
            }else{
                Toast.makeText(this, "Los campos de series y repeticiones tienen que " +
                        "ser numéricos" , Toast.LENGTH_LONG).show();
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 101:
                if(resultCode == RESULT_OK) {
                    imgOK = true;
                } else {
                    Toast.makeText(this, "Captura de imagen cancelada", Toast.LENGTH_SHORT).show();
                }
                break;
            case 102:
                if(resultCode == RESULT_OK) {
                    vidOK = true;
                } else {
                    Toast.makeText(this, "Captura de video cancelada", Toast.LENGTH_SHORT).show();
                }
                break;
            case 103:
                if(resultCode == RESULT_OK) {
                    audOK = true;
                } else {
                    Toast.makeText(this, "Captura de audio cancelada", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}