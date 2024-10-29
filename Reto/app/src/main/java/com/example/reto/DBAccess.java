package com.example.reto;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

public class DBAccess implements DBAccesible {

    private SQLiteDatabase dataBase;
    private String crearTablas = "CREATE TABLE IF NOT EXISTS ejercicio (nombre varchar(30) PRIMARY KEY, grupo varchar(30), descripcion Text, repeticiones Integer, series Integer, imagen Text, video Text, audio Text);";
    private String select = "SELECT * FROM ejercicio";
    private String insert = "INSERT INTO ejercicio (nombre, grupo, descripcion, repeticiones, series, imagen, video, audio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public DBAccess(Context context) {
        try {
            dataBase = context.openOrCreateDatabase("BDEjercicios", Context.MODE_PRIVATE, null);

            dataBase.execSQL(crearTablas);

        } catch (SQLiteException e) {
            Toast.makeText(context, R.string.txtErrorCrearBD, Toast.LENGTH_LONG).show();
        }
    }

    public Map<String, Ejercicio> selectEjercicio(){
        Map<String, Ejercicio> ejercicios = new HashMap<>();
        Cursor cursor = dataBase.rawQuery(select, null);
        if(cursor.getCount() != 0){
            while(cursor.moveToNext()){
                Ejercicio ejer = new Ejercicio();
                ejer.setNombre(cursor.getString(0));
                ejer.setGrupo(cursor.getString(1));
                ejer.setDescripcion(cursor.getString(2));
                ejer.setRepeticiones(Integer.parseInt(cursor.getString(3)));
                ejer.setSeries(Integer.parseInt(cursor.getString(4)));
                ejer.setImagen(cursor.getString(5));
                ejer.setVideo(cursor.getString(6));
                ejer.setAudio(cursor.getString(7));
                ejercicios.put(ejer.getNombre(),ejer);
            }
        }

        return ejercicios;
    }

    public void insertEjercicio(Ejercicio ejer){
        dataBase.execSQL(insert, new Object[]{
                ejer.getNombre(),
                ejer.getGrupo(),
                ejer.getDescripcion(),
                ejer.getRepeticiones(),
                ejer.getSeries(),
                ejer.getImagen(),
                ejer.getVideo(),
                ejer.getAudio()
        });


    }


}
