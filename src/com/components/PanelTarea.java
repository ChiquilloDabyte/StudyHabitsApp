package com.components;

import com.database.GestorRegistro;
import com.implementation.Tarea;
import com.windowP.PrincipalWindow;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelTarea extends JPanel {
    private Tarea tarea;
    private GestorRegistro gestorRegistro;
    private PrincipalWindow parent;

    public PanelTarea(Tarea tarea, GestorRegistro gestorRegistro, PrincipalWindow parent) {
        this.tarea = tarea;
        this.gestorRegistro = gestorRegistro;
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBackground(new Color(0, 153, 153));
        setPreferredSize(new Dimension(180, 120));

        // Título (arriba)
        JLabel tituloLabel = new JLabel(tarea.getNombre());
        tituloLabel.setFont(new Font("Roboto Medium", Font.BOLD, 14));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tituloLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tituloLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                VistaTareaDialog dialog = new VistaTareaDialog(parent, tarea, gestorRegistro);
                dialog.setVisible(true);
            }
        });
        add(tituloLabel, BorderLayout.NORTH);

        // Panel central para descripción y fecha
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(0, 153, 153));
        
        // Descripción
        JLabel descripcionLabel = new JLabel("<html>" + tarea.getDescripcion() + "</html>");
        descripcionLabel.setFont(new Font("Roboto Medium", Font.PLAIN, 12));
        descripcionLabel.setForeground(Color.WHITE);
        descripcionLabel.setBackground(new Color(0, 153, 153));
        descripcionLabel.setOpaque(true);
        descripcionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Fecha
        JLabel fechaLabel = new JLabel();
        fechaLabel.setFont(new Font("Roboto Medium", Font.PLAIN, 11));
        fechaLabel.setForeground(Color.WHITE);
        if (tarea.getFechaEntrega() != null) {
            fechaLabel.setText("Entrega: " + tarea.getFechaEntrega());
        }
        fechaLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        centerPanel.add(descripcionLabel);
        centerPanel.add(fechaLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Botón de eliminar (abajo)
        PanelRedondeado eliminarBtn = new PanelRedondeado();
        eliminarBtn.setBackground(new Color(0, 153, 153));
        JLabel eliminarLabel = new JLabel("Eliminar");
        eliminarLabel.setFont(new Font("Roboto Medium", Font.BOLD, 12));
        eliminarLabel.setForeground(Color.WHITE);
        eliminarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        eliminarLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        eliminarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int option = JOptionPane.showConfirmDialog(
                    parent,
                    "¿Eliminar tarea " + tarea.getNombre() + "?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
                );
                if (option == JOptionPane.YES_OPTION) {
                    gestorRegistro.eliminarTarea(tarea.getIdTarea());
                    parent.actualizarTareas();
                }
            }
            @Override
            public void mouseEntered(MouseEvent evt) {
                eliminarBtn.setBackground(new Color(0, 102, 102));
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                eliminarBtn.setBackground(new Color(0, 153, 153));
            }
        });

        GroupLayout eliminarBtnLayout = new GroupLayout(eliminarBtn);
        eliminarBtn.setLayout(eliminarBtnLayout);
        eliminarBtnLayout.setHorizontalGroup(
            eliminarBtnLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(eliminarLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        eliminarBtnLayout.setVerticalGroup(
            eliminarBtnLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(eliminarLabel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
        );
        add(eliminarBtn, BorderLayout.SOUTH);
    }
}