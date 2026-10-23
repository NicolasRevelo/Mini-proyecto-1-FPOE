package co.edu.univalle.blackjack.modelo;

/** Jugada que se reconoce en una mano. */
public enum Jugada {
    NINGUNA,
    BLACKJACK,       // As + diez en las dos cartas iniciales
    VEINTIUNO,       // 21 que no es Blackjack
    VEINTE_Y_MEDIO,  // dos cartas iniciales que suman 14
    PASARSE          // mas de 21 despues de ajustar los ases
}
