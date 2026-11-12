# Sistema de Atención de Turnos

Aplicación de consola en Java 21 para administrar la atención de clientes de una oficina de servicios: gestión de clientes y servicios, generación de turnos en una cola FIFO, atención, cancelación y reportes. Usa SQLite mediante JDBC.

## Integrante

| Nombre | Carné |
|---|---|
| Manuel Alexander Osorio Aldana | 0909 23 11873 |

## Requisitos

- JDK 21.
- Visual Studio Code con la extensión **Extension Pack for Java**.
- Conexión a internet la primera vez, para que Maven descargue las dependencias (`sqlite-jdbc` y `slf4j-nop`).
- Maven es opcional: la extensión de Java de VS Code lo incluye para importar el proyecto. Solo se necesita instalarlo si se quiere ejecutar desde la terminal con `mvn`.

## Abrir y ejecutar en VS Code

1. Descomprimir el ZIP.
2. En VS Code: **Archivo → Abrir carpeta…** y elegir la carpeta del proyecto (la que contiene `pom.xml`).
3. Esperar a que la extensión de Java termine de importar el proyecto y descargar las dependencias.
4. Abrir `src/main/java/com/oficina/turnos/Main.java` y pulsar **Run** (▶) sobre el método `main`.

El programa debe ejecutarse con la carpeta del proyecto como directorio de trabajo, porque usa rutas relativas (`data/turnos.db` y `sql/crear_y_cargar.sql`). VS Code lo hace así por defecto al abrir la carpeta.

## Crear la base de datos desde el script

El script `sql/crear_y_cargar.sql` crea las tablas `clientes`, `servicios` y `turnos` y carga los 5 clientes y 3 servicios iniciales, sin turnos. No borra ni reinicia datos existentes.

- **Automático:** si se elimina `data/turnos.db` y se ejecuta la aplicación, esta detecta que faltan las tablas y ejecuta el script una sola vez. Así también se restablece la base antes de una demostración.
- **Con la herramienta `sqlite3`:**
  ```
  sqlite3 data/turnos.db ".read sql/crear_y_cargar.sql"
  ```
- **Con una herramienta gráfica** (extensión SQLite de VS Code, DB Browser for SQLite): abrir o crear `data/turnos.db` y ejecutar el contenido del script.

## Distribución del trabajo

El proyecto fue desarrollado por un único integrante, que se encargó de todas las partes.
Yo.


## Repositorio Git

Enlace: `<pegar aquí el enlace del repositorio>`
