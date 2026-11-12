package com.oficina.turnos.datos;

import com.oficina.turnos.modelo.Servicio;

import java.util.List;
import java.util.Optional;

public interface ServicioDAO {

    List<Servicio> listarTodos();

    Optional<Servicio> buscarPorCodigo(int codigo);

    void insertar(Servicio servicio);

    void actualizar(Servicio servicio);
}
