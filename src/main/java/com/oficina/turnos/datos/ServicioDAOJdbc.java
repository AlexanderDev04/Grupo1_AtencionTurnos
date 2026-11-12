package com.oficina.turnos.datos;

import com.oficina.turnos.excepciones.AccesoDatosException;
import com.oficina.turnos.modelo.Servicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicioDAOJdbc implements ServicioDAO {

    private static final String SQL_LISTAR =
            "SELECT codigo, nombre, activo FROM servicios ORDER BY codigo";
    private static final String SQL_BUSCAR =
            "SELECT codigo, nombre, activo FROM servicios WHERE codigo = ?";
    private static final String SQL_INSERTAR =
            "INSERT INTO servicios (codigo, nombre, activo) VALUES (?, ?, ?)";
    private static final String SQL_ACTUALIZAR =
            "UPDATE servicios SET nombre = ?, activo = ? WHERE codigo = ?";

    @Override
    public List<Servicio> listarTodos() {
        List<Servicio> servicios = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                servicios.add(mapear(resultado));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudieron consultar los servicios.", e);
        }
        return servicios;
    }

    @Override
    public Optional<Servicio> buscarPorCodigo(int codigo) {
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR)) {
            sentencia.setInt(1, codigo);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(mapear(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo consultar el servicio.", e);
        }
    }

    @Override
    public void insertar(Servicio servicio) {
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {
            sentencia.setInt(1, servicio.getCodigo());
            sentencia.setString(2, servicio.getNombre());
            sentencia.setInt(3, servicio.isActivo() ? 1 : 0);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo guardar el servicio.", e);
        }
    }

    @Override
    public void actualizar(Servicio servicio) {
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setString(1, servicio.getNombre());
            sentencia.setInt(2, servicio.isActivo() ? 1 : 0);
            sentencia.setInt(3, servicio.getCodigo());
            if (sentencia.executeUpdate() != 1) {
                throw new AccesoDatosException("No se encontró el servicio " + servicio.getCodigo() + " en la base de datos.");
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar el servicio.", e);
        }
    }

    private Servicio mapear(ResultSet resultado) throws SQLException {
        return new Servicio(
                resultado.getInt("codigo"),
                resultado.getString("nombre"),
                resultado.getInt("activo") == 1);
    }
}
