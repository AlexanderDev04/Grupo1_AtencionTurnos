package com.oficina.turnos.estructuras;

import com.oficina.turnos.modelo.Turno;

/**
 * Nodo de la lista enlazada que sostiene la cola de turnos pendientes.
 */
class Nodo {

    private final Turno turno;
    private Nodo siguiente;

    Nodo(Turno turno) {
        this.turno = turno;
    }

    Turno getTurno() {
        return turno;
    }

    Nodo getSiguiente() {
        return siguiente;
    }

    void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }
}
