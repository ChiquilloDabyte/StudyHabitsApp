package com.components;

import java.awt.*;

public class WrapLayout extends FlowLayout {
    private Dimension preferredLayoutSize;

    public WrapLayout() {
        super(FlowLayout.LEFT, 10, 10);
    }

    public WrapLayout(int align) {
        super(align, 10, 10);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();
            boolean firstVisibleComponent = true;
            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int maxWidth = target.getWidth() - (insets.left + insets.right + hgap * 2);

            int x = 0, y = insets.top + vgap;
            int rowHeight = 0;

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    if (firstVisibleComponent) {
                        firstVisibleComponent = false;
                    } else {
                        x += hgap;
                    }
                    if (x + d.width > maxWidth && x > 0) {
                        x = 0;
                        y += vgap + rowHeight;
                        rowHeight = 0;
                    }
                    x += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                    dim.width = Math.max(dim.width, x);
                }
            }
            y += rowHeight;
            dim.width = Math.min(dim.width + insets.left + insets.right + hgap * 2, target.getWidth());
            dim.height = y + insets.top + insets.bottom + vgap * 2;

            preferredLayoutSize = dim;
            return dim;
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();
            boolean firstVisibleComponent = true;
            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int maxWidth = target.getWidth() - (insets.left + insets.right + hgap * 2);

            int x = 0, y = insets.top + vgap;
            int rowHeight = 0;

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getMinimumSize();
                    if (firstVisibleComponent) {
                        firstVisibleComponent = false;
                    } else {
                        x += hgap;
                    }
                    if (x + d.width > maxWidth && x > 0) {
                        x = 0;
                        y += vgap + rowHeight;
                        rowHeight = 0;
                    }
                    x += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                    dim.width = Math.max(dim.width, x);
                }
            }
            y += rowHeight;
            dim.width = Math.min(dim.width + insets.left + insets.right + hgap * 2, target.getWidth());
            dim.height = y + insets.top + insets.bottom + vgap * 2;

            return dim;
        }
    }
}