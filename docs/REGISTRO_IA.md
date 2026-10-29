# Registro de uso de IA – Miniproyecto 1: 21 Black Jack (FPOE)

Este archivo se llena **en el momento** en que se usa una IA, no al final. Sirve de base para la
declaración de uso de IA del informe. Cada entrada dice qué se pidió, qué se recibió, qué se
cambió o se descartó y cómo se comprobó que funcionaba.

**Herramienta usada hasta ahora:** Claude (Anthropic).

**Cómo llenarlo:** una fila por uso. Si se usó el resultado sin cambios, se escribe "sin cambios".
Si se modificó o se descartó, se explica qué y por qué. En "Verificación" va lo que hicimos nosotros
para comprobar el resultado (compilar, ejecutar, comparar con el enunciado o con las historias).

| # | Fecha | Quién | Para qué se usó | Qué se recibió | Qué se cambió o descartó | Verificación |
| 1 | 2026-10-22 | Nicolás | Plan de trabajo de 7 días a partir del enunciado, las historias de usuario y la rúbrica | PDF con reparto A/B, calendario, entregables y casos de prueba |  |  se contrastó con la rúbrica y el enunciado y está bien |
| 2 | 2026-10-22 | Nicolás | Contrato entre el modelo (A) y la vista/controlador (B) y una versión provisional del modelo | `CONTRATO.md` y 8 clases Java con una partida "falsa" (3 escenarios fijos) | Se cambió `plantarse()` para que el turno del computador sea paso a paso (`jugarPasoComputador()`) | Se compiló y se recorrió una ronda completa por consola |
**(el contrato es donde llegamos a acuerdos ambos y declaramos los nombres de clases y metodos, se sube siempre con un commit)
## Entradas nuevas
-NICOLÁS: Se le pide a la IA que cree las 8 clases de una aprtida simulada para que B con estos datos pueda trabajar sin esperar a A
-NICOLÁS: Se le pide a la IA que genera las 52 imágenes de las cartas, lo genera con un script de python, incluido en docs
-NICOLÁS: Se le pide a la IA que explique un poco cómo se conectan los archivos blackjack-view.fxml, BlackjackController.java y BlackjackApp.java, le pedí que comprobara los criterios de la HU01
-NICOLÁS: escribí el codigo de 'onIniciarRonda', 'dibujarMano'y 'crearImagenCarta'  en el controlador. | Claude me indicó mal dónde pegar los métodos y quedaron dentro de `actualizarVista()`. Lo detecté por los errores en rojo de IntelliJ, moví la llave y quedó bien. | Compilé y comprobé que salen las cartas, el saldo baja y el puntaje cambia |
-NICOLÁS: Conectar "Pedir carta" (HU04) | Claude dio el método `onPedirCarta` y el `onAction` del FXML. Yo lo agregué. | Claude había dicho que el botón ya agregaba cartas, pero todavía no estaba conectado. Lo noté al probarlo y lo conecte | Probé: cartas K, 5, 4, 9 = 28, derrota y botones que ya no se pudieran hundir |
|3|2026-10-28|Douglas|Para averiguar como hacer que el jugador tuviera dos cartas al principio del juego|se recibio un for para el comand de manoJugador.agregar(mazo.repartir()) pero preferí simplemente repetir el comando dos veces.
|4|2026-10-28|Douglas|No supe como hacer que funcione la jugade el mismo nivel en Partida.java para el modelo.|Claude me dio una serie de else con los ResultadoRonda, lo veo funcional asi que lo agrego al codigo.
|5|2026-10-28|Douglas|Mi compañero me deja un pendiente sobre si cuenta el AS +3 y si se pierde al pedir mas cartas.| Claude dice que no es necesario el arreglo, per por si acaso agrego un switch que se puede prendar en caso de no necesitarlo, mejor que sobre a que falte.
|6|2026-10-29|Douglas| No tengo una idea clar de como realizar la función mezclar en el archivo de Mazo|Claude me arroja un codigo con un for anidado para realizar la mezcla aleatoria|Cambio las variables que se presentan en el ejemplo de claude y simplifico la logica