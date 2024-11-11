package com.example.reto.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.example.reto.R;
import com.example.reto.model.Ejercicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBAccess implements DBAccesible {

    private SQLiteDatabase dataBase;

    private String crearTablaGrupo = "CREATE TABLE IF NOT EXISTS grupo (id Integer PRIMARY KEY, nombre varchar(30) NOT NULL)";
    private String crearTablaEjercicio = "CREATE TABLE IF NOT EXISTS ejercicio (nombre varchar(30) PRIMARY KEY, idGrupo Integer, descripcion Text, repeticiones Integer, series Integer, imagen Text, video Text, audio Text, FOREIGN KEY (idGrupo) REFERENCES grupo(id) ON DELETE CASCADE);";
    private String selectIdGrupo = "SELECT id FROM grupo WHERE nombre = ?";
    private String selectNombreGrupo = "SELECT nombre FROM grupo WHERE id = ?";
    private String selectGrupos = "SELECT nombre FROM grupo";
    private String selectEjercicioPorGrupo = "SELECT * FROM ejercicio WHERE idGrupo = (SELECT id FROM grupo WHERE nombre = ?)";
    private String insertGrupos = "INSERT INTO grupo(nombre) VALUES ('brazo'),('pierna'),('pecho'),('espalda')";
    private String insertEjercicio = "INSERT INTO ejercicio (nombre, idGrupo, descripcion, repeticiones, series, imagen, video, audio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private Context context;

    public DBAccess(Context context) {
        this.context = context;
        try {
            dataBase = context.openOrCreateDatabase("BDEjercicios", Context.MODE_PRIVATE, null);
            dataBase.execSQL(crearTablaGrupo);
            dataBase.execSQL(crearTablaEjercicio);
            if(!HayGrupos())
                dataBase.execSQL(insertGrupos);

        } catch (SQLiteException e) {
            Toast.makeText(context, R.string.txtErrorCrearBD, Toast.LENGTH_LONG).show();
        }
    }

    private boolean HayGrupos() {
        Cursor cursor = null;
        cursor = dataBase.rawQuery(selectGrupos, null);
        if(cursor.getCount() == 0){
            return false;
        }else{
            return true;
        }

    }

    @Override
    public List<String> getGrupos() {
        List<String> grupos = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = dataBase.rawQuery(selectGrupos, null);
            if(cursor.getCount() != 0){
                while (cursor.moveToNext()){
                    grupos.add(cursor.getString(0));
                }
            }
        }catch(SQLiteException e){
            Toast.makeText(context, "Error al recoger los datos", Toast.LENGTH_LONG).show();
        }finally {
            if(cursor != null){
                cursor.close();
            }

        }

        return grupos;
    }

    @Override
    public Map<String, Ejercicio> getEjerciciosGrupoMuscular(String nombre){
        Map<String, Ejercicio> ejercicios = new HashMap<>();
        Cursor cursor = null;
        try{
            cursor = dataBase.rawQuery(selectEjercicioPorGrupo, new String[]{nombre});
            if(cursor.getCount() != 0){
                while(cursor.moveToNext()){
                    Ejercicio ejer = new Ejercicio();
                    ejer.setNombre(cursor.getString(0));
                    ejer.setGrupo(getNombreGrupo(Integer.parseInt(cursor.getString(1))));
                    ejer.setDescripcion(cursor.getString(2));
                    ejer.setRepeticiones(Integer.parseInt(cursor.getString(3)));
                    ejer.setSeries(Integer.parseInt(cursor.getString(4)));
                    ejer.setImagen(cursor.getString(5));
                    ejer.setVideo(cursor.getString(6));
                    ejer.setAudio(cursor.getString(7));
                    ejercicios.put(ejer.getNombre(),ejer);
                }
            }
        }catch(SQLiteException e){
            Toast.makeText(context, "Error al recoger los datos", Toast.LENGTH_LONG).show();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }

        return ejercicios;
    }

    @Override
    public void setEjercicio(Ejercicio ejer){
        dataBase.execSQL(insertEjercicio, new Object[]{
                ejer.getNombre(),
                getIdGrupo(ejer.getGrupo()),
                ejer.getDescripcion(),
                ejer.getRepeticiones(),
                ejer.getSeries(),
                ejer.getImagen(),
                ejer.getVideo(),
                ejer.getAudio()
        });

    }

    private Integer getIdGrupo(String grupo){
        Integer id = null;
        Cursor cursor = null;
        try{
            cursor = dataBase.rawQuery(selectIdGrupo, new String[]{grupo});
            if(cursor.getCount() != 0){
                cursor.moveToNext();
                id = Integer.parseInt(cursor.getString(0));
            }
        }catch(SQLiteException e){
            Toast.makeText(context, "Error al recoger los datos", Toast.LENGTH_LONG).show();
        }finally {
            if(cursor != null){
                cursor.close();
            }

        }

        return id;
    }


    private String getNombreGrupo(Integer id){
        String nombre = null;
        Cursor cursor = null;
        try{
            cursor = dataBase.rawQuery(selectNombreGrupo, new String[]{String.valueOf(id)});
            if(cursor.getCount() != 0){
                cursor.moveToNext();
                nombre = cursor.getString(0);
            }
        }catch(SQLiteException e){
            Toast.makeText(context, "Error al recoger los datos", Toast.LENGTH_LONG).show();
        }finally {
            if(cursor != null){
                cursor.close();
            }

        }
        return nombre;
    }

}
