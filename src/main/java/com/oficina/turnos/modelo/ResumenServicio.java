package com.oficina.turnos.modelo;

/**
 * Cantidades de turnos de un servicio, agrupadas por estado.
 */
public record ResumenServicio(int codigoServicio, String nombreServicio,
                              int pendientes, int atendidos, int cancelados) {

    public int total() {
        return pendientes + atendidos + cancelados;
    }
}
