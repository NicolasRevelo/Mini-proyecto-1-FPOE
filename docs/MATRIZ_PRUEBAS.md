# Matriz de pruebas de la interfaz – 21 Black Jack

Pruebas manuales hechas por Persona B sobre la versión entregada. Se agrupan por los cinco bloques de la
rúbrica (B1 a B5). La columna **Esperado** sale de los criterios de aceptación de las historias de usuario.
**Obtenido** y **Resultado** se llenan al ejecutar cada caso (no se llenan de memoria).

- **Resultado:** `OK` si coincide con lo esperado, `FALLA` si no (y se anota qué pasó y a qué registro de error se enlaza).
- **Mano forzada:** las manos poco frecuentes (Blackjack, 14, 21 de tres cartas) se obtuvieron con un mazo de
  prueba temporal en el controlador (ver "Cómo se forzaron las manos" al final). Ese cambio no se sube al repositorio.

Fecha de la prueba: 10/29/2026 · Probó: Nicolás Revelo (Persona B) · 

## B1 – Apuestas y saldo (HU01, HU02, HU09)

| ID | HU | Pasos | Esperado | Obtenido | Resultado |
|---|---|---|---|---|---|
| P01 | HU01 | Abrir la aplicación | Saldo 1000, apuesta "-", manos vacías, Pedir carta y Plantarse deshabilitados; Iniciar ronda, Ver reglas y Cerrar habilitados; mensaje inicial visible |Se abre la aplicación y se obtiene saldo 1000, apuesta "-", botones deshabilitados menos ver reglas y cerrar| OK|
| P02 | HU02 | Dejar el campo vacío y pulsar Iniciar ronda | Mensaje que pide una apuesta; no se reparten cartas; saldo sigue en 1000 | No se reparten cartas y saldo sigue igual, sigue pidiendo que ingrese una apuesta|OK |
| P03 | HU02 | Escribir `abc` | Mensaje de que debe ser un número entero; sin cartas ni descuento |Mensaje "La apuesta debe ser un numero entero, sin letas ni decimales", no hay cartas y no se descuenta el saldo |OK |
| P04 | HU02 | Escribir `15` | Mensaje de que debe ser múltiplo de 10; sin cartas ni descuento |Mensaje "La apuesta debe ser multiplo de 10" |OK |
| P05 | HU02 | Escribir `0` | Mensaje de que debe ser mayor que cero |Mensaje "La apuesta debe ser mayor que cero" |OK |
| P06 | HU02 | Escribir `-10` | Mensaje de que debe ser mayor que cero |Mensaje "La apuesta debe ser mayor que cero" |OK |
| P07 | HU02 | Escribir `10,5` y luego `10.5` | Mensaje de número entero en ambos casos |Mensaje "La apuesta debe ser un número entero y sin decimales" |OK |
| P08 | HU02 | Escribir `5` | Mensaje de apuesta mínima de 10 |"La apuesta minima es de 10 fichas" |OK |
| P09 | HU02 | Escribir `1010` (saldo 1000) | Mensaje de que no puede superar el saldo |"La apuesta no puede superar el saldo de x fichas (x= a las que tiene el jugador) | OK|
| P10 | HU02 | Escribir ` 50 ` (con espacios) | Se acepta como 50 |Inicia ronda con 50 |OK |
| P11 | HU02 | Escribir `1000` e iniciar | Se acepta; el saldo durante la ronda es 0 |Pasa lo esperado | OK|
| P12 | HU03 | Apostar 100 e iniciar | Saldo 900, apuesta 100 visible; el descuento se hace una sola vez |Pasa lo esperado |OK |
| P13 | HU09 | Perder una ronda con apuesta 100 (saldo 1000) | Saldo final 900 |Saldo restante es 900 |OK |
| P14 | HU09 | Empatar con apuesta 100 | Saldo final 1000 |Se devuelve mi apuesta |OK |
| P15 | HU09 | Ganar (sin Blackjack) con apuesta 100 | Saldo final 1100 |Pasa lo esperado |OK |
| P16 | HU09 | Ganar con Blackjack y apuesta 100 | Saldo final 1150 |Pasa lo esperado porque con blackjack se da el 2.5 de tu apuesta |OK |
| P17 | HU09 | Al terminar una ronda, abrir Reglas, cerrar y esperar | El saldo no vuelve a cambiar (pago aplicado una sola vez) |Pasa lo esperado |OK |

## B2 – Mazo y rondas (HU03, HU10)

