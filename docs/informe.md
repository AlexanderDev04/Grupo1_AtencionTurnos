# Informe de la secuencia de comprobación

Sistema de Atención de Turnos — Manuel Alexander Osorio Aldana, carné 0909 23 11873.

La secuencia se ejecutó comenzando con los datos iniciales (5 clientes, 3 servicios y ningún turno). El paso 6 se realizó cerrando el programa con la opción 0 y volviéndolo a abrir.

## Resultados

| Paso | Operación | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| 1 | Buscar al cliente 1003 mediante el BST | Mostrar a María García | Cumple |
| 2 | Generar turnos 1001/1, 1002/2, 1003/1, 1004/3, 1005/2 | Turnos 1, 2, 3, 4 y 5 | Cumple |
| 3 | Otro turno para el cliente 1001 | Rechazado por turno pendiente | Cumple |
| 4 | Cancelar el turno 3 | Cola: 1, 2, 4 y 5 | Cumple |
| 5 | Atender dos turnos | Primero el 1 y después el 2 | Cumple |
| 6 | Cerrar y volver a abrir | Estados conservados; cola con 4 y 5 | Cumple |
| 7 | Atender los turnos restantes | Primero el 4 y después el 5 | Cumple |
| 8 | Intentar atender nuevamente | Informa que no hay turnos pendientes | Cumple |
| 9 | Historial del cliente 1003 | Turno 3 cancelado | Cumple |
| 10 | Resumen por servicio | Resumen esperado | Cumple |

## Capturas principales

![Pasos 1 a 3](captura1.jpg)

![Pasos 1 a 3, continuación](captura4.jpg)

![Pasos 4 y 5](captura2.jpg)

![Pasos 6 a 10](captura3.jpg)