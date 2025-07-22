package com.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ScrollStyle extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(130, 130, 130, 180);  // Color del "agarre"
        trackColor = new Color(0, 0, 0, 0);          // Fondo transparente
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return crearBotonInvisible();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return crearBotonInvisible();
    }

    private JButton crearBotonInvisible() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        return button;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 10;
        int width = Math.min(8, thumbBounds.width);
        int x = thumbBounds.x + (thumbBounds.width - width) / 2;
        int y = thumbBounds.y;

        g2.setPaint(thumbColor);
        g2.fillRoundRect(x, y, width, thumbBounds.height, arc, arc);

        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // Fondo invisible (sin pista)
    }
}