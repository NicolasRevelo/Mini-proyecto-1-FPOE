package co.edu.univalle.blackjack.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas de la mano: puntajes, ajuste de ases y reconocimiento de jugadas (HU07). */
class ManoTest {

    // ---------- Puntaje ----------

    @Test
    @DisplayName("Una mano vacía no tiene puntaje ni cartas")
    void manoVacia() {
        Mano mano = new Mano();
        assertTrue(mano.estaVacia());
        assertEquals(0, mano.getNumeroCartas());
        assertEquals(0, mano.puntaje());
        assertFalse(mano.sePaso());
    }

    @Test
    @DisplayName("Los números valen su valor y J, Q, K valen 10")
    void valoresBasicos() {
        assertEquals(15, Ayuda.mano(Rango.CINCO, Rango.K).puntaje());
        assertEquals(20, Ayuda.mano(Rango.J, Rango.Q).puntaje());
        assertEquals(19, Ayuda.mano(Rango.NUEVE, Rango.DIEZ).puntaje());
    }

    @Test
    @DisplayName("Un As: As + 9 = 20 y al agregar un 5 pasa a 15")
    void unAs() {
        Mano mano = Ayuda.mano(Rango.AS, Rango.NUEVE);
        assertEquals(20, mano.puntaje());
        mano.agregar(Ayuda.cartas(Rango.CINCO)[0]);
        assertEquals(15, mano.puntaje());
    }

    @Test
    @DisplayName("Varios ases: As + As + 9 = 21")
    void dosAsesYNueve() {
        assertEquals(21, Ayuda.mano(Rango.AS, Rango.AS, Rango.NUEVE).puntaje());
    }

    @Test
    @DisplayName("Varios ases: As + As + K = 12")
    void dosAsesYFigura() {
        assertEquals(12, Ayuda.mano(Rango.AS, Rango.AS, Rango.K).puntaje());
    }

    @Test
    @DisplayName("Varios ases: As + As + K + K = 22 y se pasa")
    void dosAsesYDosFiguras() {
        Mano mano = Ayuda.mano(Rango.AS, Rango.AS, Rango.K, Rango.K);
        assertEquals(22, mano.puntaje());
        assertTrue(mano.sePaso());
    }

    @Test
    @DisplayName("Si todas las combinaciones se pasan, todos los ases cuentan 1")
    void todosLosAsesComoUno() {
        // 4 ases (4) + K + Q + J = 34 con todos en 1
        Mano mano = Ayuda.mano(Rango.AS, Rango.AS, Rango.AS, Rango.AS, Rango.K, Rango.Q, Rango.J);
        assertEquals(34, mano.puntaje());
        assertTrue(mano.sePaso());
    }

    @Test
    @DisplayName("Cuatro ases valen 14 (uno como 11 y tres como 1)")
    void cuatroAses() {
        assertEquals(14, Ayuda.mano(Rango.AS, Rango.AS, Rango.AS, Rango.AS).puntaje());
    }

    @Test
    @DisplayName("El As se ajusta de 11 a 1 cuando llega otra carta que haría pasarse")
    void asSeAjustaAlPedir() {
        Mano mano = Ayuda.mano(Rango.AS, Rango.SEIS);      // 17 (As como 11)
        assertEquals(17, mano.puntaje());
        mano.agregar(Ayuda.cartas(Rango.K)[0]);             // 1 + 6 + 10 = 17
        assertEquals(17, mano.puntaje());
        assertFalse(mano.sePaso());
    }

    @Test
    @DisplayName("Los palos no cambian el puntaje")
    void palosNoCuentan() {
        Mano mano = new Mano();
        mano.agregar(new Carta(Rango.K, Palo.CORAZONES));
        mano.agregar(new Carta(Rango.SIETE, Palo.TREBOLES));
        assertEquals(17, mano.puntaje());
    }

    // ---------- Jugadas ----------

    @Test
    @DisplayName("As + 10 (y As + figura) es Blackjack")
    void blackjack() {
        assertTrue(Ayuda.mano(Rango.AS, Rango.DIEZ).esBlackjack());
        assertTrue(Ayuda.mano(Rango.K, Rango.AS).esBlackjack());
        assertEquals(Jugada.BLACKJACK, Ayuda.mano(Rango.AS, Rango.Q).getJugada());
    }

