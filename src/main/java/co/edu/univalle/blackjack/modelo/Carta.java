package co.edu.univalle.blackjack.modelo;

import java.util.Objects;

/** Una carta de la baraja: un rango y un palo. Es inmutable. */
public class Carta {

    private final Rango rango;
    private final Palo palo;

    public Carta(Rango rango, Palo palo) {
        this.rango = Objects.requireNonNull(rango);
        this.palo = Objects.requireNonNull(palo);
    }

    public Rango getRango() { return rango; }
    public Palo getPalo() { return palo; }

    /** Valor base: As = 1, figuras = 10. */
    public int getValor() { return rango.getValor(); }

    // equals/hashCode permiten comprobar que no haya cartas repetidas en las pruebas.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carta)) return false;
        Carta otra = (Carta) o;
        return rango == otra.rango && palo == otra.palo;
    }

    @Override
    public int hashCode() { return Objects.hash(rango, palo); }

    @Override
    public String toString() { return rango.getSimbolo() + " de " + palo; }
}
