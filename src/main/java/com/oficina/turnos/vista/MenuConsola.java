package com.oficina.turnos.vista;

import com.oficina.turnos.excepciones.AccesoDatosException;
import com.oficina.turnos.excepciones.NegocioException;
import com.oficina.turnos.modelo.Cliente;
import com.oficina.turnos.modelo.ResumenServicio;
import com.oficina.turnos.modelo.Servicio;
import com.oficina.turnos.modelo.Turno;
import com.oficina.turnos.negocio.ClienteService;
import com.oficina.turnos.negocio.ReporteService;
import com.oficina.turnos.negocio.ServicioService;
import com.oficina.turnos.negocio.TurnoService;

import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Menú de consola: entrada de datos y presentación de resultados.
 */
public class MenuConsola {

    private static final String NOMBRE_OFICINA = "Oficina de Atención al Cliente";
    private static final String OPCION_INVALIDA = "Opción inexistente. Intente de nuevo.";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ClienteService clienteService;
    private final ServicioService servicioService;
    private final TurnoService turnoService;
    private final ReporteService reporteService;
    private final Scanner entrada;

    public MenuConsola(ClienteService clienteService, ServicioService servicioService,
                       TurnoService turnoService, ReporteService reporteService) {
        this.clienteService = clienteService;
        this.servicioService = servicioService;
        this.turnoService = turnoService;
        this.reporteService = reporteService;
        this.entrada = new Scanner(System.in, charsetDeEntrada());
    }

