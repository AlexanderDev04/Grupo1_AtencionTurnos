package com.oficina.turnos.datos;

import com.oficina.turnos.excepciones.AccesoDatosException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Crea conexiones JDBC a la base de datos SQLite local (ruta relativa data/turnos.db).
 */
public final class ConexionSQLite {

    private static final Path RUTA_BASE_DATOS = Path.of("data", "turnos.db");
    private static final Path RUTA_SCRIPT = Path.of("sql", "crear_y_cargar.sql");
    private static final String URL = "jdbc:sqlite:data/turnos.db";

    private ConexionSQLite() {
    }

    /**
     * Abre una conexión con las claves foráneas habilitadas. El llamador debe cerrarla.
     */
    public static Connection obtenerConexion() throws SQLException {
        Connection conexion = DriverManager.getConnection(URL);
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            conexion.close();
            throw e;
        }
        return conexion;
    }

    /**
     * Si la base de datos no tiene las tablas, ejecuta sql/crear_y_cargar.sql una sola vez.
     * Si ya existen, no modifica nada.
     */
    public static void inicializarSiHaceFalta() {
        try {
            Files.createDirectories(RUTA_BASE_DATOS.getParent());
            try (Connection conexion = obtenerConexion()) {
                if (!existeTabla(conexion, "clientes")) {
                    ejecutarScript(conexion);
                }
            }
        } catch (SQLException | IOException e) {
            throw new AccesoDatosException("No se pudo inicializar la base de datos: " + e.getMessage(), e);
        }
    }

    private static boolean existeTabla(Connection conexion, String nombreTabla) throws SQLException {
        String sql = "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nombreTabla);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    private static void ejecutarScript(Connection conexion) throws SQLException, IOException {
        String contenido = Files.readString(RUTA_SCRIPT, StandardCharsets.UTF_8);
        boolean autoCommitOriginal = conexion.getAutoCommit();
        conexion.setAutoCommit(false);
        try (Statement sentencia = conexion.createStatement()) {
            for (String instruccion : separarInstrucciones(contenido)) {
                sentencia.execute(instruccion);
            }
            conexion.commit();
        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(autoCommitOriginal);
        }
    }

    private static List<String> separarInstrucciones(String contenido) {
        StringBuilder sinComentarios = new StringBuilder();
        for (String linea : contenido.split("\\R")) {
            if (!linea.trim().startsWith("--")) {
                sinComentarios.append(linea).append('\n');
            }
        }
        List<String> instrucciones = new ArrayList<>();
        for (String parte : sinComentarios.toString().split(";")) {
            if (!parte.isBlank()) {
                instrucciones.add(parte.trim());
            }
        }
        return instrucciones;
    }
}
