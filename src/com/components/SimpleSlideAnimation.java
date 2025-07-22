package com.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SimpleSlideAnimation {

    public static void slide(JPanel container, CardLayout layout, String nextPanelName, String direction) {
        int width = container.getWidth();
        int height = container.getHeight();

        Component current = null;
        for (Component comp : container.getComponents()) {
            if (comp.isVisible()) {
                current = comp;
                break;
            }
        }

        if (current == null) return;

        layout.show(container, nextPanelName);

        Component next = null;
        for (Component comp : container.getComponents()) {
            if (comp.isVisible()) {
                next = comp;
                break;
            }
        }

        if (next == null) return;

        BufferedImage imgCurrent = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        current.paint(imgCurrent.getGraphics());

        BufferedImage imgNext = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        next.paint(imgNext.getGraphics());

        JWindow animationWindow = new JWindow();
        animationWindow.setSize(width, height);
        animationWindow.setLocation(container.getLocationOnScreen());

        AnimationPanel panel = new AnimationPanel(imgCurrent, imgNext);
        animationWindow.add(panel);
        animationWindow.setVisible(true);

        new Thread(() -> {
            try {
                int step = 20;
                int delay = 10;

                if (direction.equalsIgnoreCase("left")) {
                    for (int i = 0; i <= width; i += step) {
                        panel.setOffsetX(-i);
                        panel.repaint();
                        Thread.sleep(delay);
                    }
                } else if (direction.equalsIgnoreCase("right")) {
                    for (int i = 0; i <= width; i += step) {
                        panel.setOffsetX(i);
                        panel.repaint();
                        Thread.sleep(delay);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                animationWindow.setVisible(false);
                animationWindow.dispose();
            }
        }).start();
    }

    private static class AnimationPanel extends JPanel {
        private BufferedImage currentImg;
        private BufferedImage nextImg;
        private int offsetX = 0;

        public AnimationPanel(BufferedImage currentImg, BufferedImage nextImg) {
            this.currentImg = currentImg;
            this.nextImg = nextImg;
        }

        public void setOffsetX(int offsetX) {
            this.offsetX = offsetX;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (offsetX < 0) {
                g.drawImage(currentImg, offsetX, 0, null);
                g.drawImage(nextImg, offsetX + currentImg.getWidth(), 0, null);
            } else {
                g.drawImage(nextImg, offsetX - nextImg.getWidth(), 0, null);
                g.drawImage(currentImg, offsetX, 0, null);
            }
        }
    }
}
