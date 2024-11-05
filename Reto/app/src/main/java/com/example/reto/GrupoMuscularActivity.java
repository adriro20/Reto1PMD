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
            grupo = "";  // O maneja el caso de ausencia de datos
        }

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
                ejercicios = dao.getEjerciciosGrupoMuscular(cbGrupo.getSelectedItem().toString());
                cargarTabla();
                tit.setText(cbGrupo.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tabla = (TableLayout) findViewById(R.id.tablaEjer);
        ejercicios = dao.getEjerciciosGrupoMuscular(grupo);
        cargarTabla();
    }

    private void cargarCombo() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbGrupo.setAdapter(adapter);
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