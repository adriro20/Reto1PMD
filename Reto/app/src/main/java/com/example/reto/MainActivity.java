package com.example.reto;

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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button btnSalir = null;
    Button btnAnadir = null;
    Button btnVer = null;
    Spinner comboGrupoMusc = null;
    private List<String> grupos;
    private DBAccesible dao;
    TableLayout tablaEjercicios = null;
    private Map<String, Ejercicio> ejercicios = new HashMap<>();
    private String grupoSeleccionado;
    private final int anadirActivity = 1;
    private final int grupoActivity = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dao = new DBAccess(this);

        btnSalir = (Button) findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this::cerrarApp);

        btnAnadir = (Button) findViewById(R.id.btnAnadir);
        btnAnadir.setOnClickListener(this::irAAnadir);

        btnVer = (Button) findViewById(R.id.btnVer);
        btnVer.setOnClickListener(this::irAGrupo);

        comboGrupoMusc = (Spinner) findViewById(R.id.comboGrupoMusc);
        cargarDatosEnCombo();
        comboGrupoMusc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //cargarTabla();
                grupoSeleccionado = comboGrupoMusc.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tablaEjercicios = (TableLayout) findViewById(R.id.tablaEjercicios);

    }

    private void cargarTabla() {
        //limpiar los datos de la tabla
        tablaEjercicios.removeAllViews();
        String grupo = comboGrupoMusc.getSelectedItem().toString();
        ejercicios = dao.getEjerciciosGrupoMuscular(grupo);
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
            tablaEjercicios.addView(row);
        }
    }

    private void cerrarApp(View view) {
        finishAffinity();
    }

    private void irAAnadir(View view) {
        Intent intent = new Intent(MainActivity.this, CrearEjercicioActivity.class);
        startActivityForResult(intent, anadirActivity);
    }

    private void irAGrupo(View view) {
        if (grupoSeleccionado != null && !grupoSeleccionado.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, GrupoMuscularActivity.class);
            intent.putExtra("GRUPO", grupoSeleccionado);
            startActivityForResult(intent, grupoActivity);
        }else{
            Toast.makeText(this, "Seleccione un grupo muscular", Toast.LENGTH_SHORT).show();
        }

    }

    private void cargarDatosEnCombo() {
        grupos = dao.getGrupos();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboGrupoMusc.setAdapter(adapter);
    }
}