    @Test
    @DisplayName("Un 21 de tres cartas no es Blackjack, es Veintiuno")
    void veintiunoNoEsBlackjack() {
        Mano siete = Ayuda.mano(Rango.SIETE, Rango.SIETE, Rango.SIETE);
        assertEquals(21, siete.puntaje());
        assertFalse(siete.esBlackjack());
        assertEquals(Jugada.VEINTIUNO, siete.getJugada());

        Mano conAs = Ayuda.mano(Rango.AS, Rango.CINCO, Rango.CINCO);
        assertFalse(conAs.esBlackjack());
        assertEquals(Jugada.VEINTIUNO, conAs.getJugada());
    }

    @Test
    @DisplayName("Dos cartas que suman 14 son Veinte y medio (9+5, 8+6, 10+4, J+4)")
    void veinteYMedio() {
        assertTrue(Ayuda.mano(Rango.NUEVE, Rango.CINCO).esVeinteYMedio());
        assertTrue(Ayuda.mano(Rango.OCHO, Rango.SEIS).esVeinteYMedio());
        assertTrue(Ayuda.mano(Rango.DIEZ, Rango.CUATRO).esVeinteYMedio());
        assertTrue(Ayuda.mano(Rango.J, Rango.CUATRO).esVeinteYMedio());
        assertEquals(Jugada.VEINTE_Y_MEDIO, Ayuda.mano(Rango.NUEVE, Rango.CINCO).getJugada());
    }

    @Test
    @DisplayName("Veinte y medio solo cuenta con las dos primeras cartas")
    void veinteYMedioSoloConDosCartas() {
        assertFalse(Ayuda.mano(Rango.DOS, Rango.DOS, Rango.DIEZ).esVeinteYMedio());   // 14 con tres cartas
        Mano mano = Ayuda.mano(Rango.J, Rango.CUATRO);
        assertTrue(mano.esVeinteYMedio());
        mano.agregar(Ayuda.cartas(Rango.DOS)[0]);                                      // ahora son tres cartas
        assertFalse(mano.esVeinteYMedio());
    }

    @Test
    @DisplayName("Dos cartas que no suman 14 no son Veinte y medio")
    void noEsVeinteYMedio() {
        assertFalse(Ayuda.mano(Rango.K, Rango.CINCO).esVeinteYMedio());   // 15
        assertFalse(Ayuda.mano(Rango.K, Rango.TRES).esVeinteYMedio());    // 13
    }

    @Test
    @DisplayName("Pasarse es tener más de 21 después de ajustar los ases (K + 9 + 5 = 24)")
    void pasarse() {
        Mano mano = Ayuda.mano(Rango.K, Rango.NUEVE, Rango.CINCO);
        assertEquals(24, mano.puntaje());
        assertTrue(mano.sePaso());
        assertEquals(Jugada.PASARSE, mano.getJugada());
    }

    @Test
    @DisplayName("21 exacto no es pasarse")
    void veintiunoNoEsPasarse() {
        assertFalse(Ayuda.mano(Rango.K, Rango.CINCO, Rango.SEIS).sePaso());
    }

    @Test
    @DisplayName("Una mano común no tiene jugada especial")
    void sinJugada() {
        assertEquals(Jugada.NINGUNA, Ayuda.mano(Rango.K, Rango.OCHO).getJugada());
    }

    // ---------- Lista enlazada ----------

    @Test
    @DisplayName("agregar conserva el orden de llegada y cuenta las cartas")
    void ordenYConteo() {
        Carta[] cartas = Ayuda.cartas(Rango.TRES, Rango.K, Rango.AS);
        Mano mano = new Mano();
        for (Carta carta : cartas) {
            mano.agregar(carta);
        }
        assertEquals(3, mano.getNumeroCartas());
        assertEquals(List.of(cartas), mano.getCartas());
    }

    @Test
    @DisplayName("getCartas devuelve una copia: modificarla no cambia la mano")
    void getCartasEsCopia() {
        Mano mano = Ayuda.mano(Rango.K, Rango.OCHO);
        mano.getCartas().clear();
        assertEquals(2, mano.getNumeroCartas());
        assertEquals(2, mano.getCartas().size());
    }

    @Test
    @DisplayName("vaciar deja la mano sin cartas ni puntaje y permite volver a usarla")
    void vaciar() {
        Mano mano = Ayuda.mano(Rango.K, Rango.OCHO);
        mano.vaciar();
        assertTrue(mano.estaVacia());
        assertEquals(0, mano.getNumeroCartas());
        assertEquals(0, mano.puntaje());
        mano.agregar(Ayuda.cartas(Rango.CINCO)[0]);
        assertEquals(5, mano.puntaje());
    }

    @Test
    @DisplayName("No se puede agregar una carta null")
    void agregarNull() {
        assertThrows(IllegalArgumentException.class, () -> new Mano().agregar(null));
    }
}
