package com.example.reto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button btnSalir = null;
    Button btnAnadir = null;
    Button btnVer = null;
    Spinner comboGrupoMusc = null;
    TableLayout tablaEjercicios = null;
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
        btnSalir = (Button) findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(this::cerrarApp);
        btnAnadir = (Button) findViewById(R.id.btnAnadir);
        btnAnadir.setOnClickListener(this::irAAnadir);
        btnVer = (Button) findViewById(R.id.btnVer);
        btnVer.setOnClickListener(this::irAGrupo);
        comboGrupoMusc = (Spinner) findViewById(R.id.comboGrupoMusc);
        tablaEjercicios = (TableLayout) findViewById(R.id.tablaEjercicios);
        cargarDatosEnCombo();
    }

    private void cerrarApp(View view) {
        finishAffinity();
    }

    private void irAAnadir(View view) {
        Intent intent = new Intent(MainActivity.this, CrearEjercicioActivity.class);
        startActivityForResult(intent, anadirActivity);
    }

    private void irAGrupo(View view) {
        Intent intent = new Intent(MainActivity.this, GrupoMuscularActivity.class);
        startActivityForResult(intent, grupoActivity);
    }

    private void cargarDatosEnCombo() {
        
    }
}