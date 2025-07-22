package com.estructuras;

public class ListaEnlazada<T> {
    private Nodo<T> cabeza;

    public ListaEnlazada() {
        cabeza = null;
    }

    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
    }

    public Nodo<T> buscar(T dato) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public void eliminar(T dato) {
        if (cabeza == null) return;

        if (cabeza.getDato().equals(dato)) {
            cabeza = cabeza.getSiguiente();
            return;
        }

        Nodo<T> anterior = cabeza;
        Nodo<T> actual = cabeza.getSiguiente();
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                anterior.setSiguiente(actual.getSiguiente());
                return;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
    }

    public Nodo<T> getCabeza() {
        return cabeza;
    }

    public void insertarAlInicio(Nodo<T> nuevo) {
        nuevo.setSiguiente(cabeza);
        cabeza = nuevo;
    }
}