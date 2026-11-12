package com.oficina.turnos.datos;

import com.oficina.turnos.excepciones.AccesoDatosException;
import com.oficina.turnos.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOJdbc implements ClienteDAO {

    private static final String SQL_LISTAR =
            "SELECT codigo, nombre, telefono, activo FROM clientes ORDER BY codigo";
    private static final String SQL_INSERTAR =
            "INSERT INTO clientes (codigo, nombre, telefono, activo) VALUES (?, ?, ?, ?)";
    private static final String SQL_ACTUALIZAR =
            "UPDATE clientes SET nombre = ?, telefono = ?, activo = ? WHERE codigo = ?";

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                clientes.add(new Cliente(
                        resultado.getInt("codigo"),
                        resultado.getString("nombre"),
                        resultado.getString("telefono"),
                        resultado.getInt("activo") == 1));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudieron consultar los clientes.", e);
        }
        return clientes;
    }

    @Override
    public void insertar(Cliente cliente) {
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR)) {
            sentencia.setInt(1, cliente.getCodigo());
            sentencia.setString(2, cliente.getNombreCompleto());
            sentencia.setString(3, cliente.getTelefono());
            sentencia.setInt(4, cliente.isActivo() ? 1 : 0);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo guardar el cliente.", e);
        }
    }

    @Override
    public void actualizar(Cliente cliente) {
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setString(1, cliente.getNombreCompleto());
            sentencia.setString(2, cliente.getTelefono());
            sentencia.setInt(3, cliente.isActivo() ? 1 : 0);
            sentencia.setInt(4, cliente.getCodigo());
            if (sentencia.executeUpdate() != 1) {
                throw new AccesoDatosException("No se encontró el cliente " + cliente.getCodigo() + " en la base de datos.");
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar el cliente.", e);
        }
    }
}
