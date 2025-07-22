package com.implementation;

import com.estructuras.ListaEnlazada;
import com.estructuras.Nodo;

public class ListaTareas extends ListaEnlazada<Tarea> {
    public NodoTareas agregarTarea(int idTarea, int idUsuario, String titulo, String descripcion) {
        Tarea tarea = new Tarea(idTarea, idUsuario, titulo, descripcion);
        NodoTareas nodo = new NodoTareas(tarea);
        super.agregar(tarea);
        return nodo;
    }

    public NodoTareas agregarTarea(String titulo) {
        return agregarTarea(0, 0, titulo, "");
    }


    public NodoTareas buscarPorIdTarea(int idTarea) {
        Nodo<Tarea> actual = getCabeza();
        while (actual != null) {
            NodoTareas nodo = (NodoTareas) actual;
            if (nodo.getDato().getIdTarea() == idTarea) {
                return nodo;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }


}