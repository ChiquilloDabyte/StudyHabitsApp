/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.components;

import com.database.GestorRegistro;
import com.estructuras.Pila;
import com.implementation.Tarea;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class HistorialAcciones {
    private final Pila<RegistroAccion> pila = new Pila<>(5); // Tamaño máximo 5

    public void registrarAccion(RegistroAccion accion) {
        if (accion.getTipo() == RegistroAccion.Tipo.ELIMINAR) {
            pila.push(accion); // Tu clase ya maneja el límite de 5 elementos
        }
    }

    public RegistroAccion deshacerUltimaEliminacion() {
        if (pila.getTamano() > 0) {
            return pila.pop();
        }
        return null;
    }

    public boolean hayAcciones() {
        return pila.getTamano() > 0;
    }

    public void agregarBotonDeshacer(JButton deshacerBtn, GestorRegistro gestorRegistro, VistaTareaDialog vistaTareaDialog) {
        deshacerBtn.setText("Deshacer eliminación");
        deshacerBtn.addActionListener(e -> {
            RegistroAccion accion = deshacerUltimaEliminacion();
            if (accion != null) {
                Tarea tareaRestaurada = accion.getTareaEliminada();
                // Restaurar la tarea eliminada
                int idRestaurada = gestorRegistro.agregarTarea(
                    tareaRestaurada.getIdUsuario(),
                    tareaRestaurada.getNombre(),
                    tareaRestaurada.getDescripcion(),
                    tareaRestaurada.getFechaEntrega()
                );
                vistaTareaDialog.dispose(); // Cerrar el diálogo para actualizar la vista
            } else {
                JOptionPane.showMessageDialog(vistaTareaDialog, "No hay acciones para deshacer.");
            }
        });
    }
}
