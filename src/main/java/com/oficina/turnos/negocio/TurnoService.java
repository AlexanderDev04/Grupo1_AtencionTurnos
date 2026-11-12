package com.oficina.turnos.negocio;

import com.oficina.turnos.datos.TurnoDAO;
import com.oficina.turnos.estructuras.ColaTurnos;
import com.oficina.turnos.excepciones.NegocioException;
import com.oficina.turnos.modelo.Cliente;
import com.oficina.turnos.modelo.EstadoTurno;
import com.oficina.turnos.modelo.Servicio;
import com.oficina.turnos.modelo.Turno;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Reglas de negocio de los turnos y coordinación entre la cola en memoria y SQLite.
 * En cada operación se guarda primero en la base de datos; la cola solo cambia si el guardado fue exitoso.
 */
public class TurnoService {

    private final TurnoDAO turnoDao;
    private final ClienteService clienteService;
    private final ServicioService servicioService;
    private ColaTurnos cola = new ColaTurnos();

    public TurnoService(TurnoDAO turnoDao, ClienteService clienteService, ServicioService servicioService) {
        this.turnoDao = turnoDao;
        this.clienteService = clienteService;
        this.servicioService = servicioService;
    }

    /**
     * Reconstruye la cola con los turnos pendientes guardados, ordenados por número.
     */
    public void cargarCola() {
        ColaTurnos nuevaCola = new ColaTurnos();
        for (Turno turno : turnoDao.listarPorEstado(EstadoTurno.PENDIENTE)) {
            nuevaCola.encolar(turno);
        }
        this.cola = nuevaCola;
    }

    public Turno generarTurno(int clienteCodigo, int servicioCodigo) {
        Cliente cliente = clienteService.buscar(clienteCodigo);
        if (!cliente.isActivo()) {
            throw new NegocioException("El cliente " + clienteCodigo + " está inactivo y no puede recibir turnos.");
        }
        Servicio servicio = servicioService.buscar(servicioCodigo);
        if (!servicio.isActivo()) {
            throw new NegocioException("El servicio " + servicioCodigo + " está inactivo.");
        }
        if (turnoDao.existePendienteDeCliente(clienteCodigo)) {
            throw new NegocioException("El cliente " + clienteCodigo + " ya tiene un turno pendiente.");
        }
        LocalDateTime ahora = ahora();
        int numero = turnoDao.insertar(clienteCodigo, servicioCodigo, ahora);
        Turno turno = new Turno(numero, clienteCodigo, servicioCodigo, ahora, EstadoTurno.PENDIENTE, null, null);
        cola.encolar(turno);
        return turno;
    }

    public Turno atenderSiguiente() {
        if (cola.estaVacia()) {
            throw new NegocioException("No existen turnos pendientes.");
        }
        Turno turno = cola.verPrimero();
        LocalDateTime ahora = ahora();
        turnoDao.marcarAtendido(turno.getNumero(), ahora);
        cola.desencolar();
        turno.marcarAtendido(ahora);
        return turno;
    }

    public Turno cancelarTurno(int numero) {
        Turno turno = turnoDao.buscarPorNumero(numero)
                .orElseThrow(() -> new NegocioException("No existe un turno con el número " + numero + "."));
        if (turno.getEstado() != EstadoTurno.PENDIENTE) {
            throw new NegocioException("No se puede cancelar el turno " + numero
                    + ": ya fue " + turno.getEstado().name().toLowerCase() + ".");
        }
        LocalDateTime ahora = ahora();
        turnoDao.marcarCancelado(numero, ahora);
        cola.cancelar(numero);
        turno.marcarCancelado(ahora);
        return turno;
    }

    /**
     * Turnos pendientes en orden de atención.
     */
    public List<Turno> listarPendientes() {
        return cola.recorrer();
    }

    private LocalDateTime ahora() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