| ID | HU | Pasos | Esperado | Obtenido | Resultado |
|---|---|---|---|---|---|
| P18 | HU03 | Iniciar una ronda | El jugador recibe 2 cartas distintas, la imagen coincide con valor y palo y el puntaje es correcto; el computador no tiene cartas | Pasa lo esperado|OK |
| P19 | HU03 | Jugar 10 rondas seguidas mirando las cartas de cada una | En una misma ronda no hay cartas repetidas; las cartas cambian entre rondas | Pasa lo esperado|OK |
| P20 | HU03 | Hacer doble clic rápido en Iniciar ronda | Se inicia una sola ronda y se descuenta una sola vez | Pasa lo esperado|OK |
| P21 | HU10 | Terminar una ronda y apostar otra | Manos vacías al empezar, mensaje renovado, el saldo continúa (no vuelve a 1000) | Pasa lo esperado|OK |
| P22 | HU10 | Terminar una ronda con saldo menor de 10 | Mensaje de que no es posible seguir apostando; Iniciar ronda deshabilitado; Ver reglas y Cerrar siguen disponibles |Pasa lo esperado |OK |
| P23 | HU03 | Intentar cambiar la apuesta durante la ronda | El campo y el botón Iniciar ronda están deshabilitados | Los botones iniciar ronda está deshabilitado|OK |

## B3 – Puntajes y resultado (HU07, HU08)

| ID | HU | Pasos | Esperado | Obtenido | Resultado |
|---|---|---|---|---|---|
| P24 | HU07 | Mano As + 9, luego pedir un 5 | Puntaje 20 y después 15 |Puntaje 20 y despues 15 |OK |
| P25 | HU07 | Mano As + As + 9 | Puntaje 21 | 21|OK |
| P26 | HU07 | Mano As + As + K | Puntaje 12 | 12|OK |
| P27 | HU07 | Mano As + As + K + K | Puntaje 22 y pierde (se pasa) | pasa lo esperado|OK |
| P28 | HU07 | Mano As + K de salida | Se anuncia Blackjack y el turno pasa solo al computador | Pasa lo esperado|OK |
| P29 | HU07 | Mano 7 + 7 + 7 | Puntaje 21; no se trata como Blackjack | Pasa lo esperado|OK |
| P30 | HU07 | Mano 9 + 5 (o J + 4) de salida | Mensaje de Veinte y medio; aún se puede pedir o plantarse |Pasa lo esperado |OK |
| P31 | HU08 | Jugador 21 de tres cartas contra computador con Blackjack inicial | Gana el computador; mensaje con la causa |Pasa lo esperado |OK |
| P32 | HU08 | Blackjack de ambos | Empate; se devuelve la apuesta | Pasa lo esperado|OK |
| P33 | HU08 | Ambos con 18 | Empate; se devuelve la apuesta | Pasa lo esperado|OK |
| P34 | HU08 | Jugador con 20 y computador que se pasa | Gana el jugador; mensaje dice que el computador se pasó | Pasa lo esperado|OK |
| P35 | HU08 | Veinte y medio contra computador con 19 o menos | Gana el jugador (sin pago especial) | Gana el jugador|OK |
| P36 | HU08 | Veinte y medio contra computador con 21 | Pierde el jugador | Pierde el jugador|OK |
| P37 | HU08 | Jugador se pasa (K + 9 + 5) | Derrota inmediata; la zona del computador queda vacía; no se inventa una mano | Pasa lo esperado|OK |
| P38 | HU08 | Terminar cualquier ronda | El mensaje final dice victoria, derrota o empate y explica la causa; se ven manos y puntajes | Pasa lo esperado| OK|

## B4 – Turnos y rival (HU04, HU05, HU06)

| ID | HU | Pasos | Esperado | Obtenido | Resultado |
|---|---|---|---|---|---|
| P39 | HU04 | Pulsar Pedir carta una vez | Se agrega exactamente una carta, se muestra su imagen y el puntaje se actualiza | Pasa lo esperado|OK |
| P40 | HU04 | Pedir hasta pasarte de 21 | Pierde de inmediato; el computador no recibe cartas; Pedir y Plantarse quedan deshabilitados | Pasa lo esperado|OK |
| P41 | HU04 | Pedir hasta llegar a 21 exacto | El turno pasa solo al computador | Pasa lo esperado|OK |
| P42 | HU05 | Pulsar Plantarse | Se conservan cartas y puntaje; ambos botones deshabilitados; mensaje de cambio de turno; el computador recibe 2 cartas; no se descuenta otra apuesta | Pasa lo esperado|OK |
| P43 | HU05 | Pulsar Plantarse varias veces seguidas | El computador juega una sola vez; el saldo cambia una sola vez al final | Pasa lo esperado|OK |
| P44 | HU06 | Observar el turno del computador | Juega solo, un paso por segundo, sin clics adicionales | Pasa lo esperado|OK |
| P45 | HU06 | Computador con 16 o menos (repetir hasta verlo) | Pide carta | Pide una carta |OK |
| P46 | HU06 | Computador con 10 + 7 y con As + 6 | Se planta (17) | Pasa lo esperado|OK |
| P47 | HU06 | Mano inicial del computador con 14 | Pide carta (no se planta por tener Veinte y medio) | Pasa lo esperado|OK |
| P48 | HU04/05 | Durante el turno del computador y tras el resultado, intentar pulsar Pedir o Plantarse | Ambos deshabilitados; no cambia mano, mazo, apuesta ni saldo | Ambos deshabilitados|OK |

