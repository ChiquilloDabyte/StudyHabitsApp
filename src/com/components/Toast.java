package com.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Toast extends JDialog {
    private static final int FADE_STEP = 10;
    private static final int FADE_DELAY = 20;
    private static final int DISPLAY_TIME = 2000;
    private static final Color BACKGROUND_COLOR = new Color(0, 153, 153);
    private static final float BACKGROUND_OPACITY = 0.9f;

    public Toast(JFrame parent, String message) {
        super(parent);
        setUndecorated(true);
        setSize(200, 40);
        setLocationRelativeTo(parent);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 0));

        // Panel con bordes redondeados
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, BACKGROUND_OPACITY));
                g2d.setColor(BACKGROUND_COLOR);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        // Etiqueta del mensaje
        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Roboto Medium", Font.PLAIN, 12));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(label, BorderLayout.CENTER);

        add(panel);

        // Posicionar el toast en la parte inferior
        Point parentLocation = parent.getLocation();
        Dimension parentSize = parent.getSize();
        int x = parentLocation.x + (parentSize.width - getWidth()) / 2;
        int y = parentLocation.y + parentSize.height - getHeight() - 50;
        setLocation(x, y);
    }

    public static void mostrar(JFrame parent, String message) {
        SwingUtilities.invokeLater(() -> {
            Toast toast = new Toast(parent, message);
            toast.setOpacity(0.0f);
            toast.setVisible(true);

            // Animación de entrada (fade in)
            Timer fadeInTimer = new Timer(FADE_DELAY, null);
            fadeInTimer.addActionListener(e -> {
                float opacity = toast.getOpacity();
                opacity = Math.min(opacity + 0.1f, 1.0f);
                toast.setOpacity(opacity);
                if (opacity >= 1.0f) {
                    fadeInTimer.stop();
                    // Temporizador para mantener visible
                    Timer displayTimer = new Timer(DISPLAY_TIME, e2 -> {
                        // Animación de salida (fade out)
                        Timer fadeOutTimer = new Timer(FADE_DELAY, null);
                        fadeOutTimer.addActionListener(e3 -> {
                            float currentOpacity = toast.getOpacity();
                            currentOpacity = Math.max(currentOpacity - 0.1f, 0.0f);
                            toast.setOpacity(currentOpacity);
                            if (currentOpacity <= 0.0f) {
                                fadeOutTimer.stop();
                                toast.dispose();
                            }
                        });
                        fadeOutTimer.start();
                    });
                    displayTimer.setRepeats(false);
                    displayTimer.start();
                }
            });
            fadeInTimer.start();
        });
    }
}
