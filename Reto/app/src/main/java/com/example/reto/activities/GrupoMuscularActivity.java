package com.example.reto.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
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
    private final int mostrarActivity = 2;

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
        ejercicios = dao.getEjerciciosGrupoMuscular(grupo);

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


                // Crear y rellenar la columna de la imagen
                ImageView imagen = new ImageView(this);

                imagen.setPadding(10, 10, 10, 10); // Padding para hacer mÃ¡s grande la celda
                File archivoImagen = new File(getFilesDir(), "IMAGENES/" + ejer.getValue().getImagen());
                if(archivoImagen.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(archivoImagen.getAbsolutePath());
                    imagen.setImageBitmap(bitmap);
                }else{
                    Intent intentEnviar = new Intent();
                    setResult(RESULT_CANCELED, intentEnviar);
                    finish();
                }
                row.addView(imagen);



                // Crear y rellenar la columna de nombre
                TextView txtNombre = new TextView(this);
                txtNombre.setText(ejer.getValue().getNombre());
                txtNombre.setPadding(10, 10, 10, 10); // Padding para hacer mÃ¡s grande la celda
                txtNombre.setGravity(Gravity.CENTER); // Centrar el texto
                txtNombre.setTextSize(25);
                row.addView(txtNombre);

                // Crear y rellenar la columna de series y repeticiones
                TextView txtSeries = new TextView(this);
                txtSeries.setText(ejer.getValue().getSeries() + " x " + ejer.getValue().getRepeticiones());
                txtSeries.setPadding(10, 10, 10, 10); // Padding para hacer mÃ¡s grande la celda
                txtSeries.setGravity(Gravity.CENTER); // Centrar el texto
                txtSeries.setTextSize(25);
                row.setBackgroundColor(getResources().getColor(R.color.white));
                row.setAlpha(0.7f);
                row.addView(txtSeries);
                // Agregar el evento de clic a la fila
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        irVistaEjercicio(ejer.getValue());
                    }
                });

                // Agregar la fila al TableLayout
                tabla.addView(row);


            }
        }
    }

    private void irVistaEjercicio(Ejercicio ejercicioSeleccionado) {
        Intent intent = new Intent(GrupoMuscularActivity.this, MostrarEjercicioActivity.class);
        intent.putExtra("EJERCICIO", ejercicioSeleccionado);
        startActivityForResult(intent, mostrarActivity);
    }
}