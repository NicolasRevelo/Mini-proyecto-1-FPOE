package co.edu.univalle.blackjack.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas del mazo: 52 cartas, mezcla, reparto sin repetir y mazo de orden fijo para pruebas (HU03). */
class MazoTest {

    private static List<Carta> repartirTodo(Mazo mazo) {
        List<Carta> cartas = new ArrayList<>();
        while (mazo.cartasRestantes() > 0) {
            cartas.add(mazo.repartir());
        }
        return cartas;
    }

    @Test
    @DisplayName("Un mazo nuevo tiene 52 cartas distintas")
    void mazoCompleto() {
        Mazo mazo = new Mazo();
        assertEquals(52, mazo.cartasRestantes());
        Set<Carta> distintas = new HashSet<>(repartirTodo(mazo));
        assertEquals(52, distintas.size());
    }

    @Test
    @DisplayName("Cada combinación de rango y palo aparece exactamente una vez, sin comodines")
    void todasLasCombinaciones() {
        Mazo mazo = new Mazo();
        mazo.mezclar();
        Set<Carta> cartas = new HashSet<>(repartirTodo(mazo));
        for (Palo palo : Palo.values()) {
            for (Rango rango : Rango.values()) {
                assertTrue(cartas.contains(new Carta(rango, palo)), "Falta " + rango + " de " + palo);
            }
        }
    }

    @Test
    @DisplayName("repartir descuenta una carta del mazo cada vez")
    void repartirDescuenta() {
        Mazo mazo = new Mazo();
        mazo.repartir();
        assertEquals(51, mazo.cartasRestantes());
        mazo.repartir();
        assertEquals(50, mazo.cartasRestantes());
    }

    @Test
    @DisplayName("Repartir las 52 cartas no repite ninguna y la carta 53 lanza excepción")
    void agotarElMazo() {
        Mazo mazo = new Mazo();
        mazo.mezclar();
        Set<Carta> vistas = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            assertTrue(vistas.add(mazo.repartir()), "Se repitió una carta");
        }
        assertEquals(0, mazo.cartasRestantes());
        assertThrows(IllegalStateException.class, mazo::repartir);
    }

    @Test
    @DisplayName("mezclar conserva las 52 cartas distintas y reinicia el reparto")
    void mezclarConservaCartas() {
        Mazo mazo = new Mazo();
        mazo.repartir();
        mazo.repartir();
        mazo.mezclar();
        assertEquals(52, mazo.cartasRestantes());
        assertEquals(52, new HashSet<>(repartirTodo(mazo)).size());
    }

    @Test
    @DisplayName("Dos mazos mezclados salen en distinto orden")
    void mezclaCambiaElOrden() {
        Mazo a = new Mazo();
        Mazo b = new Mazo();
        a.mezclar();
        b.mezclar();
        assertFalse(repartirTodo(a).equals(repartirTodo(b)));
    }

    @Test
    @DisplayName("Un mazo mezclado queda en distinto orden que el mazo sin mezclar")
    void mezclaCambiaRespectoAlOrdenFijo() {
        Mazo ordenado = new Mazo();
        Mazo mezclado = new Mazo();
        mezclado.mezclar();
        assertFalse(repartirTodo(ordenado).equals(repartirTodo(mezclado)));
    }

    // ---------- Mazo de orden fijo (para las pruebas) ----------

    @Test
    @DisplayName("Con orden fijo, la primera carta del arreglo es la primera que se reparte")
    void ordenFijo() {
        Carta[] orden = Ayuda.cartas(Rango.AS, Rango.K, Rango.SIETE);
        Mazo mazo = new Mazo(orden);
        assertEquals(3, mazo.cartasRestantes());
        assertSame(orden[0], mazo.repartir());
        assertSame(orden[1], mazo.repartir());
        assertSame(orden[2], mazo.repartir());
        assertThrows(IllegalStateException.class, mazo::repartir);
    }

    @Test
    @DisplayName("Un mazo de orden fijo rechaza cartas repetidas")
    void ordenFijoRechazaRepetidas() {
        Carta[] repetidas = {new Carta(Rango.AS, Palo.PICAS), new Carta(Rango.AS, Palo.PICAS)};
        assertThrows(IllegalArgumentException.class, () -> new Mazo(repetidas));
    }

    @Test
    @DisplayName("Un mazo de orden fijo rechaza arreglos vacíos, con null o demasiado largos")
    void ordenFijoRechazaInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Mazo(new Carta[0]));
        assertThrows(IllegalArgumentException.class,
                () -> new Mazo(new Carta[] {new Carta(Rango.AS, Palo.PICAS), null}));
        assertThrows(IllegalArgumentException.class, () -> new Mazo(new Carta[53]));
        assertThrows(NullPointerException.class, () -> new Mazo(null));
    }
}
