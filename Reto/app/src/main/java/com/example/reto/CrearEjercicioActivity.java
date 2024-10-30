package com.example.reto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    private List<String> grupos;

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

                    //FALTAN AÑADIR IMAGEN VIDEO AUDIO

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
}