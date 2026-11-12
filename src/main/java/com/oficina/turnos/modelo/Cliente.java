package com.oficina.turnos.modelo;

public class Cliente {

    private final int codigo;
    private String nombreCompleto;
    private String telefono;
    private boolean activo;

    public Cliente(int codigo, String nombreCompleto, String telefono, boolean activo) {
        this.codigo = codigo;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.activo = activo;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return String.format("%d | %s | Tel: %s | %s",
                codigo, nombreCompleto, telefono, activo ? "Activo" : "Inactivo");
    }
}
