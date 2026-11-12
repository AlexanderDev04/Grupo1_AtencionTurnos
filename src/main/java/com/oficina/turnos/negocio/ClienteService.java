package com.oficina.turnos.negocio;

import com.oficina.turnos.datos.ClienteDAO;
import com.oficina.turnos.datos.TurnoDAO;
import com.oficina.turnos.estructuras.ArbolClientes;
import com.oficina.turnos.excepciones.NegocioException;
import com.oficina.turnos.modelo.Cliente;

import java.util.List;

/**
 * Validaciones y coordinación de las operaciones sobre clientes.
 * La base de datos se actualiza primero; el árbol solo cambia si el guardado fue exitoso.
 */
public class ClienteService {

    private static final String PATRON_TELEFONO = "\\d{8}";

    private final ClienteDAO clienteDao;
    private final TurnoDAO turnoDao;
    private ArbolClientes arbol = new ArbolClientes();

    public ClienteService(ClienteDAO clienteDao, TurnoDAO turnoDao) {
        this.clienteDao = clienteDao;
        this.turnoDao = turnoDao;
    }

    /**
     * Construye el BST con los clientes guardados en SQLite.
     */
    public void cargarArbol() {
        ArbolClientes nuevoArbol = new ArbolClientes();
        for (Cliente cliente : clienteDao.listarTodos()) {
            nuevoArbol.insertar(cliente);
        }
        this.arbol = nuevoArbol;
    }

    public Cliente registrar(int codigo, String nombreCompleto, String telefono) {
        validarCodigo(codigo);
        String nombre = validarNombre(nombreCompleto);
        String telefonoValido = validarTelefono(telefono);
        if (arbol.buscar(codigo) != null) {
            throw new NegocioException("Ya existe un cliente con el código " + codigo + ".");
        }
        Cliente cliente = new Cliente(codigo, nombre, telefonoValido, true);
        clienteDao.insertar(cliente);
        arbol.insertar(cliente);
        return cliente;
    }

    public List<Cliente> listar() {
        return arbol.inorden();
    }

    public Cliente buscar(int codigo) {
        Cliente cliente = arbol.buscar(codigo);
        if (cliente == null) {
            throw new NegocioException("No existe un cliente con el código " + codigo + ".");
        }
        return cliente;
    }

    public Cliente actualizar(int codigo, String nombreCompleto, String telefono) {
        Cliente cliente = buscar(codigo);
        String nombre = validarNombre(nombreCompleto);
        String telefonoValido = validarTelefono(telefono);
        clienteDao.actualizar(new Cliente(codigo, nombre, telefonoValido, cliente.isActivo()));
        cliente.setNombreCompleto(nombre);
        cliente.setTelefono(telefonoValido);
        return cliente;
    }

    public Cliente cambiarEstado(int codigo, boolean activar) {
        Cliente cliente = buscar(codigo);
        if (cliente.isActivo() == activar) {
            throw new NegocioException("El cliente " + codigo + " ya está " + (activar ? "activo." : "inactivo."));
        }
        if (!activar && turnoDao.existePendienteDeCliente(codigo)) {
            throw new NegocioException("No se puede desactivar al cliente " + codigo + ": tiene un turno pendiente.");
        }
        clienteDao.actualizar(new Cliente(codigo, cliente.getNombreCompleto(), cliente.getTelefono(), activar));
        cliente.setActivo(activar);
        return cliente;
    }

    private void validarCodigo(int codigo) {
        if (codigo <= 0) {
            throw new NegocioException("El código debe ser un número entero positivo.");
        }
    }

    private String validarNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new NegocioException("El nombre completo es obligatorio.");
        }
        return nombreCompleto.trim();
    }

    private String validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new NegocioException("El teléfono es obligatorio.");
        }
        String limpio = telefono.trim();
        if (!limpio.matches(PATRON_TELEFONO)) {
            throw new NegocioException("El teléfono debe tener exactamente 8 dígitos.");
        }
        return limpio;
    }
}
