package com.oficina.turnos.estructuras;

import com.oficina.turnos.modelo.Cliente;

/**
 * Nodo del árbol binario de búsqueda de clientes.
 */
class NodoArbol {

    private final Cliente cliente;
    private NodoArbol izquierdo;
    private NodoArbol derecho;

    NodoArbol(Cliente cliente) {
        this.cliente = cliente;
    }

    Cliente getCliente() {
        return cliente;
    }

    NodoArbol getIzquierdo() {
        return izquierdo;
    }

    void setIzquierdo(NodoArbol izquierdo) {
        this.izquierdo = izquierdo;
    }

    NodoArbol getDerecho() {
        return derecho;
    }

    void setDerecho(NodoArbol derecho) {
        this.derecho = derecho;
    }
}
