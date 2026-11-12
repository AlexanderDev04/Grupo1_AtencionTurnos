package com.oficina.turnos.excepciones;

/**
 * Se lanza cuando una operación incumple una regla de negocio o recibe datos inválidos.
 */
public class NegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
