package co.edu.univalle.blackjack.modelo;

/**
 * Rango (número o figura) de una carta.
 * El As vale 1 aquí; el ajuste a 11 lo decide la Mano al calcular el puntaje.
 */
public enum Rango {
    AS("A", 1), DOS("2", 2), TRES("3", 3), CUATRO("4", 4), CINCO("5", 5),
    SEIS("6", 6), SIETE("7", 7), OCHO("8", 8), NUEVE("9", 9), DIEZ("10", 10),
    J("J", 10), Q("Q", 10), K("K", 10);

    private final String simbolo;
    private final int valor;

    Rango(String simbolo, int valor) {
        this.simbolo = simbolo;
        this.valor = valor;
    }

    /** Texto corto para mostrar o para armar el nombre de la imagen: "A", "2"... "10", "J", "Q", "K". */
    public String getSimbolo() { return simbolo; }

    /** Valor base de la carta (As = 1, figuras = 10). */
    public int getValor() { return valor; }

    public boolean esAs() { return this == AS; }
}
