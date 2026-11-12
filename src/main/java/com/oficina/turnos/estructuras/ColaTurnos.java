package com.oficina.turnos.estructuras;

import com.oficina.turnos.modelo.Turno;

import java.util.ArrayList;
import java.util.List;

/**
 * Cola FIFO de turnos pendientes implementada con una lista enlazada propia.
 */
public class ColaTurnos {

    private Nodo frente;
    private Nodo fin;

    public void encolar(Turno turno) {
        Nodo nuevo = new Nodo(turno);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
    }

    public Turno desencolar() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola está vacía.");
        }
        Turno turno = frente.getTurno();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        return turno;
    }

    public Turno verPrimero() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola está vacía.");
        }
        return frente.getTurno();
    }

    /**
     * Retira de la cola el turno con el número indicado, conservando el orden de los demás.
     *
     * @return true si el turno estaba en la cola; false en caso contrario
     */
    public boolean cancelar(int numero) {
        Nodo anterior = null;
        Nodo actual = frente;
        while (actual != null && actual.getTurno().getNumero() != numero) {
            anterior = actual;
            actual = actual.getSiguiente();
        }
        if (actual == null) {
            return false;
        }
        if (anterior == null) {
            frente = actual.getSiguiente();
        } else {
            anterior.setSiguiente(actual.getSiguiente());
        }
        if (actual == fin) {
            fin = anterior;
        }
        return true;
    }

    /**
     * Recorre la cola desde el frente hasta el final y devuelve los turnos en orden de atención.
     */
    public List<Turno> recorrer() {
        List<Turno> turnos = new ArrayList<>();
        Nodo actual = frente;
        while (actual != null) {
            turnos.add(actual.getTurno());
            actual = actual.getSiguiente();
        }
        return turnos;
    }

    public boolean estaVacia() {
        return frente == null;
    }
}
