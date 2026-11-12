package com.oficina.turnos.modelo;

import java.time.LocalDateTime;

public class Turno {

    private final int numero;
    private final int clienteCodigo;
    private final int servicioCodigo;
    private final LocalDateTime fechaGeneracion;
    private EstadoTurno estado;
    private LocalDateTime fechaAtencion;
    private LocalDateTime fechaCancelacion;

    public Turno(int numero, int clienteCodigo, int servicioCodigo, LocalDateTime fechaGeneracion,
                 EstadoTurno estado, LocalDateTime fechaAtencion, LocalDateTime fechaCancelacion) {
        this.numero = numero;
        this.clienteCodigo = clienteCodigo;
        this.servicioCodigo = servicioCodigo;
        this.fechaGeneracion = fechaGeneracion;
        this.estado = estado;
        this.fechaAtencion = fechaAtencion;
        this.fechaCancelacion = fechaCancelacion;
    }

    public int getNumero() {
        return numero;
    }

    public int getClienteCodigo() {
        return clienteCodigo;
    }

    public int getServicioCodigo() {
        return servicioCodigo;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public LocalDateTime getFechaAtencion() {
        return fechaAtencion;
    }

    public LocalDateTime getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void marcarAtendido(LocalDateTime fecha) {
        this.estado = EstadoTurno.ATENDIDO;
        this.fechaAtencion = fecha;
    }

    public void marcarCancelado(LocalDateTime fecha) {
        this.estado = EstadoTurno.CANCELADO;
        this.fechaCancelacion = fecha;
    }
}
