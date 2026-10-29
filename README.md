# 21 Black Jack – Duelo contra el computador

Mini Proyecto 1 de Fundamentos de Programación Orientada a Eventos (FPOE), Universidad del Valle, sede Meléndez.
Profesora: Dra. Paola Johanna Rodríguez Carrillo.

Juego de 21 Black Jack de un jugador contra el computador, hecho en Java con JavaFX y Maven, con el patrón MVC.
El jugador empieza con 1000 fichas, apuesta, pide cartas o se planta, y el computador juega su turno automáticamente.

## Integrantes

| Integrante | Rol | Usuario de GitHub | Código | Correo institucional |
|---|---|---|---|---|
| Nicolás Revelo Noreña | Persona B: vista y controlador | `NicolasRevelo` | 2537974 | nicolas.revelo@correounivalle.edu.co |
| Douglas Velez Monginaite | Persona A: modelo | `douglasvelez-crypto` | 2516737 | douglas.velez@correounivalle.edu.co |

- **Repositorio:** https://github.com/NicolasRevelo/Mini-proyecto-1-FPOE
- **Commit de la entrega:** _(completar con el enlace al commit final)_
- **Acceso de la docente:** _(completar con el usuario de GitHub de la profesora, agregada como colaboradora)_

## Requisitos

- **JDK 21** (el proyecto compila con `release 21`).
- **Maven**: no hace falta instalarlo; el proyecto incluye el Maven Wrapper (`mvnw` y `mvnw.cmd`).
- **JavaFX 21.0.6**: Maven lo descarga solo (dependencias `javafx-controls` y `javafx-fxml`).
- Conexión a internet la primera vez, para descargar las dependencias.
- IntelliJ IDEA es opcional; con él basta abrir el proyecto y recargar Maven.

## Cómo ejecutar

**Desde IntelliJ IDEA:**

1. Abrir la carpeta del proyecto y esperar a que termine de cargar Maven (si pide recargar, aceptar).
2. En File > Project Structure, comprobar que el SDK sea el JDK 21 y el nivel del lenguaje 21.
3. Abrir `src/main/java/co/edu/univalle/blackjack/Launcher.java` y pulsar el triángulo verde junto a `main`.

**Desde la terminal** (en la carpeta del proyecto):

```
mvnw.cmd javafx:run        (Windows)
./mvnw javafx:run          (Linux o macOS)
```

**Pruebas automáticas del modelo:**

```
mvnw.cmd test              (Windows)
./mvnw test                (Linux o macOS)
```

## Cómo se juega

1. Escribir una apuesta (entero, múltiplo de 10, mínimo 10 y no mayor al saldo) y pulsar **Iniciar ronda**.
2. Se descuenta la apuesta y el jugador recibe dos cartas. Con 21 o Blackjack el turno pasa solo al computador.
3. El jugador puede **Pedir carta** o **Plantarse**. Si se pasa de 21, pierde de inmediato.
4. El computador juega solo, un paso por segundo: recibe dos cartas, pide mientras tenga menos de 17 y se planta con 17 o más.
5. Se muestra quién ganó y por qué, y el saldo se actualiza una sola vez. Luego se puede apostar otra ronda.
6. **Ver reglas** muestra las reglas en cualquier momento sin alterar la partida. **Cerrar juego** (y la X de la ventana) piden confirmación.

**Reglas principales de esta adaptación:**

- Valores: del 2 al 10 su número, J, Q y K valen 10, el As vale 11 o 1 (el mayor puntaje sin pasarse de 21).
- **Blackjack:** As + una carta de valor 10 en las dos primeras cartas. **Veintiuno:** 21 que no es Blackjack.
- **Veinte y medio:** las dos primeras cartas suman 14. No da pago extra; le gana a cualquier mano de 20 o menos, pierde contra cualquier 21 (incluido el Blackjack) y contra el Blackjack.
- Prioridad: Blackjack > Veintiuno > Veinte y medio > cualquier otro puntaje.
- Pagos con apuesta de 100: derrota 900, empate 1000 (se devuelve), victoria 1100 (doble), victoria con Blackjack 1150 (2,5 veces).
- Si el jugador tiene Blackjack, solo se comprueba si el computador también lo tiene con sus dos cartas iniciales. Si ambos lo tienen, empatan.

