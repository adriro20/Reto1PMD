package com.example.reto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
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

import java.util.List;
import java.util.Map;

public class GrupoMuscularActivity extends AppCompatActivity {
    private String grupo;
    private TextView tit;
    private Button btnSalir;
    private Button btnVolver;
    private Spinner cbGrupo;
    private TableLayout tabla;
    private Map<String, Ejercicio> ejercicios ;
    private List<String> grupos;
    private DBAccesible dao;
    private final int main = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grupo_muscular);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dao = new DBAccess(this);

        Intent intent = getIntent();
        if (intent.hasExtra("GRUPO")) {
            grupo = intent.getStringExtra("GRUPO");
        } else {
            grupo = "";
        }
        ejercicios = dao.getEjerciciosGrupoMuscular(grupo);
        tit = (TextView) findViewById(R.id.textTitulo);
        tit.setText(grupo);

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
                Intent intent = new Intent(GrupoMuscularActivity.this, MainActivity.class);
                startActivityForResult(intent,main);
            }
        });

        cbGrupo = (Spinner) findViewById(R.id.cboxMusculos);
        grupos = dao.getGrupos();
        cargarCombo();
        //poner por defecto el seleccionado
        for(int i = 0; i<grupos.size();i++){
            if(grupos.get(i).equalsIgnoreCase(grupo))
                cbGrupo.setSelection(i);
        }
        cbGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                grupo = cbGrupo.getSelectedItem().toString();
                cargarTabla();
                tit.setText(cbGrupo.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tabla = (TableLayout) findViewById(R.id.tablaEjer);

        cargarTabla();
    }

    private void cargarCombo() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbGrupo.setAdapter(adapter);
    }

    private void cargarTabla() {
        ejercicios.clear();
        ejercicios = dao.getEjerciciosGrupoMuscular(grupo);
        // Limpiar los datos de la tabla y dejar el encabezado
        int rowCount = tabla.getChildCount();
        if (rowCount > 1) {
            for (int i = 1; i < rowCount; i++) {
                tabla.removeViewAt(1);
            }
        }

        if (ejercicios.isEmpty()) {
            Toast.makeText(this, R.string.txtNoHayEjercicios, Toast.LENGTH_SHORT).show();
        } else {
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
                txtNombre.setHeight(150);
                txtNombre.setWidth(120);
                txtNombre.setGravity(Gravity.CENTER); // Centrar el texto
                txtNombre.setBackgroundResource(R.drawable.cell_border);
                txtNombre.setTextSize(18);
                row.addView(txtNombre);

                // Crear y rellenar la columna de series y repeticiones
                TextView txtSeries = new TextView(this);
                txtSeries.setText(ejer.getValue().getSeries() + " x " + ejer.getValue().getRepeticiones());
                txtSeries.setHeight(150);
                txtSeries.setWidth(120);
                txtSeries.setGravity(Gravity.CENTER);
                txtSeries.setBackgroundResource(R.drawable.cell_border);
                txtSeries.setTextSize(18);
                row.addView(txtSeries);

                // Agregar la fila al TableLayout
                tabla.addView(row);
            }
        }
    }
}