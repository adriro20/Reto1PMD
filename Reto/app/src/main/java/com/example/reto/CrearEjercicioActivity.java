package com.example.reto;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_AUDIO_PERMISSION = 2;
    private static final int CAPTURA_IMAGEN = 101;
    private static final int CAPTURA_VIDEO = 102;
    private static final int CAPTURA_AUDIO = 103;

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

        solicitarPermisosCamara();
        solicitarPermisosAudio();

    }

    private void subirAudio(View view) {
        if(etNombre.getText().toString().isEmpty()){
            Toast.makeText(this, "Primero introduce el nombre del ejercicio" ,
                    Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            if (intent.resolveActivity(getPackageManager()) == null) {
                Toast.makeText(this, "No hay una aplicación de grabación de audio disponible", Toast.LENGTH_SHORT).show();
            } else {
                String nombreArchivo = "AUD_"+etNombre.getText().toString() + ".mp3";
                File directorio = new File(getFilesDir(), "Audios");
                if (!directorio.exists()) {
                    directorio.mkdirs(); // Crea el directorio si no existe
                }

                File audio = new File(directorio, nombreArchivo);
                Uri uriAudio = FileProvider.getUriForFile(this, "com.example.reto.fileprovider", audio);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriAudio);
                startActivityForResult(intent, CAPTURA_AUDIO);
            }
        }
    }

    private void subirVideo(View view) {
        if(etNombre.getText().toString().isEmpty()){
            Toast.makeText(this, "Primero introduce el nombre del ejercicio" ,
                    Toast.LENGTH_SHORT).show();
        }else{
            abrirCamara("VIDEO");
        }
    }

    public void subirImagen(View view) {
        if (etNombre.getText().toString().isEmpty()) {
            Toast.makeText(this, "Primero introduce el nombre del ejercicio",
                    Toast.LENGTH_SHORT).show();
        } else {
            abrirCamara("IMAGEN");
        }
    }

    private void solicitarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void solicitarPermisosAudio(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION);
        }
    }

    private void abrirCamara(String queHacer) {
        if(queHacer.equals("IMAGEN")){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String nombreArchivo = "IMG_" + etNombre.getText().toString() + ".jpg";

            File directorio = new File(getFilesDir(), "Imagenes");
            if (!directorio.exists()) {
                directorio.mkdirs(); // Crea el directorio si no existe
            }

            File imagen = new File(directorio, nombreArchivo);
            Uri uriImagen = FileProvider.getUriForFile(this, "com.example.reto.fileprovider", imagen);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImagen);
            startActivityForResult(intent, CAPTURA_IMAGEN);
        }else{
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            String nombreArchivo = "VID_" + etNombre.getText().toString() + ".mp4";

            File directorio = new File(getFilesDir(), "Videos");
            if (!directorio.exists()) {
                directorio.mkdirs(); // Crea el directorio si no existe
            }

            File video = new File(directorio, nombreArchivo);
            Uri uriVideo = FileProvider.getUriForFile(this, "com.example.reto.fileprovider", video);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriVideo);
            startActivityForResult(intent, CAPTURA_VIDEO);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de cámara aceptado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sin aceptar los permisos no se puede crear un ejercicio", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }
        if (requestCode == REQUEST_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de audio aceptado", Toast.LENGTH_SHORT).show();
            } else {
                // Permiso denegado, muestra un mensaje
                Toast.makeText(this, "Sin aceptar los permisos no se puede crear un ejercicio", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }
    }

    private void crearEjercicio(View view) {
        ejercicio = new Ejercicio();
        if(etNombre.getText().toString().isEmpty() || cbGrupo.getSelectedItem().equals(-1)
                || etSeries.getText().toString().isEmpty() || etRepeticiones.getText().toString().isEmpty()
                || etDescripcion.getText().toString().isEmpty()){
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
                if(imgOK){
                    ejercicio.setImagen("IMG_"+etNombre.getText().toString());
                }
                if(vidOK){
                    ejercicio.setVideo("VID_"+etNombre.getText().toString());
                }
                if(audOK){
                    ejercicio.setAudio("AUD_"+etNombre.getText().toString());
                }

                dao.setEjercicio(ejercicio);

                Toast.makeText(this, "Ejercicio guardado correctamente" , Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
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
            case CAPTURA_IMAGEN:
                if(resultCode == RESULT_OK) {
                    imgOK = true;
                } else {
                    Toast.makeText(this, "Captura de imagen cancelada", Toast.LENGTH_SHORT).show();
                }
                break;
            case CAPTURA_VIDEO:
                if(resultCode == RESULT_OK) {
                    vidOK = true;
                } else {
                    Toast.makeText(this, "Captura de video cancelada", Toast.LENGTH_SHORT).show();
                }
                break;
            case CAPTURA_AUDIO:
                if(resultCode == RESULT_OK) {
                    audOK = true;
                } else {
                    Toast.makeText(this, "Captura de audio cancelada", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}