package com.oficina.turnos.negocio;

import com.oficina.turnos.datos.ServicioDAO;
import com.oficina.turnos.datos.TurnoDAO;
import com.oficina.turnos.excepciones.NegocioException;
import com.oficina.turnos.modelo.Servicio;

import java.util.List;

/**
 * Validaciones y coordinación de las operaciones sobre servicios.
 */
public class ServicioService {

    private final ServicioDAO servicioDao;
    private final TurnoDAO turnoDao;

    public ServicioService(ServicioDAO servicioDao, TurnoDAO turnoDao) {
        this.servicioDao = servicioDao;
        this.turnoDao = turnoDao;
    }

    public Servicio registrar(int codigo, String nombre) {
        if (codigo <= 0) {
            throw new NegocioException("El código debe ser un número entero positivo.");
        }
        String nombreValido = validarNombre(nombre);
        if (servicioDao.buscarPorCodigo(codigo).isPresent()) {
            throw new NegocioException("Ya existe un servicio con el código " + codigo + ".");
        }
        Servicio servicio = new Servicio(codigo, nombreValido, true);
        servicioDao.insertar(servicio);
        return servicio;
    }

    public List<Servicio> listar() {
        return servicioDao.listarTodos();
    }

    public Servicio buscar(int codigo) {
        return servicioDao.buscarPorCodigo(codigo)
                .orElseThrow(() -> new NegocioException("No existe un servicio con el código " + codigo + "."));
    }

    public Servicio actualizar(int codigo, String nombre) {
        Servicio servicio = buscar(codigo);
        servicio.setNombre(validarNombre(nombre));
        servicioDao.actualizar(servicio);
        return servicio;
    }

    public Servicio cambiarEstado(int codigo, boolean activar) {
        Servicio servicio = buscar(codigo);
        if (servicio.isActivo() == activar) {
            throw new NegocioException("El servicio " + codigo + " ya está " + (activar ? "activo." : "inactivo."));
        }
        if (!activar && turnoDao.existePendienteDeServicio(codigo)) {
            throw new NegocioException("No se puede desactivar el servicio " + codigo + ": tiene turnos pendientes.");
        }
        servicio.setActivo(activar);
        servicioDao.actualizar(servicio);
        return servicio;
    }

    private String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new NegocioException("El nombre del servicio es obligatorio.");
        }
        return nombre.trim();
    }
}
