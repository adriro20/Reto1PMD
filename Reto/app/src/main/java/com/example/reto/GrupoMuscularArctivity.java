package com.example.reto;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GrupoMuscularArctivity extends AppCompatActivity {
    private Button btnSalir;
    private Button btnVolver;
    private Spinner cbGrupo;
    private TableLayout tabla;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.grupo_muscular_arctivity);
        btnSalir = (Button) findViewById(R.id.btnSalir);
        btnVolver = (Button) findViewById(R.id.btnVolver);
        cbGrupo = (Spinner) findViewById(R.id.cboxMusculos);
        tabla = (TableLayout) findViewById(R.id.tablaEjer);
        cargarTabla();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void cargarTabla() {

    }
}