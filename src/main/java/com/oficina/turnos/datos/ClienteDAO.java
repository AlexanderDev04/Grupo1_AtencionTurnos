package com.oficina.turnos.datos;

import com.oficina.turnos.modelo.Cliente;

import java.util.List;

public interface ClienteDAO {

    List<Cliente> listarTodos();

    void insertar(Cliente cliente);

    void actualizar(Cliente cliente);
}
