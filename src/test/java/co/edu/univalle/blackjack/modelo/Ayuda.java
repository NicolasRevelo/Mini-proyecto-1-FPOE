package co.edu.univalle.blackjack.modelo;

/**
 * Ayudas para las pruebas: arman cartas, manos y mazos de orden fijo a partir de rangos.
 * Como un mazo no puede tener dos cartas iguales, el palo se asigna rotando por cada repetición
 * del mismo rango (el primer 7 es de picas, el segundo de corazones, etc.). Los palos no cambian
 * el puntaje, así que no afectan lo que se prueba.
 */
final class Ayuda {

    private static final Palo[] PALOS = {Palo.PICAS, Palo.CORAZONES, Palo.DIAMANTES, Palo.TREBOLES};

    private Ayuda() { }

    /** Arma un arreglo de cartas con los rangos dados, en ese orden, sin repetir cartas. */
    static Carta[] cartas(Rango... rangos) {
        int[] repeticiones = new int[Rango.values().length];
        Carta[] resultado = new Carta[rangos.length];
        for (int i = 0; i < rangos.length; i++) {
            Rango rango = rangos[i];
            resultado[i] = new Carta(rango, PALOS[repeticiones[rango.ordinal()]++ % PALOS.length]);
        }
        return resultado;
    }

    /** Mazo de orden fijo: la primera carta que se reparte es la primera de la lista. */
    static Mazo mazo(Rango... rangos) {
        return new Mazo(cartas(rangos));
    }

    /** Mano con las cartas dadas. */
    static Mano mano(Rango... rangos) {
        Mano mano = new Mano();
        for (Carta carta : cartas(rangos)) {
            mano.agregar(carta);
        }
        return mano;
    }

    /**
     * Partida con saldo inicial de 1000 y un mazo de orden fijo. Las cartas se reparten así:
     * primero las dos del jugador, luego las que pida el jugador, luego las dos del computador
     * y luego las que pida el computador.
     */
    static Partida partida(Rango... rangos) {
        return new Partida(mazo(rangos));
    }

    /** Hace jugar al computador hasta que la ronda termine (con un tope por seguridad). */
    static void jugarComputador(Partida partida) {
        int pasos = 0;
        while (partida.getEstado() == EstadoRonda.TURNO_COMPUTADOR && pasos++ < 30) {
            partida.jugarPasoComputador();
        }
    }

    /** Inicia la ronda con la apuesta dada, el jugador se planta y el computador juega hasta el final. */
    static void rondaPlantandose(Partida partida, int apuesta) {
        partida.iniciarRonda(apuesta);
        partida.plantarse();
        jugarComputador(partida);
    }
}
