package com.oficina.turnos.datos;

import com.oficina.turnos.excepciones.AccesoDatosException;
import com.oficina.turnos.modelo.EstadoTurno;
import com.oficina.turnos.modelo.ResumenServicio;
import com.oficina.turnos.modelo.Turno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TurnoDAOJdbc implements TurnoDAO {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String COLUMNAS =
            "SELECT numero, cliente_codigo, servicio_codigo, fecha_generacion, estado, "
                    + "fecha_atencion, fecha_cancelacion FROM turnos ";

    private static final String SQL_INSERTAR =
            "INSERT INTO turnos (cliente_codigo, servicio_codigo, fecha_generacion, estado) VALUES (?, ?, ?, ?)";
    private static final String SQL_MARCAR_ATENDIDO =
            "UPDATE turnos SET estado = ?, fecha_atencion = ? WHERE numero = ? AND estado = ?";
    private static final String SQL_MARCAR_CANCELADO =
            "UPDATE turnos SET estado = ?, fecha_cancelacion = ? WHERE numero = ? AND estado = ?";
    private static final String SQL_BUSCAR_POR_NUMERO = COLUMNAS + "WHERE numero = ?";
    private static final String SQL_LISTAR_POR_ESTADO = COLUMNAS + "WHERE estado = ? ORDER BY numero";
    private static final String SQL_LISTAR_POR_CLIENTE = COLUMNAS + "WHERE cliente_codigo = ? ORDER BY numero";
    private static final String SQL_PENDIENTE_DE_CLIENTE =
            "SELECT 1 FROM turnos WHERE cliente_codigo = ? AND estado = ?";
    private static final String SQL_PENDIENTE_DE_SERVICIO =
            "SELECT 1 FROM turnos WHERE servicio_codigo = ? AND estado = ?";
    private static final String SQL_RESUMEN =
            "SELECT s.codigo, s.nombre, "
                    + "COALESCE(SUM(CASE WHEN t.estado = ? THEN 1 ELSE 0 END), 0) AS pendientes, "
                    + "COALESCE(SUM(CASE WHEN t.estado = ? THEN 1 ELSE 0 END), 0) AS atendidos, "
                    + "COALESCE(SUM(CASE WHEN t.estado = ? THEN 1 ELSE 0 END), 0) AS cancelados "
                    + "FROM servicios s LEFT JOIN turnos t ON t.servicio_codigo = s.codigo "
                    + "GROUP BY s.codigo, s.nombre ORDER BY s.codigo";

    @Override
    public int insertar(int clienteCodigo, int servicioCodigo, LocalDateTime fechaGeneracion) {
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, clienteCodigo);
            sentencia.setInt(2, servicioCodigo);
            sentencia.setString(3, fechaGeneracion.format(FORMATO_FECHA));
            sentencia.setString(4, EstadoTurno.PENDIENTE.name());
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
                throw new AccesoDatosException("No se obtuvo el número del turno generado.");
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo guardar el turno.", e);
        }
    }

    @Override
    public void marcarAtendido(int numero, LocalDateTime fechaAtencion) {
        actualizarEstado(SQL_MARCAR_ATENDIDO, EstadoTurno.ATENDIDO, fechaAtencion, numero);
    }

    @Override
    public void marcarCancelado(int numero, LocalDateTime fechaCancelacion) {
        actualizarEstado(SQL_MARCAR_CANCELADO, EstadoTurno.CANCELADO, fechaCancelacion, numero);
    }

    private void actualizarEstado(String sql, EstadoTurno nuevoEstado, LocalDateTime fecha, int numero) {
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nuevoEstado.name());
            sentencia.setString(2, fecha.format(FORMATO_FECHA));
            sentencia.setInt(3, numero);
            sentencia.setString(4, EstadoTurno.PENDIENTE.name());
            if (sentencia.executeUpdate() != 1) {
                throw new AccesoDatosException("El turno " + numero + " no está pendiente en la base de datos.");
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar el turno " + numero + ".", e);
        }
    }

    @Override
    public Optional<Turno> buscarPorNumero(int numero) {
        List<Turno> turnos = consultar(SQL_BUSCAR_POR_NUMERO, numero, "No se pudo consultar el turno.");
        return turnos.isEmpty() ? Optional.empty() : Optional.of(turnos.get(0));
    }

    @Override
    public List<Turno> listarPorEstado(EstadoTurno estado) {
        return consultar(SQL_LISTAR_POR_ESTADO, estado.name(), "No se pudieron consultar los turnos.");
    }

    @Override
    public List<Turno> listarPorCliente(int clienteCodigo) {
        return consultar(SQL_LISTAR_POR_CLIENTE, clienteCodigo, "No se pudo consultar el historial del cliente.");
    }

    @Override
    public boolean existePendienteDeCliente(int clienteCodigo) {
        return existe(SQL_PENDIENTE_DE_CLIENTE, clienteCodigo);
    }

    @Override
    public boolean existePendienteDeServicio(int servicioCodigo) {
        return existe(SQL_PENDIENTE_DE_SERVICIO, servicioCodigo);
    }

    @Override
    public List<ResumenServicio> resumenPorServicio() {
        List<ResumenServicio> resumen = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_RESUMEN)) {
            sentencia.setString(1, EstadoTurno.PENDIENTE.name());
            sentencia.setString(2, EstadoTurno.ATENDIDO.name());
            sentencia.setString(3, EstadoTurno.CANCELADO.name());
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    resumen.add(new ResumenServicio(
                            resultado.getInt("codigo"),
                            resultado.getString("nombre"),
                            resultado.getInt("pendientes"),
                            resultado.getInt("atendidos"),
                            resultado.getInt("cancelados")));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo generar el resumen por servicio.", e);
        }
        return resumen;
    }

    private List<Turno> consultar(String sql, Object parametro, String mensajeError) {
        List<Turno> turnos = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setObject(1, parametro);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    turnos.add(mapear(resultado));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException(mensajeError, e);
        }
        return turnos;
    }

    private boolean existe(String sql, int codigo) {
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, codigo);
            sentencia.setString(2, EstadoTurno.PENDIENTE.name());
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo verificar los turnos pendientes.", e);
        }
    }

    private Turno mapear(ResultSet resultado) throws SQLException {
        return new Turno(
                resultado.getInt("numero"),
                resultado.getInt("cliente_codigo"),
                resultado.getInt("servicio_codigo"),
                leerFecha(resultado.getString("fecha_generacion")),
                EstadoTurno.valueOf(resultado.getString("estado")),
                leerFecha(resultado.getString("fecha_atencion")),
                leerFecha(resultado.getString("fecha_cancelacion")));
    }

    private LocalDateTime leerFecha(String texto) {
        return texto == null ? null : LocalDateTime.parse(texto, FORMATO_FECHA);
    }
}
