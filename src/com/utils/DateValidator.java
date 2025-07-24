package com.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 * Utility class for date validation in the task manager
 * Ensures only future dates can be selected for task due dates
 */
public class DateValidator {
    
    /**
     * Validates that the selected date is not in the past
     * @param selectedDate The date to validate
     * @return true if the date is today or in the future, false if it's in the past
     */
    public static boolean isFutureOrToday(Date selectedDate) {
        if (selectedDate == null) {
            return false;
        }
        
        LocalDate selected = selectedDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        LocalDate today = LocalDate.now();
        
        return !selected.isBefore(today);
    }
    
    /**
     * Validates that the combined date and time is not in the past
     * @param selectedDate The selected date
     * @param selectedTime The selected time
     * @return true if the datetime is in the future, false if it's in the past
     */
    public static boolean isFutureDateTime(Date selectedDate, Date selectedTime) {
        if (selectedDate == null || selectedTime == null) {
            return false;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(selectedTime);
        
        // Combine date and time
        cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Date combinedDateTime = cal.getTime();
        Date now = new Date();
        
        return combinedDateTime.after(now);
    }
    
    /**
     * Shows a user-friendly error message for invalid date selection
     * @param parent The parent component for the dialog
     */
    public static void showPastDateError(java.awt.Component parent) {
        JOptionPane.showMessageDialog(
            parent,
            "⚠️ No se puede seleccionar una fecha del pasado.\n" +
            "Por favor, selecciona una fecha de hoy en adelante.",
            "Fecha Inválida",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Shows a user-friendly error message for invalid datetime selection
     * @param parent The parent component for the dialog
     */
    public static void showPastDateTimeError(java.awt.Component parent) {
        JOptionPane.showMessageDialog(
            parent,
            "⚠️ No se puede seleccionar una fecha y hora del pasado.\n" +
            "Por favor, selecciona un momento futuro.",
            "Fecha y Hora Inválidas",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Gets the minimum selectable date (today)
     * @return Today's date
     */
    public static Date getMinimumDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Formats a date validation message for the user
     * @param selectedDate The date that was selected
     * @return A formatted message string
     */
    public static String formatValidationMessage(Date selectedDate) {
        LocalDate selected = selectedDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        LocalDate today = LocalDate.now();
        
        if (selected.isBefore(today)) {
            return String.format("La fecha seleccionada (%s) es anterior a hoy (%s)", 
                selected.toString(), today.toString());
        }
        
        return "Fecha válida";
    }
}
