package com.oficina.turnos.negocio;

import com.oficina.turnos.datos.TurnoDAO;
import com.oficina.turnos.modelo.EstadoTurno;
import com.oficina.turnos.modelo.ResumenServicio;
import com.oficina.turnos.modelo.Turno;

import java.util.List;

/**
 * Consultas y reportes de turnos.
 */
public class ReporteService {

    private final TurnoDAO turnoDao;
    private final ClienteService clienteService;

    public ReporteService(TurnoDAO turnoDao, ClienteService clienteService) {
        this.turnoDao = turnoDao;
        this.clienteService = clienteService;
    }

    public List<Turno> listarAtendidos() {
        return turnoDao.listarPorEstado(EstadoTurno.ATENDIDO);
    }

    public List<Turno> listarCancelados() {
        return turnoDao.listarPorEstado(EstadoTurno.CANCELADO);
    }

    public List<Turno> historialDeCliente(int clienteCodigo) {
        clienteService.buscar(clienteCodigo);
        return turnoDao.listarPorCliente(clienteCodigo);
    }

    public List<ResumenServicio> resumenPorServicio() {
        return turnoDao.resumenPorServicio();
    }
}
