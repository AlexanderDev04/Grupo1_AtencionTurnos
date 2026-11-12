package com.oficina.turnos.datos;

import com.oficina.turnos.modelo.EstadoTurno;
import com.oficina.turnos.modelo.ResumenServicio;
import com.oficina.turnos.modelo.Turno;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TurnoDAO {

    /**
     * Guarda un turno nuevo en estado PENDIENTE y devuelve el número generado.
     */
    int insertar(int clienteCodigo, int servicioCodigo, LocalDateTime fechaGeneracion);

    void marcarAtendido(int numero, LocalDateTime fechaAtencion);

    void marcarCancelado(int numero, LocalDateTime fechaCancelacion);

    Optional<Turno> buscarPorNumero(int numero);

    /**
     * Turnos de un estado, ordenados por número.
     */
    List<Turno> listarPorEstado(EstadoTurno estado);

    List<Turno> listarPorCliente(int clienteCodigo);

    boolean existePendienteDeCliente(int clienteCodigo);

    boolean existePendienteDeServicio(int servicioCodigo);

    /**
     * Cantidades por estado de cada servicio, incluidos los que no tienen turnos.
     */
    List<ResumenServicio> resumenPorServicio();
}
