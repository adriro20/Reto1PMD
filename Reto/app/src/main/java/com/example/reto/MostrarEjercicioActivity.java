package com.example.reto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MostrarEjercicioActivity extends AppCompatActivity {

    private Ejercicio ejercicio;

    private EditText etNombre;
    private EditText etGrupo;
    private EditText etSeries;
    private EditText etRepeticiones;
    private EditText etDescripcion;

    private Button btnSalir;
    private Button btnVolver;

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

        etNombre = findViewById(R.id.etNombre);
        etGrupo = findViewById(R.id.etGrupo);
        etSeries = findViewById(R.id.etSeries);
        etRepeticiones = findViewById(R.id.etRepeticiones);
        etDescripcion = findViewById(R.id.etDescripcion);

        btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this::cerrarApp);

        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(this::volverAtras);

        Intent recogerIntent = getIntent();
        ejercicio = (Ejercicio) recogerIntent.getSerializableExtra("EJERCICIO");
        if(ejercicio != null){
            etNombre.setText(ejercicio.getNombre());
            etGrupo.setText(ejercicio.getGrupo());
            etSeries.setText(ejercicio.getSeries());
            etRepeticiones.setText(ejercicio.getRepeticiones());
            etDescripcion.setText(ejercicio.getDescripcion());
        }else{
            Toast.makeText(this, "Ha sucedido un error al cargar el ejercicio seleccionado" ,
                    Toast.LENGTH_LONG).show();
            Intent intentError = new Intent();
            setResult(RESULT_CANCELED, intentError);
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