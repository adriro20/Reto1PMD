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
import java.util.ArrayList;
import java.util.List;

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

    private List<String> nombreGrupos = null;
    private List<String> grupos = new ArrayList<>();

    private Boolean imgOK = false;
    private Boolean vidOK = false;
    private Boolean audOK = false;

    private DBAccesible dao;

    private static final int PEDIR_PERMISOS_AUDIO_Y_CAMARA = 3;
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

        etNombre = findViewById(R.id.tvNombre);
        etSeries = findViewById(R.id.tvSeries);
        etRepeticiones = findViewById(R.id.tvRepeticiones);
        etDescripcion = findViewById(R.id.tvDescripcion);

        cbGrupo = findViewById(R.id.tvGrupo);

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

        cargarDatosEnCombo();

        solicitarPermisos();
        
    }

    private void cargarDatosEnCombo() {
        nombreGrupos = dao.getGrupos();
        grupos.add((String) getText(R.string.txtSelecciona).toString());
        grupos.addAll(nombreGrupos);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbGrupo.setAdapter(adapter);
    }

    private void solicitarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Solicita ambos permisos en una sola llamada
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    PEDIR_PERMISOS_AUDIO_Y_CAMARA);
        }
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

    public void subirImagen(View view) {
        if (etNombre.getText().toString().isEmpty()) {
            Toast.makeText(this, "Primero introduce el nombre del ejercicio",
                    Toast.LENGTH_SHORT).show();
        } else {
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

    private void crearEjercicio(View view) {
        ejercicio = new Ejercicio();
        if(etNombre.getText().toString().isEmpty() || cbGrupo.getSelectedItem().equals(0)
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PEDIR_PERMISOS_AUDIO_Y_CAMARA) {
            boolean permisosCamara = false;
            boolean permisosAudio = false;

            // Revisa los resultados de ambos permisos
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.CAMERA)) {
                    permisosCamara = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                } else if (permissions[i].equals(Manifest.permission.RECORD_AUDIO)) {
                    permisosAudio = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }

            if (permisosCamara && permisosAudio) {
                // Ambos permisos fueron concedidos
                Toast.makeText(this, "Permisos de cámara y audio concedidos", Toast.LENGTH_SHORT).show();
            } else {
                // Al menos uno de los permisos fue denegado
                Toast.makeText(this, "Sin aceptar los permisos no se puede crear un ejercicio", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }
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