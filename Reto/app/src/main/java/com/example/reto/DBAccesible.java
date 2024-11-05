package com.example.reto;

import com.example.reto.Ejercicio;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public interface DBAccesible {

    public Map<String, Ejercicio> getEjerciciosGrupoMuscular(String nombre);

    public List<String> getGrupos();

    public void setEjercicio(Ejercicio ejer);
}
