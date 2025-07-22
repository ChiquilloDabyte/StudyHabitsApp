package com.implementation;

import com.estructuras.Nodo;
import com.components.PanelTarea;

public class NodoTareas extends Nodo<Tarea> {
    private PanelTarea panelAsociado;

    public NodoTareas(Tarea tarea) {
        super(tarea);
    }

    public void setPanelAsociado(PanelTarea panel) {
        this.panelAsociado = panel;
    }

    public PanelTarea getPanelAsociado() {
        return panelAsociado;
    }
}