## B5 – Interacción (HU01, HU11, HU12)

| ID | HU | Pasos | Esperado | Obtenido | Resultado |
|---|---|---|---|---|---|
| P49 | HU11 | Abrir Ver reglas antes de empezar, en tu turno, durante el turno del computador y tras un resultado; cerrar | Saldo, apuesta, manos, turno, resultado y botones quedan exactamente igual; no se reparte carta ni se inicia ronda | Se muestra lo esperado|OK |
| P50 | HU11 | Leer la ventana de reglas | Incluye objetivo, valores, As, apuestas, pagos, jugadas, Veinte y medio = 14, prioridad del Blackjack y reglas del computador; coincide con lo implementado | la ventana de reglas muestra lo esperado|OK |
| P51 | HU12 | Cerrar juego, luego Cancelar (antes de apostar, en tu turno y tras un resultado) | La ventana sigue abierta y todo queda igual | La ventana sigue abierta y todo sigue igual|OK |
| P52 | HU12 | Pulsar la X de la ventana (en los tres momentos) | Pide confirmación; con Cancelar todo queda igual | Pide confirmación, si se pulsa ok cierra, si no todo queda igual|OK |
| P53 | HU12 | Cerrar juego (o la X) y confirmar Salir | La aplicación termina | La app se cierra|OK |
| P54 | HU01 | Reducir la ventana al tamaño mínimo | Se ven todos los botones, cartas y mensajes sin cortarse | Se ve como se espera|OK |
| P55 | HU01 | Revisar mensajes en todo el recorrido | Mensajes claros, con ortografía y espacios correctos | Mensajes claros, con ortografia y espacios correctos|OK |

## Resumen

| Bloque | Casos | OK | Falla | Observaciones |
|---|---|---|---|---|
| B1 Apuestas y saldo | 17 | 17 | 0 | Sin fallas. Se probaron todas las apuestas inválidas, el límite de 1000 y los cuatro pagos (900, 1000, 1100, 1150). |
| B2 Mazo y rondas | 6 | 6 | 0 | Sin fallas. Mazo sin repetidas en 10 rondas, doble clic y nueva ronda con saldo conservado. |
| B3 Puntajes y resultado | 15 | 15 | 0 | Sin fallas. Ases, jugadas especiales, prioridades y empates probados con mazo de orden fijo. |
| B4 Turnos y rival | 10 | 10 | 0 | Sin fallas. Pedir, plantarse, umbral de 17 del computador y bloqueo fuera de turno. |
| B5 Interacción | 7 | 7 | 0 | Sin fallas. Reglas, cierre con botón y con la X (cancelar y salir) sin alterar la partida. |
| **Total** | **55** | **55** | **0** | |

**Fallas encontradas y enlace a su registro de error:** en esta ronda de pruebas no se observaron fallas.
Antes de ella se detectaron y corrigieron dos errores (se detallan en los registros de errores y correcciones):
mensajes de resultado con espacios y puntuación mal formados en el modelo, y falta de anuncio de las jugadas
Blackjack y Veinte y medio en la interfaz (criterios 4.3 y 7.5).

## Cómo se forzaron las manos

Las cartas se reparten del mazo barajado, así que manos como As + K o 7 + 7 + 7 son raras. Para probarlas
se reemplazó, **solo en la máquina de quien probaba y sin subirlo al repositorio**, la línea
`private final Partida partida = new Partida();` del controlador por una partida con mazo de orden fijo.
`Mazo(Carta[])` y `Partida(Mazo)` existen justamente para pruebas. El orden en que se reparten las cartas es:
primero las dos del jugador, luego las que pida el jugador, luego las dos del computador y luego las que pida
el computador. Solo la primera ronda usa ese mazo; las siguientes usan un mazo barajado normal.

Ejemplo (Blackjack del jugador contra 20 del computador):

```java
private final Partida partida = new Partida(new Mazo(new Carta[] {
    new Carta(Rango.AS, Palo.PICAS), new Carta(Rango.K, Palo.CORAZONES),   // jugador
    new Carta(Rango.K, Palo.PICAS),  new Carta(Rango.Q, Palo.PICAS)        // computador
}));
```

Al terminar las pruebas se devuelve la línea original (`new Partida()`).
