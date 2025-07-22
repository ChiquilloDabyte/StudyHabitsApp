package com.implementation;

public class Tarea {
    private int idTarea;
    private int idUsuario;
    private String nombre;
    private String descripcion;
    private boolean completada = false;
    private String fechaEntrega;
    private String googleEventId;

    public Tarea(int idTarea, int idUsuario, String nombre, String descripcion) {
        this.idTarea = idTarea;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdTarea() { return idTarea; }
    public void setIdTarea(int idTarea) { this.idTarea = idTarea; }
    public int getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    
    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getGoogleEventId() {
        return googleEventId;
    }

    public void setGoogleEventId(String googleEventId) {
        this.googleEventId = googleEventId;
    }
}