package com.example.reto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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

import com.example.reto.R;
import com.example.reto.controller.DBAccesible;
import com.example.reto.controller.DBAccess;
import com.example.reto.model.Ejercicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button btnSalir = null;
    Button btnAnadir = null;
    Button btnVer = null;
    Spinner comboGrupoMusc = null;
    private List<String> grupos = new ArrayList<>();
    private DBAccesible dao;
    TableLayout tablaEjercicios = null;
    private Map<String, Ejercicio> ejercicios = new HashMap<>();
    private String grupoSeleccionado;
    private final int anadirActivity = 1;
    private final int grupoActivity = 2;
    private final int mostrarActivity = 3;
    List<String> nombreGrupos = null;

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

        tablaEjercicios = (TableLayout) findViewById(R.id.tablaEjercicios);

        comboGrupoMusc = (Spinner) findViewById(R.id.comboGrupoMusc);
        cargarDatosEnCombo();
        comboGrupoMusc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //cargarTabla();
                String grupo = comboGrupoMusc.getSelectedItem().toString();
                if (grupo.equals(getText(R.string.txtSelecciona).toString())) {
                    int rowCount = tablaEjercicios.getChildCount();
                    if (rowCount > 1) {
                        for (int y = 1; y < rowCount; y++) {
                            tablaEjercicios.removeViewAt(1);
                        }
                    }
                    btnVer.setEnabled(false);
                }else {
                    cargarTabla(grupo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void cargarTabla(String grupo) {
        //limpiar los datos de la tabla
        int rowCount = tablaEjercicios.getChildCount();
        if (rowCount > 1) {
            for (int i = 1; i < rowCount; i++) {
                tablaEjercicios.removeViewAt(1);
            }
        }
        ejercicios = dao.getEjerciciosGrupoMuscular(grupo);
        if (ejercicios.isEmpty()) {
            btnVer.setEnabled(false);
            Toast.makeText(this, R.string.txtNoHayEjercicios, Toast.LENGTH_SHORT).show();
        } else {
            btnVer.setEnabled(true);
        for (Map.Entry<String, Ejercicio> ejer : ejercicios.entrySet()) {
            // Crear una fila
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            // Crear y rellenar la columna de nombre
            TextView txtNombre = new TextView(this);
            txtNombre.setText(ejer.getValue().getNombre());
            txtNombre.setHeight(180);
            txtNombre.setWidth(120);
            txtNombre.setGravity(Gravity.CENTER); // Centrar el texto
            txtNombre.setTextSize(18);
            row.addView(txtNombre);

            // Crear y rellenar la columna de series y repeticiones
            TextView txtSeries = new TextView(this);
            txtSeries.setText(ejer.getValue().getSeries() + " x " + ejer.getValue().getRepeticiones());
            txtSeries.setHeight(180);
            txtSeries.setWidth(120);
            txtSeries.setGravity(Gravity.CENTER);
            txtSeries.setTextSize(18);
            row.setBackgroundColor(getResources().getColor(R.color.white));
            row.setAlpha(0.7f);
            row.addView(txtSeries);
            row.setOnClickListener(this::irAEjer);

            // Agregar la fila al TableLayout
            tablaEjercicios.addView(row);
        }
        }
    }

    private void irAEjer(View view) {
        TableRow row = (TableRow) view;
        TextView txtNombre = (TextView) row.getChildAt(0);
        Ejercicio ej = new Ejercicio();

        ej = ejercicios.get(txtNombre.getText().toString());
        Intent intent = new Intent(MainActivity.this, MostrarEjercicioActivity.class);
        intent.putExtra("EJERCICIO", ej);
        startActivityForResult(intent, mostrarActivity);
    }

    private void cerrarApp(View view) {
        finishAffinity();
    }

    private void irAAnadir(View view) {
        Intent intent = new Intent(MainActivity.this, CrearEjercicioActivity.class);

        startActivityForResult(intent, anadirActivity);
    }

    private void irAGrupo(View view) {
        grupoSeleccionado = comboGrupoMusc.getSelectedItem().toString();
        if (!grupoSeleccionado.equals(getText(R.string.txtSelecciona).toString())) {
            Intent intent = new Intent(MainActivity.this, GrupoMuscularActivity.class);
            intent.putExtra("GRUPO", grupoSeleccionado);
            startActivityForResult(intent, grupoActivity);
        }else{
            Toast.makeText(this, R.string.txtSelecciona, Toast.LENGTH_SHORT).show();
        }

    }

    private void cargarDatosEnCombo() {
        nombreGrupos = dao.getGrupos();
        grupos.add(getText(R.string.txtSelecciona).toString());
        grupos.addAll(nombreGrupos);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboGrupoMusc.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        comboGrupoMusc.setSelection(0);
    }
}