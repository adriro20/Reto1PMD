package com.example.reto.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reto.R;
import com.example.reto.controller.DBAccesible;
import com.example.reto.controller.DBAccess;
import com.example.reto.model.Ejercicio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CrearEjercicioActivity extends AppCompatActivity {

    private Ejercicio ejercicio;

    private boolean grabando = false;

    private MediaRecorder recorder;

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

    private Button btnAudio;

    private List<String> nombreGrupos = null;
    private List<String> grupos = new ArrayList<>();

    private Boolean imgOK = false;
    private Boolean vidOK = false;
    private Boolean audOK = false;

    private DBAccesible dao;

    private Bitmap imagenTemporal;

    private Uri uriVideoTemporal;
    private Uri uriAudioTemporal;

    private File archivoAudioTemporal;

    private static final int PEDIR_PERMISOS_AUDIO_Y_CAMARA = 3;
    private static final int CAPTURA_IMAGEN = 101;
    private static final int CAPTURA_VIDEO = 102;

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

        btnAudio = findViewById(R.id.btnAudio);
        btnAudio.setOnClickListener(this::subirAudio);

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




    private void subirVideo(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, CAPTURA_VIDEO);
    }

    public void subirImagen(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURA_IMAGEN);

    }

    private void subirAudio(View view) {
        if(grabando){
            btnAudio.setText(R.string.txtGrabarAudio);
            recorder.stop();
            recorder.release();
            recorder = null;
            grabando = false;

            // Crear URI para el archivo grabado
            uriAudioTemporal = Uri.fromFile(archivoAudioTemporal);
            audOK = true;
            Toast.makeText(this, R.string.txtGrabacionTerminada, Toast.LENGTH_LONG).show();
        }else{
            btnAudio.setText(R.string.txtPararAudio);
            try {
                archivoAudioTemporal = File.createTempFile("audio", ".mp3", getCacheDir());
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(archivoAudioTemporal.getAbsolutePath());
                recorder.prepare();
                recorder.start();
                grabando = true;
                Toast.makeText(this, R.string.txtGrabando, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.txtErrorGrabacionAudio, Toast.LENGTH_SHORT).show();
                btnAudio.setText(R.string.txtGrabarAudio);
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

    private void crearEjercicio(View view) {
        ejercicio = new Ejercicio();
        if(etNombre.getText().toString().isEmpty() || cbGrupo.getSelectedItem().equals(0)
                || etSeries.getText().toString().isEmpty() || etRepeticiones.getText().toString().isEmpty()
                || etDescripcion.getText().toString().isEmpty() || !imgOK || !vidOK || !audOK){
            Toast.makeText(this, R.string.txtCamposVacios ,
                    Toast.LENGTH_LONG).show();
        }else{
            ejercicio.setNombre(etNombre.getText().toString());
            ejercicio.setGrupo(cbGrupo.getSelectedItem().toString());
            ejercicio.setSeries(Integer.parseInt(etSeries.getText().toString()));
            ejercicio.setRepeticiones(Integer.parseInt(etRepeticiones.getText().toString()));
            ejercicio.setDescripcion(etDescripcion.getText().toString());
            if(imgOK){
                guardarImagen();
                ejercicio.setImagen("IMG_" + etNombre.getText().toString() + ".jpeg");
            }
            if(vidOK){
                guardarVideo();
                ejercicio.setVideo("VID_"+etNombre.getText().toString()+".mp4");
            }
            if(audOK){
                guardarAudio();
                ejercicio.setAudio("AUD_"+etNombre.getText().toString()+".mp3");
            }

            dao.setEjercicio(ejercicio);

            Toast.makeText(this, R.string.txtEjercicioGuardado , Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();

        }

    }

    private void guardarAudio() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // Obtener un InputStream desde el URI del video
            inputStream = getContentResolver().openInputStream(uriAudioTemporal);

            // Crear el archivo de destino en el almacenamiento interno
            File directory = new File(getFilesDir(), "AUDIOS"); // Crea un directorio llamado "videos"
            if (!directory.exists()) {
                directory.mkdirs(); // Si no existe, lo crea
            }

            // Nombre del archivo de video
            String audioName = "AUD_" + etNombre.getText().toString() + ".mp3";
            File outputFile = new File(directory, audioName);

            // Crear un OutputStream para escribir el video en el archivo de destino
            outputStream = new FileOutputStream(outputFile);

            // Copiar el contenido del InputStream al OutputStream (guardar el video)
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Cerrar streams
            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            Toast.makeText(this, R.string.txtErrorGuardarAudio, Toast.LENGTH_SHORT).show();
        } finally {
            // Asegurarse de cerrar los streams si algo falla
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void guardarVideo() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // Obtener un InputStream desde el URI del video
            inputStream = getContentResolver().openInputStream(uriVideoTemporal);

            // Crear el archivo de destino en el almacenamiento interno
            File directory = new File(getFilesDir(), "VIDEOS"); // Crea un directorio llamado "videos"
            if (!directory.exists()) {
                directory.mkdirs(); // Si no existe, lo crea
            }

            // Nombre del archivo de video
            String videoName = "VID_" + etNombre.getText().toString() + ".mp4";
            File outputFile = new File(directory, videoName);

            // Crear un OutputStream para escribir el video en el archivo de destino
            outputStream = new FileOutputStream(outputFile);

            // Copiar el contenido del InputStream al OutputStream (guardar el video)
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Cerrar streams
            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.txtErrorGuardarVideo, Toast.LENGTH_SHORT).show();
        } finally {
            // Asegurarse de cerrar los streams si algo falla
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void guardarImagen() {
        // Nombre del archivo que se va a guardar
        String nombreArchivo = "IMG_" + etNombre.getText().toString() + ".jpeg";

        // Crear el directorio "Imagenes" en el almacenamiento interno
        File directorio = new File(getFilesDir(), "IMAGENES");
        if (!directorio.exists()) {
            directorio.mkdirs(); // Si el directorio no existe, lo crea
        }

        // Crear el archivo donde se guardará la imagen
        File imagen = new File(directorio, nombreArchivo);

        try {
            // Abre un flujo de salida para el archivo
            FileOutputStream fos = new FileOutputStream(imagen);

            // Comprimir la imagen en formato JPEG con calidad 100 (sin pérdida)

            imagenTemporal.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // Cierra el flujo de salida
            fos.close();

        } catch (IOException e) {
            Toast.makeText(this, R.string.txtErrorGuardarImagen, Toast.LENGTH_SHORT).show();
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

            if (!permisosCamara || !permisosAudio) {
                // Al menos uno de los permisos fue denegado
                Toast.makeText(this, R.string.txtPermisosNoAceptados, Toast.LENGTH_SHORT).show();
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
                    Bundle extras = data.getExtras();
                    imagenTemporal = (Bitmap) extras.get("data");
                } else {
                    Toast.makeText(this, R.string.txtCapturaImagenCancelada, Toast.LENGTH_SHORT).show();
                }
                break;
            case CAPTURA_VIDEO:
                if(resultCode == RESULT_OK) {
                    vidOK = true;
                    uriVideoTemporal = data.getData();
                } else {
                    Toast.makeText(this, R.string.txtCapturaImagenCancelada, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}