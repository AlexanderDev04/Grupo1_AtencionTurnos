package com.oficina.turnos.estructuras;

import com.oficina.turnos.modelo.Cliente;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol binario de búsqueda de clientes, ordenado por código.
 */
public class ArbolClientes {

    private NodoArbol raiz;

    /**
     * Inserta un cliente en el árbol.
     *
     * @return true si se insertó; false si ya existía un cliente con ese código
     */
    public boolean insertar(Cliente cliente) {
        if (buscar(cliente.getCodigo()) != null) {
            return false;
        }
        raiz = insertarRecursivo(raiz, cliente);
        return true;
    }

    private NodoArbol insertarRecursivo(NodoArbol nodo, Cliente cliente) {
        if (nodo == null) {
            return new NodoArbol(cliente);
        }
        if (cliente.getCodigo() < nodo.getCliente().getCodigo()) {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), cliente));
        } else {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), cliente));
        }
        return nodo;
    }

    /**
     * Busca un cliente por código.
     *
     * @return el cliente, o null si no existe
     */
    public Cliente buscar(int codigo) {
        return buscarRecursivo(raiz, codigo);
    }

    private Cliente buscarRecursivo(NodoArbol nodo, int codigo) {
        if (nodo == null) {
            return null;
        }
        int codigoNodo = nodo.getCliente().getCodigo();
        if (codigo == codigoNodo) {
            return nodo.getCliente();
        }
        if (codigo < codigoNodo) {
            return buscarRecursivo(nodo.getIzquierdo(), codigo);
        }
        return buscarRecursivo(nodo.getDerecho(), codigo);
    }

    /**
     * Recorrido inorden: devuelve los clientes ordenados por código.
     */
    public List<Cliente> inorden() {
        List<Cliente> clientes = new ArrayList<>();
        inordenRecursivo(raiz, clientes);
        return clientes;
    }

    private void inordenRecursivo(NodoArbol nodo, List<Cliente> resultado) {
        if (nodo == null) {
            return;
        }
        inordenRecursivo(nodo.getIzquierdo(), resultado);
        resultado.add(nodo.getCliente());
        inordenRecursivo(nodo.getDerecho(), resultado);
    }
}
