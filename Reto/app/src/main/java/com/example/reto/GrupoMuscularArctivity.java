package com.example.reto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GrupoMuscularArctivity extends AppCompatActivity {
    private Button btnSalir;
    private TextView titulo;
    private Button btnVolver;
    private Spinner cbGrupo;
    private TableLayout tabla;
    private Map<String, Ejercicio> ejercicios = new HashMap<>();
    private ArrayList<String> grupos = new ArrayList<>();
    private String grupo;
    private DBAccesible dao;
    private final int main = 1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.grupo_muscular_arctivity);

        //coger los datos que nos pasan desde la otra ventana
        Intent intent = getIntent();
        grupo = intent.getExtras().getString("GRUPO");

        ejercicios = dao.selectEjercicio();
        titulo.setText(grupo);
        btnSalir = (Button) findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        btnVolver = (Button) findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GrupoMuscularArctivity.this, MainActivity.class);
                startActivityForResult(intent,main);
            }
        });

        cbGrupo = (Spinner) findViewById(R.id.cboxMusculos);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbGrupo.setAdapter(adapter);
        cbGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cargarTabla();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //poner por defecto el seleccionado
        int defaultPosition = grupos.indexOf(grupo);
        cbGrupo.setSelection(defaultPosition);

        tabla = (TableLayout) findViewById(R.id.tablaEjer);


        cargarTabla();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    private void cargarTabla() {
        //limpiar los datos de la tabla
        tabla.removeAllViews();
        for(Map.Entry<String, Ejercicio> ejer : ejercicios.entrySet()){
            //creamos una fila
            TableRow row = new TableRow(this);

            //crear la imagen para ponerla en la primera columna
            ImageView imgEjer = new ImageView(this);


            //creamos y rellenamos la columna de nombre
            TextView txtNombre = new TextView(this);
            txtNombre.setText(ejer.getValue().getNombre());
            row.addView(txtNombre);

            //creamos y rellenamos la columna de sries y repeticiones
            TextView txtSeries = new TextView(this);
            txtSeries.setText(ejer.getValue().getSeries()+" x "+ejer.getValue().getRepeticiones());
            row.addView(txtSeries);

            // Agregar la fila al TableLayout
            tabla.addView(row);
        }
    }
}