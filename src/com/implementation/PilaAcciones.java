package com.implementation;

import javax.swing.*;
import java.util.Stack;

public class PilaAcciones {
    private Stack<Runnable> acciones;

    public PilaAcciones() {
        this.acciones = new Stack<>();
    }

    public void agregarAccion(Runnable accion) {
        acciones.push(accion);
    }

    public boolean deshacer(JFrame parent) {
        if (!acciones.isEmpty()) {
            try {
                Runnable accion = acciones.pop();
                accion.run();
                parent.revalidate();
                parent.repaint();
                return true;
            } catch (Exception e) {
                System.err.println("Error al deshacer acción: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
}