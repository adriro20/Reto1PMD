package com.example.reto.controller;

import com.example.reto.model.Ejercicio;

import java.util.List;
import java.util.Map;

public interface DBAccesible {

    public Map<String, Ejercicio> getEjerciciosGrupoMuscular(String nombre);

    public List<String> getGrupos();

    public void setEjercicio(Ejercicio ejer);
}
