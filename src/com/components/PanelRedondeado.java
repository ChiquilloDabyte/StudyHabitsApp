package com.components;  // Ajusta esto según tu proyecto

import javax.swing.*;
import java.awt.*;

public class PanelRedondeado extends JPanel {
    private int cornerRadius = 30;

    public PanelRedondeado() {
        setOpaque(false);       // Necesario para que se vean las esquinas redondeadas
        setName(" ");           // Evita que se muestre texto en el editor GUI
        setToolTipText("Panel redondeado");  // Opcional: muestra algo útil al pasar el mouse
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.dispose();
    }

    @Override
    public String toString() {
        return " ";  // Esto evita que aparezca el nombre largo en el editor visual
    }
}