    public void iniciar() {
        boolean salir = false;
        try {
            while (!salir) {
                mostrarMenuPrincipal();
                switch (leerLinea("Seleccione una opción: ")) {
                    case "1" -> menuClientes();
                    case "2" -> menuServicios();
                    case "3" -> ejecutar(this::generarTurno);
                    case "4" -> ejecutar(this::atenderSiguienteTurno);
                    case "5" -> ejecutar(this::cancelarTurno);
                    case "6" -> ejecutar(this::consultarPendientes);
                    case "7" -> ejecutar(this::consultarAtendidos);
                    case "8" -> ejecutar(this::consultarCancelados);
                    case "9" -> ejecutar(this::consultarHistorialCliente);
                    case "10" -> ejecutar(this::mostrarResumenPorServicio);
                    case "0" -> salir = true;
                    default -> System.out.println(OPCION_INVALIDA);
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Se cerró la entrada de datos.");
        }
        System.out.println("Hasta luego.");
    }

    // ---------------------------------------------------------------- menús

    private void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("========== " + NOMBRE_OFICINA + " ==========");
        System.out.println("1. Gestión de clientes");
        System.out.println("2. Gestión de servicios");
        System.out.println("3. Generar turno");
        System.out.println("4. Atender siguiente turno");
        System.out.println("5. Cancelar turno");
        System.out.println("6. Consultar turnos pendientes");
        System.out.println("7. Consultar turnos atendidos");
        System.out.println("8. Consultar turnos cancelados");
        System.out.println("9. Historial de un cliente");
        System.out.println("10. Resumen por servicio");
        System.out.println("0. Salir");
    }

    private void menuClientes() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- Gestión de clientes ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Consultar todos los clientes");
            System.out.println("3. Buscar cliente por código");
            System.out.println("4. Actualizar nombre y teléfono");
            System.out.println("5. Activar o desactivar cliente");
            System.out.println("0. Volver");
            switch (leerLinea("Seleccione una opción: ")) {
                case "1" -> ejecutar(this::registrarCliente);
                case "2" -> ejecutar(this::listarClientes);
                case "3" -> ejecutar(this::buscarCliente);
                case "4" -> ejecutar(this::actualizarCliente);
                case "5" -> ejecutar(this::cambiarEstadoCliente);
                case "0" -> volver = true;
                default -> System.out.println(OPCION_INVALIDA);
            }
        }
    }

    private void menuServicios() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("--- Gestión de servicios ---");
            System.out.println("1. Registrar servicio");
            System.out.println("2. Listar servicios");
            System.out.println("3. Actualizar nombre del servicio");
            System.out.println("4. Activar o desactivar servicio");
            System.out.println("0. Volver");
            switch (leerLinea("Seleccione una opción: ")) {
                case "1" -> ejecutar(this::registrarServicio);
                case "2" -> ejecutar(this::listarServicios);
                case "3" -> ejecutar(this::actualizarServicio);
                case "4" -> ejecutar(this::cambiarEstadoServicio);
                case "0" -> volver = true;
                default -> System.out.println(OPCION_INVALIDA);
            }
        }
    }

    // ---------------------------------------------------------------- clientes

    private void registrarCliente() {
        int codigo = leerEntero("Código: ");
        String nombre = leerLinea("Nombre completo: ");
        String telefono = leerLinea("Teléfono (8 dígitos): ");
        Cliente cliente = clienteService.registrar(codigo, nombre, telefono);
        System.out.println("Cliente registrado: " + cliente);
    }

    private void listarClientes() {
        List<Cliente> clientes = clienteService.listar();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("Código | Nombre | Teléfono | Estado");
        clientes.forEach(System.out::println);
    }

    private void buscarCliente() {
        int codigo = leerEntero("Código del cliente: ");
        System.out.println("Cliente encontrado: " + clienteService.buscar(codigo));
    }

    private void actualizarCliente() {
        int codigo = leerEntero("Código del cliente: ");
        String nombre = leerLinea("Nuevo nombre completo: ");
        String telefono = leerLinea("Nuevo teléfono (8 dígitos): ");
        System.out.println("Cliente actualizado: " + clienteService.actualizar(codigo, nombre, telefono));
    }

    private void cambiarEstadoCliente() {
        int codigo = leerEntero("Código del cliente: ");
        boolean activar = leerActivarODesactivar();
        System.out.println("Estado actualizado: " + clienteService.cambiarEstado(codigo, activar));
    }

    // ---------------------------------------------------------------- servicios

    private void registrarServicio() {
        int codigo = leerEntero("Código: ");
        String nombre = leerLinea("Nombre del servicio: ");
        System.out.println("Servicio registrado: " + servicioService.registrar(codigo, nombre));
    }

    private void listarServicios() {
        List<Servicio> servicios = servicioService.listar();
        if (servicios.isEmpty()) {
            System.out.println("No hay servicios registrados.");
            return;
        }
        System.out.println("Código | Nombre | Estado");
        servicios.forEach(System.out::println);
    }

    private void actualizarServicio() {
        int codigo = leerEntero("Código del servicio: ");
        String nombre = leerLinea("Nuevo nombre: ");
        System.out.println("Servicio actualizado: " + servicioService.actualizar(codigo, nombre));
    }

    private void cambiarEstadoServicio() {
        int codigo = leerEntero("Código del servicio: ");
        boolean activar = leerActivarODesactivar();
        System.out.println("Estado actualizado: " + servicioService.cambiarEstado(codigo, activar));
    }

    // ---------------------------------------------------------------- turnos

    private void generarTurno() {
        int clienteCodigo = leerEntero("Código del cliente: ");
        int servicioCodigo = leerEntero("Código del servicio: ");
        Turno turno = turnoService.generarTurno(clienteCodigo, servicioCodigo);
        System.out.println("Turno generado: " + describir(turno));
    }

    private void atenderSiguienteTurno() {
        Turno turno = turnoService.atenderSiguiente();
        System.out.println("Turno atendido: " + describir(turno));
    }

    private void cancelarTurno() {
        int numero = leerEntero("Número del turno a cancelar: ");
        Turno turno = turnoService.cancelarTurno(numero);
        System.out.println("Turno cancelado: " + describir(turno));
    }

    // ---------------------------------------------------------------- consultas

    private void consultarPendientes() {
        mostrarTurnos("Turnos pendientes (en orden de atención)", turnoService.listarPendientes(),
                "No hay turnos pendientes.");
    }

    private void consultarAtendidos() {
        mostrarTurnos("Turnos atendidos", reporteService.listarAtendidos(), "No hay turnos atendidos.");
    }

    private void consultarCancelados() {
        mostrarTurnos("Turnos cancelados", reporteService.listarCancelados(), "No hay turnos cancelados.");
    }

    private void consultarHistorialCliente() {
        int codigo = leerEntero("Código del cliente: ");
        Cliente cliente = clienteService.buscar(codigo);
        mostrarTurnos("Historial de turnos de " + cliente.getNombreCompleto(),
                reporteService.historialDeCliente(codigo), "El cliente no tiene turnos registrados.");
    }

    private void mostrarResumenPorServicio() {
        List<ResumenServicio> resumen = reporteService.resumenPorServicio();
        if (resumen.isEmpty()) {
            System.out.println("No hay servicios registrados.");
            return;
        }
        String formato = "%-26s %10s %10s %11s %7s%n";
        System.out.println("Resumen por servicio");
        System.out.printf(formato, "Servicio", "Pendientes", "Atendidos", "Cancelados", "Total");
        int pendientes = 0;
        int atendidos = 0;
        int cancelados = 0;
        for (ResumenServicio fila : resumen) {
            System.out.printf(formato, fila.nombreServicio(), fila.pendientes(), fila.atendidos(),
                    fila.cancelados(), fila.total());
            pendientes += fila.pendientes();
            atendidos += fila.atendidos();
            cancelados += fila.cancelados();
        }
        System.out.printf(formato, "Total", pendientes, atendidos, cancelados,
                pendientes + atendidos + cancelados);
    }

    private void mostrarTurnos(String titulo, List<Turno> turnos, String mensajeVacio) {
        if (turnos.isEmpty()) {
            System.out.println(mensajeVacio);
            return;
        }
        System.out.println(titulo);
        for (Turno turno : turnos) {
            System.out.println(describir(turno));
        }
    }

    private String describir(Turno turno) {
        Cliente cliente = clienteService.buscar(turno.getClienteCodigo());
        Servicio servicio = servicioService.buscar(turno.getServicioCodigo());
        StringBuilder texto = new StringBuilder();
        texto.append("Turno ").append(turno.getNumero())
                .append(" | Cliente: ").append(cliente.getCodigo()).append(" - ").append(cliente.getNombreCompleto())
                .append(" | Servicio: ").append(servicio.getNombre())
                .append(" | Generado: ").append(turno.getFechaGeneracion().format(FORMATO_FECHA))
                .append(" | Estado: ").append(turno.getEstado());
        if (turno.getFechaAtencion() != null) {
            texto.append(" | Atendido: ").append(turno.getFechaAtencion().format(FORMATO_FECHA));
        }
        if (turno.getFechaCancelacion() != null) {
            texto.append(" | Cancelado: ").append(turno.getFechaCancelacion().format(FORMATO_FECHA));
        }
        return texto.toString();
    }

    // ---------------------------------------------------------------- entrada de datos

    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (NegocioException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (AccesoDatosException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    private String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        String texto = leerLinea(mensaje);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new NegocioException("Debe ingresar un número entero válido.");
        }
    }

    private boolean leerActivarODesactivar() {
        String opcion = leerLinea("1. Activar  2. Desactivar: ");
        return switch (opcion) {
            case "1" -> true;
            case "2" -> false;
            default -> throw new NegocioException(OPCION_INVALIDA);
        };
    }

    private static Charset charsetDeEntrada() {
        String nombre = System.getProperty("stdin.encoding");
        try {
            return nombre != null ? Charset.forName(nombre) : Charset.defaultCharset();
        } catch (RuntimeException e) {
            return Charset.defaultCharset();
        }
    }
}
