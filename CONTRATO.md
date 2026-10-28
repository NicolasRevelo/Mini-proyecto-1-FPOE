# Contrato entre Modelo (A) y Vista/Controlador (B)

Miniproyecto 1 – 21 Black Jack (FPOE). Este archivo se guarda en el repositorio
y es la referencia de los dos. **Si alguien quiere cambiar un método de aquí, lo
habla con el otro, actualiza este archivo y lo sube con un commit.**

## 1. Dónde están los archivos

Los 8 archivos del modelo ya están en el repositorio, en
`src/main/java/co/edu/univalle/blackjack/modelo/`. Su primera línea es
`package co.edu.univalle.blackjack.modelo;`, y el paquete base del proyecto es
`co.edu.univalle.blackjack`.

- **No hace falta tocar `module-info.java`**: `modelo` es un subpaquete del mismo módulo.
- Si en el futuro se crea otro paquete, se avisa y se actualiza este archivo.

## 2. Las reglas del contrato

- **B (vista/controlador) solo lee y pide.** Nunca modifica saldo, manos ni estado
  directamente: llama a un método de `Partida` y luego vuelve a leer.
- **A (modelo) conserva las firmas** de los métodos públicos de esta lista. Puede cambiar
  todo lo de adentro (mazo, lista enlazada, reglas) sin avisar mientras la firma no cambie.
- **Acciones fuera de turno no hacen nada** (se ignoran, no lanzan error). Así un doble clic
  o un botón mal habilitado no daña la partida.
- **El modelo no sabe nada de JavaFX.** Ninguna clase de `modelo` importa `javafx.*`.

## 3. Clases y métodos

### `Partida` (la clase principal)

| Método | Qué hace |
|---|---|
| `int getSaldo()` | Saldo actual del jugador |
| `int getApuesta()` | Apuesta de la ronda actual (0 si no ha empezado ninguna) |
| `EstadoRonda getEstado()` | Momento del juego (ver sección 4) |
| `Mano getManoJugador()` | Mano del jugador |
| `Mano getManoComputador()` | Mano del computador (vacía mientras no ha jugado) |
| `ResultadoRonda getResultado()` | `SIN_RESULTADO`, `VICTORIA`, `DERROTA` o `EMPATE` |
| `String getMensajeResultado()` | Explica quién ganó y por qué ("" hasta que termine la ronda) |
| `int getPagoRonda()` | Fichas acreditadas al terminar (0, apuesta, 2×, 2,5×) |
| `int getGananciaNeta()` | Pago menos apuesta (−100, 0, +100, +150 con apuesta de 100) |
| `boolean haySaldoSuficiente()` | `true` si el saldo es de al menos 10 |
| `boolean puedeIniciarRonda()` | Se puede apostar e iniciar (estado correcto y saldo ≥ 10) |
| `boolean puedePedirCarta()` | `true` solo en el turno del jugador |
| `boolean puedePlantarse()` | `true` solo en el turno del jugador |
| `String validarApuesta(String texto)` | `null` si es válida; si no, mensaje que explica el error. **No modifica nada** |
| `void iniciarRonda(int apuesta)` | Descuenta la apuesta, mezcla y reparte 2 cartas al jugador |
| `void pedirCarta()` | Una carta más para el jugador |
| `void plantarse()` | Cierra el turno del jugador; el estado pasa a `TURNO_COMPUTADOR` |
| `void jugarPasoComputador()` | Ejecuta **un** paso del turno del computador (ver sección 5) |

Constantes: `SALDO_INICIAL = 1000`, `APUESTA_MINIMA = 10`, `MULTIPLO_APUESTA = 10`.

### `Mano`

| Método | Qué hace |
|---|---|
| `void agregar(Carta c)` | Agrega una carta al final |
| `void vaciar()` | Deja la mano sin cartas |
| `boolean estaVacia()` | ¿No tiene cartas? |
| `int getNumeroCartas()` | Cantidad de cartas |
| `List<Carta> getCartas()` | Copia de las cartas en orden, para dibujarlas |
| `int puntaje()` | Mejor puntaje sin pasarse (ases como 1 u 11) |
| `boolean esBlackjack()` | Dos cartas que suman 21 |
| `boolean esVeinteYMedio()` | Dos cartas que suman 14 |
| `boolean sePaso()` | Más de 21 tras ajustar los ases |
| `Jugada getJugada()` | `NINGUNA`, `BLACKJACK`, `VEINTIUNO`, `VEINTE_Y_MEDIO` o `PASARSE` |

### `Carta`, `Rango`, `Palo`

- `Carta.getRango()`, `Carta.getPalo()`, `Carta.getValor()` (As = 1, figuras = 10).
- `Rango.getSimbolo()` devuelve `"A"`, `"2"` … `"10"`, `"J"`, `"Q"`, `"K"`.
- `Palo` es `CORAZONES`, `DIAMANTES`, `TREBOLES` o `PICAS`.

