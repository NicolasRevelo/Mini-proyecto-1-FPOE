package co.edu.univalle.blackjack.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Cartas de un participante.
 *
 * VERSION PROVISIONAL: usa un ArrayList para que la interfaz pueda probarse ya.
 * La Persona A debe reemplazar el almacenamiento interno por una LISTA ENLAZADA
 * (la exige el enunciado) SIN cambiar los metodos publicos de esta clase.
 */
public class Mano {

    private final List<Carta> cartas = new ArrayList<>();

    /** Agrega una carta al final de la mano. */
    public void agregar(Carta carta) { cartas.add(carta); }

    /** Deja la mano sin cartas (para una ronda nueva). */
    public void vaciar() { cartas.clear(); }

    public boolean estaVacia() { return cartas.isEmpty(); }

    public int getNumeroCartas() { return cartas.size(); }

    /** Copia de las cartas en orden, para que la vista dibuje una imagen por cada una. */
    public List<Carta> getCartas() { return new ArrayList<>(cartas); }

    /** Mejor puntaje posible sin pasarse de 21 (los ases valen 1 u 11). */
    public int puntaje() {
        int total = 0;
        boolean hayAs = false;
        for (Carta c : cartas) {
            total += c.getValor();
            if (c.getRango().esAs()) hayAs = true;
        }
        // Solo un as puede valer 11 a la vez: si cabe, se suman 10 mas.
        if (hayAs && total + 10 <= 21) total += 10;
        return total;
    }

    /** Blackjack: exactamente dos cartas que suman 21 (As + diez). */
    public boolean esBlackjack() { return cartas.size() == 2 && puntaje() == 21; }

    /**
     * Veinte y medio: dos cartas que suman 14.
     * PENDIENTE (A): confirmar con la profesora si cuenta As + 3 y si se pierde al pedir mas cartas.
     */
    public boolean esVeinteYMedio() { return cartas.size() == 2 && puntaje() == 14; }

    /** Mas de 21 despues de ajustar los ases. */
    public boolean sePaso() { return puntaje() > 21; }

    /** Jugada que corresponde a la mano en este momento. */
    public Jugada getJugada() {
        if (sePaso()) return Jugada.PASARSE;
        if (esBlackjack()) return Jugada.BLACKJACK;
        if (puntaje() == 21) return Jugada.VEINTIUNO;
        if (esVeinteYMedio()) return Jugada.VEINTE_Y_MEDIO;
        return Jugada.NINGUNA;
    }
}
