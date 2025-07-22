package com.estructuras;

public class Pila<T> {
    private static class NodoPila<T> {
        T dato;
        NodoPila<T> siguiente;

        NodoPila(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private NodoPila<T> cabeza;
    private int tamano;
    private final int capacidadMaxima;

    public Pila(int capacidadMaxima) {
        this.cabeza = null;
        this.tamano = 0;
        this.capacidadMaxima = capacidadMaxima;
    }

    public void push(T dato) {
        if (tamano >= capacidadMaxima) {
            // Eliminar el elemento más antiguo
            NodoPila<T> actual = cabeza;
            NodoPila<T> anterior = null;
            while (actual != null && actual.siguiente != null) {
                anterior = actual;
                actual = actual.siguiente;
            }
            if (anterior != null) {
                anterior.siguiente = null;
            } else {
                cabeza = null;
            }
            tamano--;
        }
        NodoPila<T> nuevo = new NodoPila<>(dato);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
        tamano++;
    }

    public T pop() {
        if (cabeza == null) {
            return null;
        }
        T dato = cabeza.dato;
        cabeza = cabeza.siguiente;
        tamano--;
        return dato;
    }

    public int getTamano() {
        return tamano;
    }
}