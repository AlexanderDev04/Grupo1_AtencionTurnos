package com.oficina.turnos.excepciones;

/**
 * Se lanza cuando falla una operación contra la base de datos SQLite.
 */
public class AccesoDatosException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public AccesoDatosException(String mensaje) {
        super(mensaje);
    }
}
