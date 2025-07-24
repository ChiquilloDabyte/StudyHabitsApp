package com.components;

import com.implementation.ListaTareas;
import com.implementation.NodoTareas;
import com.implementation.Tarea;
import com.database.GestorRegistro;
import com.windowP.PrincipalWindow;
import com.utils.DateValidator;
import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JCalendar;
import javax.swing.SpinnerDateModel;

public class PanelTareaFactory {
    public static PanelTarea agregarTarea(JPanel parent, ListaTareas lista, NodoTareas nodo, GestorRegistro gestorRegistro) {
        PrincipalWindow principalWindow = (PrincipalWindow) parent.getTopLevelAncestor();
        if (nodo == null) {
            JTextField tituloField = new JTextField();
            JTextArea descripcionArea = new JTextArea(5, 20);
            descripcionArea.setLineWrap(true);
            descripcionArea.setWrapStyleWord(true);

            // Panel para el selector de fecha
            JPanel fechaPanel = new JPanel();
            fechaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            JTextField fechaField = new JTextField(10);
            fechaField.setEditable(false);
            JButton fechaBtn = new JButton("Seleccionar fecha");
            fechaBtn.addActionListener(e -> {
                JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
                JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
                timeSpinner.setEditor(timeEditor);
                timeSpinner.setValue(new java.util.Date());
                
                JPanel spinnerPanel = new JPanel();
                spinnerPanel.add(new JLabel("Hora:"));
                spinnerPanel.add(timeSpinner);
                
                JCalendar calendar = new JCalendar();
                // Set minimum date to today to prevent past date selection
                calendar.setMinSelectableDate(DateValidator.getMinimumDate());
                
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(calendar, BorderLayout.CENTER);
                panel.add(spinnerPanel, BorderLayout.SOUTH);
                
                // Add validation instructions
                JLabel instructionLabel = new JLabel(
                    "<html><i>📅 Solo se pueden seleccionar fechas futuras</i></html>");
                instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                instructionLabel.setForeground(new Color(0, 102, 102));
                panel.add(instructionLabel, BorderLayout.NORTH);
                
                boolean validDateSelected = false;
                while (!validDateSelected) {
                    int result = JOptionPane.showConfirmDialog(
                        null, panel, "Seleccionar fecha y hora", 
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    
                    if (result != JOptionPane.OK_OPTION) {
                        break; // User cancelled
                    }
                    
                    java.util.Date selectedDate = calendar.getDate();
                    java.util.Date selectedTime = (java.util.Date)timeSpinner.getValue();
                    
                    // Validate that the selected date and time is not in the past
                    if (!DateValidator.isFutureDateTime(selectedDate, selectedTime)) {
                        DateValidator.showPastDateTimeError(panel);
                        continue; // Show dialog again
                    }
                    
                    // Valid date selected, proceed
                    validDateSelected = true;
                    
                    // Combinar fecha y hora
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(selectedDate);
                    java.util.Calendar timeCal = java.util.Calendar.getInstance();
                    timeCal.setTime(selectedTime);
                    cal.set(java.util.Calendar.HOUR_OF_DAY, timeCal.get(java.util.Calendar.HOUR_OF_DAY));
                    cal.set(java.util.Calendar.MINUTE, timeCal.get(java.util.Calendar.MINUTE));
                    
                    // Formatear la fecha
                    String formattedDate = String.format("%tF %tR", cal, cal);
                    fechaField.setText(formattedDate);
                }
            });
            fechaPanel.add(fechaField);
            fechaPanel.add(fechaBtn);

            Object[] message = {
                "Título de la tarea:", tituloField,
                "Descripción:", new JScrollPane(descripcionArea),
                "Fecha de entrega:", fechaPanel
            };
            int option = JOptionPane.showConfirmDialog(parent, message, "Nueva Tarea", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION || tituloField.getText().trim().isEmpty()) {
                if (tituloField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(parent, "El título no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }
            String titulo = tituloField.getText().trim();
            String descripcion = descripcionArea.getText().trim();
            int idUsuario = principalWindow.getIdUsuario();
            String fechaEntrega = fechaField.getText().trim();
            
            // Guardar la tarea en la base de datos
            int idTarea = gestorRegistro.agregarTarea(idUsuario, titulo, descripcion, fechaEntrega);
            if (idTarea == -1) {
                JOptionPane.showMessageDialog(parent, "Error al guardar la tarea.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            // Crear el nodo de tarea y establecer la fecha
            NodoTareas nodoTarea = lista.agregarTarea(idTarea, idUsuario, titulo, descripcion);
            nodoTarea.getDato().setFechaEntrega(fechaEntrega);
            nodo = nodoTarea;
        }
        PanelTarea panel = new PanelTarea(nodo.getDato(), gestorRegistro, principalWindow);
        panel.setPreferredSize(new Dimension(180, 120));
        panel.setBackground(new Color(0, 153, 153));
        nodo.setPanelAsociado(panel);
        parent.add(panel);
        parent.revalidate();
        parent.repaint();
        return panel;
    }
}