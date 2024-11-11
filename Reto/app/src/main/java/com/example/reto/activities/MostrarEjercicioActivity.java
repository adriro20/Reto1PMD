package com.example.reto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reto.R;
import com.example.reto.model.Ejercicio;

public class MostrarEjercicioActivity extends AppCompatActivity {

    private Ejercicio ejercicio;

    private TextView tvNombre;
    private TextView tvGrupo;
    private TextView tvSeries;
    private TextView tvRepeticiones;
    private TextView tvDescripcion;

    private Button btnSalir;
    private Button btnVolver;

    private ImageButton btnImagen;
    private ImageButton btnVideo;
    private ImageButton btnAudio;

    private final int MOSTRAR_IMAGEN = 1;
    private final int MOSTRAR_VIDEO = 2;
    private final int MOSTRAR_AUDIO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mostrar_ejercicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent recogerIntent = getIntent();
        this.ejercicio = (Ejercicio) recogerIntent.getSerializableExtra("EJERCICIO");

        tvNombre = findViewById(R.id.tvNombre);
        tvGrupo = findViewById(R.id.tvGrupo);
        tvSeries = findViewById(R.id.tvSeries);
        tvRepeticiones = findViewById(R.id.tvRepeticiones);
        tvDescripcion = findViewById(R.id.tvDescripcion);

        btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this::cerrarApp);

        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(this::volverAtras);

        btnImagen = findViewById(R.id.ibImagen);
        btnImagen.setOnClickListener(this::cargarImagen);

        btnVideo = findViewById(R.id.ibVideo);
        btnVideo.setOnClickListener(this::cargarVideo);

        btnAudio = findViewById(R.id.ibAudio);
        btnAudio.setOnClickListener(this::cargarAudio);

        if(ejercicio != null){
            tvNombre.setText(ejercicio.getNombre());
            tvGrupo.setText(ejercicio.getGrupo());
            tvSeries.setText(String.valueOf(ejercicio.getSeries()));
            tvRepeticiones.setText(String.valueOf(ejercicio.getRepeticiones()));
            tvDescripcion.setText(ejercicio.getDescripcion());
        }else{
            Toast.makeText(this, R.string.txtErrorCargarEjercicio ,
                    Toast.LENGTH_LONG).show();
            Intent intentError = new Intent();
            setResult(RESULT_CANCELED, intentError);
            finish();
        }

    }

    private void cargarAudio(View view) {
        Intent intent = new Intent(MostrarEjercicioActivity.this, MostrarAudio.class);
        intent.putExtra("AUDIO", ejercicio.getAudio());
        startActivityForResult(intent, MOSTRAR_AUDIO);
    }

    private void cargarVideo(View view) {
        Intent intent = new Intent(MostrarEjercicioActivity.this, MostrarVideo.class);
        intent.putExtra("VIDEO", ejercicio.getVideo());
        startActivityForResult(intent, MOSTRAR_VIDEO);
    }

    private void cargarImagen(View view) {
        Intent intent = new Intent(MostrarEjercicioActivity.this, MostrarImagen.class);
        intent.putExtra("IMAGEN", ejercicio.getImagen());
        startActivityForResult(intent, MOSTRAR_IMAGEN);
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
            case MOSTRAR_IMAGEN:
                if(resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, R.string.txtErrorCargarImagen, Toast.LENGTH_SHORT).show();
                }
                break;
            case MOSTRAR_VIDEO:
                if(resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, R.string.txtErrorCargarVideo , Toast.LENGTH_SHORT).show();
                }
                break;
            case MOSTRAR_AUDIO:
                if(resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, R.string.txtErrorCargarAudio , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}