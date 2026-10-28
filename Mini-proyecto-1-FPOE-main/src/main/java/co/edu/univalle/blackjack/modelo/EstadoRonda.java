package co.edu.univalle.blackjack.modelo;

/** Momento en que se encuentra el juego. La vista habilita los botones segun este estado. */
public enum EstadoRonda {
    ESPERANDO_APUESTA,  // todavia no se ha iniciado ninguna ronda
    TURNO_JUGADOR,      // el usuario puede pedir carta o plantarse
    TURNO_COMPUTADOR,   // el computador juega (el controlador llama jugarPasoComputador())
    RONDA_TERMINADA     // hay resultado; se puede apostar e iniciar otra ronda
}
