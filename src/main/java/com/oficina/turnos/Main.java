package com.oficina.turnos;

import com.oficina.turnos.datos.ClienteDAO;
import com.oficina.turnos.datos.ClienteDAOJdbc;
import com.oficina.turnos.datos.ConexionSQLite;
import com.oficina.turnos.datos.ServicioDAO;
import com.oficina.turnos.datos.ServicioDAOJdbc;
import com.oficina.turnos.datos.TurnoDAO;
import com.oficina.turnos.datos.TurnoDAOJdbc;
import com.oficina.turnos.excepciones.AccesoDatosException;
import com.oficina.turnos.negocio.ClienteService;
import com.oficina.turnos.negocio.ReporteService;
import com.oficina.turnos.negocio.ServicioService;
import com.oficina.turnos.negocio.TurnoService;
import com.oficina.turnos.vista.MenuConsola;

public class Main {

    public static void main(String[] args) {
        try {
            ConexionSQLite.inicializarSiHaceFalta();

            ClienteDAO clienteDao = new ClienteDAOJdbc();
            ServicioDAO servicioDao = new ServicioDAOJdbc();
            TurnoDAO turnoDao = new TurnoDAOJdbc();

            ClienteService clienteService = new ClienteService(clienteDao, turnoDao);
            ServicioService servicioService = new ServicioService(servicioDao, turnoDao);
            TurnoService turnoService = new TurnoService(turnoDao, clienteService, servicioService);
            ReporteService reporteService = new ReporteService(turnoDao, clienteService);

            clienteService.cargarArbol();
            turnoService.cargarCola();

            new MenuConsola(clienteService, servicioService, turnoService, reporteService).iniciar();
        } catch (AccesoDatosException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
        }
    }
}
