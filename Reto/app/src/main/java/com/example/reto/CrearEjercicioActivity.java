package com.example.reto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CrearEjercicioActivity extends AppCompatActivity {

    private Ejercicio ejercicio;

    private EditText etNombre;
    private EditText etGrupo;
    private EditText etSeries;
    private EditText etRepeticiones;
    private EditText etDescripcion;

    private Button btnSalir;
    private Button btnVolver;
    private Button btnCrear;

    private DBAccess dao;

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


        etNombre = findViewById(R.id.etNombre);
        etGrupo = findViewById(R.id.etGrupo);
        etSeries = findViewById(R.id.etSeries);
        etRepeticiones = findViewById(R.id.etRepeticiones);
        etDescripcion = findViewById(R.id.etDescripcion);

        btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this::cerrarApp);

        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(this::volverAtras);

        btnCrear = findViewById(R.id.btnCrear);
        btnCrear.setOnClickListener(this::crearEjercicio);

        Intent intent = getIntent();
        ejercicio = (Ejercicio) intent.getSerializableExtra("EJERCICIO");
        
    }

    private void crearEjercicio(View view) {
        try{
            etNombre.setText(ejercicio.getNombre());
            etGrupo.setText(ejercicio.getGrupo());
            etSeries.setText(ejercicio.getSeries());
            etRepeticiones.setText(ejercicio.getRepeticiones());
            etDescripcion.setText(ejercicio.getDescripcion());

            dao.setEjercicio(ejercicio);
        }catch(NullPointerException e){
            e.printStackTrace();
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