### Enums

- `EstadoRonda`: `ESPERANDO_APUESTA`, `TURNO_JUGADOR`, `TURNO_COMPUTADOR`, `RONDA_TERMINADA`
- `ResultadoRonda`: `SIN_RESULTADO`, `VICTORIA`, `DERROTA`, `EMPATE`
- `Jugada`: `NINGUNA`, `BLACKJACK`, `VEINTIUNO`, `VEINTE_Y_MEDIO`, `PASARSE`

## 4. Estados y transiciones

```
ESPERANDO_APUESTA ──iniciarRonda──▶ TURNO_JUGADOR ──plantarse──▶ TURNO_COMPUTADOR
        ▲                               │  │                          │
        │                   pedirCarta  │  └─ llega a 21 ────────────▶│
        │                   (se pasa)   ▼                             │ jugarPasoComputador (varias veces)
        │                         RONDA_TERMINADA ◀───────────────────┘
        └──────────────── iniciarRonda (nueva apuesta) ───────────────
```

- Si el jugador **se pasa**, la ronda termina de inmediato y el computador no recibe cartas.
- Si el jugador saca **21 o Blackjack** (al repartir o al pedir), el estado pasa solo a `TURNO_COMPUTADOR`.
- Desde `RONDA_TERMINADA` se puede llamar `iniciarRonda` otra vez: las manos de la ronda
  anterior siguen visibles hasta ese momento (así se muestra el resultado) y se vacían al empezar la nueva.

## 5. El turno del computador, paso a paso

Para que la pantalla muestre el turno como una secuencia breve, el computador **no juega
todo de una vez**. El controlador llama `jugarPasoComputador()` cada cierto tiempo:

- **Primera llamada:** el computador recibe sus dos cartas (y se revisa si hay Blackjack).
- **Siguientes llamadas:** pide una carta por llamada mientras tenga menos de 17.
- Cuando termina, el estado cambia a `RONDA_TERMINADA` y el resultado ya está listo.

> **Cambio respecto al documento del plan:** allí decía que `plantarse()` ejecutaba todo el
> turno del computador. Quedó mejor así: `plantarse()` solo cambia el estado y el
> controlador va llamando `jugarPasoComputador()`. Es lo que permite ver las cartas aparecer una a una.


## 7. Lo que NO está en el contrato (es solo de B)

- **Ver reglas** y **Cerrar juego con confirmación** (HU11 y HU12) no necesitan nada del modelo:
  son ventanas y diálogos de la vista. El texto de las reglas debe coincidir con lo que A implemente.
- Los mensajes de turno ("Tu turno...", "Turno del computador...") los arma el controlador.
  El modelo solo da el motivo del resultado (`getMensajeResultado()`).

## 8. Lo que debe hacer la Persona A con estos archivos

1. **`Mano`**: cambiar el `ArrayList` por una lista enlazada propia (la del ejercicio de clase),
   sin cambiar los métodos públicos.
2. **`Mazo`** (clase nueva): arreglo de 52 posiciones, `mezclar()`, `Carta repartir()`.
   Debe tener un constructor que reciba un orden fijo de cartas, para las pruebas.
3. **`Partida`**: quitar el guion falso y usar el `Mazo`. Implementar las reglas completas
   (prioridades de Veinte y medio, Blackjack inicial del computador, etc.).
4. Agregar a `Partida` un constructor para pruebas que reciba un `Mazo` ya armado.
5. Escribir pruebas JUnit con esos mazos de orden fijo (casos de la tabla de las historias de usuario).

Cuando A suba la versión real, B no debería tener que cambiar casi nada en el controlador.

## 9. Qué hace la versión falsa de `Partida`

Rota entre 3 escenarios, uno por ronda, para que B vea todos los casos en pantalla:

| Ronda | Escenario | Qué se ve |
|---|---|---|
| 1 | Normal | Jugador K+5 (15). Si se planta pierde 15 contra 19; si pide una carta llega a 19 y empata; si pide otra se pasa con 28 |
| 2 | Blackjack | Jugador A+K. El computador recibe 9+7 y no juega más. Ganancia de +150 |
| 3 | Veinte y medio | Jugador J+4 (14). Si se planta contra 10+8 gana por Veinte y medio |
| 4 | vuelve al 1 | … y así sucesivamente |

Las apuestas se validan con las reglas reales (vacío, texto, decimales, cero, negativos, no múltiplos de 10,
mayores al saldo), así que ya se pueden probar todos los mensajes de error.

## 10. Preguntas pendientes para la profesora

1. ¿Veinte y medio es solo "figura + 4" (enunciado) o cualquier suma de 14 en las dos primeras cartas
   (historias de usuario)? La versión provisional usa "suma 14".
2. Si el jugador pide carta después de un 14 inicial, ¿pierde el Veinte y medio? La versión provisional dice que sí.