## Estructura del proyecto (MVC)

```
src/main/java/co/edu/univalle/blackjack/
├── Launcher.java              arranque (llama a Application.launch)
├── BlackjackApp.java          abre la ventana y pide confirmación al cerrar con la X
├── BlackjackController.java   CONTROLADOR: lee la partida, repinta la pantalla y atiende los botones
└── modelo/                    MODELO (no depende de JavaFX)
    ├── Partida.java           saldo, apuesta, turnos, reglas del computador, resultado y pagos
    ├── Mazo.java              arreglo de 52 cartas; mezclar y repartir sin repetir
    ├── Mano.java              cartas de un participante en una lista enlazada; puntaje y jugadas
    ├── Nodo.java              nodo de la lista enlazada
    ├── Carta.java, Rango.java, Palo.java
    └── EstadoRonda.java, ResultadoRonda.java, Jugada.java   (enumeraciones)
src/main/resources/
├── co/edu/univalle/blackjack/blackjack-view.fxml   VISTA
└── imagenes/                  52 cartas y el dorso (PNG, <rango>_<palo>.png)
src/test/java/.../modelo/      pruebas JUnit 5 del modelo
docs/                          UML, contrato A–B, matriz de pruebas, registros y registro de uso de IA
```

**Decisiones de diseño:**

- La **vista no decide nada de las reglas**: el controlador le pregunta a `Partida` el estado y habilita o deshabilita los botones con `puedeIniciarRonda()`, `puedePedirCarta()` y `puedePlantarse()`.
- El **turno del computador** se ejecuta paso a paso con `jugarPasoComputador()`, llamado con una pausa de un segundo (`PauseTransition`), para que se vea y no congele la ventana.
- El **mazo** es un arreglo de 52 posiciones y cada **mano** es una lista enlazada, como pide el enunciado.
- El modelo se puede probar sin pantalla: `Mazo` acepta un orden fijo de cartas, y así se arman manos exactas (As + 10, K + 9 + 5, etc.).
- **Veinte y medio** se implementó como "las dos primeras cartas suman 14", según las historias de usuario. El enunciado lo describe como figura + 4; en `Mano` existe un interruptor (`VEINTE_Y_MEDIO_SOLO_FIGURA_Y_CUATRO`) para cambiarlo si la docente lo indica.

## Pruebas

- **Pruebas JUnit del modelo** (`src/test/java`): `CartaTest`, `ManoTest`, `MazoTest` y `PartidaTest`. Cubren valores, ases, Blackjack, Veintiuno, Veinte y medio, pasarse, mazo sin cartas repetidas, validación de apuestas, turnos, reglas del computador, resultados, pagos, rondas consecutivas y 2000 rondas aleatorias.
- **Matriz de pruebas de la interfaz:** [`docs/MATRIZ_PRUEBAS.md`](docs/MATRIZ_PRUEBAS.md), con los 55 casos probados a mano en la aplicación.

## Documentación

| Archivo | Contenido |
|---|---|
| [`CONTRATO.md`](CONTRATO.md) | Acuerdo entre modelo (A) y vista/controlador (B): clases y métodos |
| [`docs/UML_inicial.png`](docs/UML_inicial.png) y [`docs/UML_final.png`](docs/UML_final.png) | Diagrama de clases al inicio y al final |
| [`docs/MATRIZ_PRUEBAS.md`](docs/MATRIZ_PRUEBAS.md) | Matriz de pruebas de la interfaz |
| [`docs/REGISTRO_IA.md`](docs/REGISTRO_IA.md) | Registro del uso de IA durante el proyecto |
| [`docs/generar_cartas.py`](docs/generar_cartas.py) | Script con el que se dibujaron las imágenes de las cartas |

## Uso de IA

Se usó Claude (Anthropic) como apoyo para planear el trabajo, definir el contrato entre el modelo y la vista,
explicar el código, revisar el trabajo del otro integrante y preparar documentos. Cada uso se registró en
[`docs/REGISTRO_IA.md`](docs/REGISTRO_IA.md), con lo que se pidió, lo que se recibió, lo que se cambió y cómo se verificó.
