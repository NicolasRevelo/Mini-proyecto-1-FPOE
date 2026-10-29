package co.edu.univalle.blackjack.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CartaTest {

    @Test
    @DisplayName("Las cartas del 2 al 10 valen su número y el As vale 1 como valor base")
    void valoresNumericos() {
        assertEquals(1, new Carta(Rango.AS, Palo.PICAS).getValor());
        assertEquals(2, new Carta(Rango.DOS, Palo.PICAS).getValor());
        assertEquals(9, new Carta(Rango.NUEVE, Palo.CORAZONES).getValor());
        assertEquals(10, new Carta(Rango.DIEZ, Palo.DIAMANTES).getValor());
    }

    @Test
    @DisplayName("J, Q y K valen 10")
    void figurasValenDiez() {
        assertEquals(10, new Carta(Rango.J, Palo.PICAS).getValor());
        assertEquals(10, new Carta(Rango.Q, Palo.PICAS).getValor());
        assertEquals(10, new Carta(Rango.K, Palo.PICAS).getValor());
    }

    @Test
    @DisplayName("El palo no cambia el valor de la carta")
    void paloNoAfectaValor() {
        for (Palo palo : Palo.values()) {
            assertEquals(7, new Carta(Rango.SIETE, palo).getValor());
        }
    }

    @Test
    @DisplayName("Dos cartas con mismo rango y palo son iguales; si difiere uno, son distintas")
    void igualdad() {
        Carta a = new Carta(Rango.K, Palo.PICAS);
        assertEquals(a, new Carta(Rango.K, Palo.PICAS));
        assertEquals(a.hashCode(), new Carta(Rango.K, Palo.PICAS).hashCode());
        assertFalse(a.equals(new Carta(Rango.K, Palo.CORAZONES)));
        assertFalse(a.equals(new Carta(Rango.Q, Palo.PICAS)));
    }

    @Test
    @DisplayName("Una carta no puede crearse sin rango o sin palo")
    void noAceptaNulos() {
        assertThrows(NullPointerException.class, () -> new Carta(null, Palo.PICAS));
        assertThrows(NullPointerException.class, () -> new Carta(Rango.AS, null));
    }

    @Test
    @DisplayName("Rango.getSimbolo coincide con el nombre de las imágenes (A, 2..10, J, Q, K)")
    void simbolosParaImagenes() {
        assertEquals("A", Rango.AS.getSimbolo());
        assertEquals("10", Rango.DIEZ.getSimbolo());
        assertEquals("K", Rango.K.getSimbolo());
        assertTrue(Rango.AS.esAs());
        assertFalse(Rango.K.esAs());
    }
}
