package com.components;

import com.database.GestorRegistro;
import com.implementation.Tarea;
import com.utils.DateValidator;
import java.awt.*;
import javax.swing.*;
import com.toedter.calendar.JCalendar;
import javax.swing.SpinnerDateModel;

public class VistaTareaDialog extends JDialog {
    private Tarea tarea;
    private GestorRegistro gestorRegistro;

    public VistaTareaDialog(JFrame parent, Tarea tarea, GestorRegistro gestorRegistro) {
        super(parent, "Vista de Tarea", true);
        this.tarea = tarea;
        this.gestorRegistro = gestorRegistro;
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(parent);

        JLabel titulo = new JLabel(tarea.getNombre(), SwingConstants.CENTER);
        titulo.setFont(new Font("Roboto SemiBold", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        // Panel central para descripción y fecha
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        // Descripción
        JTextArea descripcion = new JTextArea(tarea.getDescripcion());
        descripcion.setWrapStyleWord(true);
        descripcion.setLineWrap(true);
        descripcion.setEditable(false);
        descripcion.setBackground(null);
        descripcion.setBorder(BorderFactory.createTitledBorder("Descripción"));
        
        // Fecha de entrega
        JPanel fechaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fechaLabel = new JLabel("Fecha de entrega: ");
        JTextField fechaField = new JTextField(15);
        fechaField.setEditable(false);
        if (tarea.getFechaEntrega() != null) {
            fechaField.setText(tarea.getFechaEntrega());
        }
        
        JButton editFechaBtn = new JButton("Cambiar fecha");
        editFechaBtn.addActionListener(e -> {
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
                    this, panel, "Seleccionar fecha y hora", 
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
                tarea.setFechaEntrega(formattedDate);
                gestorRegistro.actualizarFechaEntrega(tarea.getIdTarea(), formattedDate);
            }
        });
        
        fechaPanel.add(fechaLabel);
        fechaPanel.add(fechaField);
        fechaPanel.add(editFechaBtn);
        
        centerPanel.add(descripcion);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(fechaPanel);
        
        add(new JScrollPane(centerPanel), BorderLayout.CENTER);
    }
}