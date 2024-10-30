package com.example.reto;

import com.example.reto.Ejercicio;

import java.util.Map;
import java.util.HashMap;

public interface DBAccesible {

    public Map<String, Ejercicio> getEjercicios();

    public void setEjercicio(Ejercicio ejer);

    public Map<Integer, String> getGrupos();

}
