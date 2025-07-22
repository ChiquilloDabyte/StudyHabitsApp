/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.components;

import com.implementation.Tarea;

public class RegistroAccion {
    public enum Tipo { ELIMINAR }

    private final Tipo tipo;
    private final Tarea tareaEliminada;
    private final int idTareaPadre;

    public RegistroAccion(Tipo tipo, Tarea tareaEliminada, int idTareaPadre) {
        this.tipo = tipo;
        this.tareaEliminada = tareaEliminada;
        this.idTareaPadre = idTareaPadre;
    }

    public Tipo getTipo() { return tipo; }
    public Tarea getTareaEliminada() { return tareaEliminada; }
    public int getIdTareaPadre() { return idTareaPadre; }
}
