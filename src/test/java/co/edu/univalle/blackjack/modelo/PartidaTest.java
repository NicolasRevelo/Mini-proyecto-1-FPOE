package co.edu.univalle.blackjack.modelo;

import static co.edu.univalle.blackjack.modelo.Rango.AS;
import static co.edu.univalle.blackjack.modelo.Rango.CINCO;
import static co.edu.univalle.blackjack.modelo.Rango.CUATRO;
import static co.edu.univalle.blackjack.modelo.Rango.DIEZ;
import static co.edu.univalle.blackjack.modelo.Rango.DOS;
import static co.edu.univalle.blackjack.modelo.Rango.J;
import static co.edu.univalle.blackjack.modelo.Rango.K;
import static co.edu.univalle.blackjack.modelo.Rango.NUEVE;
import static co.edu.univalle.blackjack.modelo.Rango.OCHO;
import static co.edu.univalle.blackjack.modelo.Rango.Q;
import static co.edu.univalle.blackjack.modelo.Rango.SEIS;
import static co.edu.univalle.blackjack.modelo.Rango.SIETE;
import static co.edu.univalle.blackjack.modelo.Rango.TRES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la partida: apuestas, turnos, reglas del computador, resultados y pagos.
 *
 * Con un mazo de orden fijo las cartas se reparten así: dos del jugador, las que el jugador pida,
 * dos del computador y las que el computador pida. Por eso cada caso escribe sus cartas en ese orden.
 * Saldo inicial 1000 y apuesta 100, salvo que el caso diga otra cosa.
 */
class PartidaTest {

    // =====================================================================
    //  Estado inicial (HU01)
    // =====================================================================

    @Test
    @DisplayName("Al empezar: saldo 1000, sin apuesta, manos vacías y solo se puede iniciar ronda")
    void estadoInicial() {
        Partida p = new Partida();
        assertEquals(1000, p.getSaldo());
        assertEquals(0, p.getApuesta());
        assertEquals(EstadoRonda.ESPERANDO_APUESTA, p.getEstado());
        assertTrue(p.getManoJugador().estaVacia());
        assertTrue(p.getManoComputador().estaVacia());
        assertEquals(ResultadoRonda.SIN_RESULTADO, p.getResultado());
        assertTrue(p.puedeIniciarRonda());
        assertFalse(p.puedePedirCarta());
        assertFalse(p.puedePlantarse());
    }

    // =====================================================================
    //  Validación de apuestas (HU02)
    // =====================================================================

    @Test
    @DisplayName("Apuestas incorrectas se rechazan con un mensaje")
    void apuestasInvalidas() {
        Partida p = new Partida();
        String[] invalidas = {null, "", "   ", "abc", "12abc", "15", "0", "-10", "10,5", "10.5", "5", "1010", "1e3"};
        for (String texto : invalidas) {
            String mensaje = p.validarApuesta(texto);
            assertNotNull(mensaje, "Debería rechazar: '" + texto + "'");
            assertFalse(mensaje.isBlank(), "El mensaje no puede estar vacío para: '" + texto + "'");
        }
    }

    @Test
    @DisplayName("Apuestas correctas se aceptan (múltiplos de 10, mínimo 10, hasta el saldo)")
    void apuestasValidas() {
        Partida p = new Partida();
        assertNull(p.validarApuesta("10"));
        assertNull(p.validarApuesta("100"));
        assertNull(p.validarApuesta(" 50 "));
        assertNull(p.validarApuesta("1000"));      // límite: todo el saldo
    }

    @Test
    @DisplayName("Validar una apuesta no reparte cartas ni descuenta saldo")
    void validarNoCambiaNada() {
        Partida p = new Partida();
        p.validarApuesta("15");
        p.validarApuesta("100");
        assertEquals(1000, p.getSaldo());
        assertEquals(0, p.getApuesta());
        assertEquals(EstadoRonda.ESPERANDO_APUESTA, p.getEstado());
        assertTrue(p.getManoJugador().estaVacia());
    }

    @Test
    @DisplayName("El mensaje de apuesta mayor al saldo menciona el saldo disponible")
    void mensajeSaldoInsuficiente() {
        String mensaje = new Partida().validarApuesta("1010");
        assertTrue(mensaje.contains("1000"), mensaje);
    }

    @Test
    @DisplayName("Con saldo menor de 10 no se puede iniciar ronda ni apostar")
    void saldoInsuficiente() {
        Partida p = new Partida(5, null);
        assertFalse(p.haySaldoSuficiente());
        assertFalse(p.puedeIniciarRonda());
        assertNotNull(p.validarApuesta("10"));
    }

    @Test
    @DisplayName("No se puede validar una apuesta nueva mientras la ronda está en juego")
    void apuestaDuranteLaRonda() {
        Partida p = Ayuda.partida(K, OCHO, K, SEIS);
        p.iniciarRonda(100);
        assertNotNull(p.validarApuesta("100"));
    }

    // =====================================================================
    //  Iniciar la ronda (HU03)
    // =====================================================================

    @Test
    @DisplayName("Iniciar descuenta la apuesta una sola vez y reparte dos cartas solo al jugador")
    void iniciarRonda() {
        Partida p = Ayuda.partida(K, OCHO, K, SEIS);
        p.iniciarRonda(100);
        assertEquals(900, p.getSaldo());
        assertEquals(100, p.getApuesta());
        assertEquals(EstadoRonda.TURNO_JUGADOR, p.getEstado());
        assertEquals(2, p.getManoJugador().getNumeroCartas());
        assertTrue(p.getManoComputador().estaVacia(), "El computador espera sin cartas hasta su turno");
        assertEquals(18, p.getManoJugador().puntaje());
        assertTrue(p.puedePedirCarta());
        assertTrue(p.puedePlantarse());
        assertFalse(p.puedeIniciarRonda());
    }

    @Test
    @DisplayName("Apostar todo el saldo deja el saldo en 0 durante la ronda")
    void apostarTodo() {
        Partida p = Ayuda.partida(K, OCHO, K, SEIS);
        p.iniciarRonda(1000);
        assertEquals(0, p.getSaldo());
        assertEquals(1000, p.getApuesta());
    }

    @Test
    @DisplayName("Iniciar con una apuesta inválida lanza excepción y no cambia el saldo")
    void iniciarConApuestaInvalida() {
        Partida p = new Partida();
        assertThrows(IllegalArgumentException.class, () -> p.iniciarRonda(15));
        assertThrows(IllegalArgumentException.class, () -> p.iniciarRonda(2000));
        assertEquals(1000, p.getSaldo());
        assertEquals(EstadoRonda.ESPERANDO_APUESTA, p.getEstado());
    }

    @Test
    @DisplayName("Activar Iniciar ronda otra vez durante la ronda no descuenta ni reparte de nuevo")
    void iniciarDosVeces() {
        Partida p = Ayuda.partida(K, OCHO, K, SEIS);
        p.iniciarRonda(100);
        p.iniciarRonda(100);
        assertEquals(900, p.getSaldo());
        assertEquals(2, p.getManoJugador().getNumeroCartas());
    }

    @Test
    @DisplayName("Con Blackjack al repartir, el turno pasa solo al computador")
    void blackjackInicialPasaAlComputador() {
        Partida p = Ayuda.partida(AS, K, K, Q);
        p.iniciarRonda(100);
        assertEquals(EstadoRonda.TURNO_COMPUTADOR, p.getEstado());
        assertFalse(p.puedePedirCarta());
        assertFalse(p.puedePlantarse());
    }

    // =====================================================================
    //  Pedir carta (HU04)
    // =====================================================================

    @Test
    @DisplayName("Pedir carta agrega exactamente una carta y actualiza el puntaje")
    void pedirCarta() {
        Partida p = Ayuda.partida(CINCO, TRES, DOS);
        p.iniciarRonda(100);
        p.pedirCarta();
        assertEquals(3, p.getManoJugador().getNumeroCartas());
        assertEquals(10, p.getManoJugador().puntaje());
        assertEquals(EstadoRonda.TURNO_JUGADOR, p.getEstado());
    }

    @Test
    @DisplayName("Si el jugador se pasa, pierde de inmediato y el computador no recibe cartas")
    void jugadorSePasa() {
        Partida p = Ayuda.partida(K, NUEVE, CINCO, SEIS, SEIS);
        p.iniciarRonda(100);
        p.pedirCarta();                                  // K + 9 + 5 = 24
        assertEquals(24, p.getManoJugador().puntaje());
        assertEquals(EstadoRonda.RONDA_TERMINADA, p.getEstado());
        assertEquals(ResultadoRonda.DERROTA, p.getResultado());
        assertEquals(900, p.getSaldo());
        assertTrue(p.getManoComputador().estaVacia(), "El computador no debe recibir cartas");
        assertFalse(p.puedePedirCarta());
        assertFalse(p.puedePlantarse());
    }

    @Test
    @DisplayName("Si el jugador llega a 21 pidiendo, el turno pasa solo al computador")
    void jugadorLlegaA21() {
        Partida p = Ayuda.partida(SIETE, SIETE, SIETE, K, OCHO);
        p.iniciarRonda(100);
        p.pedirCarta();
        assertEquals(21, p.getManoJugador().puntaje());
        assertEquals(EstadoRonda.TURNO_COMPUTADOR, p.getEstado());
    }

    @Test
    @DisplayName("Pedir carta fuera del turno del jugador no hace nada")
    void pedirFueraDeTurno() {
        // antes de apostar
        Partida p = Ayuda.partida(K, OCHO, K, SEIS, DOS);
        p.pedirCarta();
        assertTrue(p.getManoJugador().estaVacia());
        assertEquals(1000, p.getSaldo());

        // durante el turno del computador
        p.iniciarRonda(100);
        p.plantarse();
        p.pedirCarta();
        assertEquals(2, p.getManoJugador().getNumeroCartas());

        // con la ronda terminada
        Ayuda.jugarComputador(p);
        int saldo = p.getSaldo();
        p.pedirCarta();
        assertEquals(2, p.getManoJugador().getNumeroCartas());
        assertEquals(saldo, p.getSaldo());
    }

    // =====================================================================
    //  Plantarse (HU05)
    // =====================================================================

    @Test
    @DisplayName("Plantarse conserva las cartas, no descuenta otra apuesta y pasa el turno al computador")
    void plantarse() {
        Partida p = Ayuda.partida(K, OCHO, K, SEIS);
        p.iniciarRonda(100);
        p.plantarse();
        assertEquals(EstadoRonda.TURNO_COMPUTADOR, p.getEstado());
        assertEquals(2, p.getManoJugador().getNumeroCartas());
        assertEquals(18, p.getManoJugador().puntaje());
        assertEquals(900, p.getSaldo());
        assertFalse(p.puedePedirCarta());
        assertFalse(p.puedePlantarse());
        assertTrue(p.getManoComputador().estaVacia(), "El computador recibe sus cartas en su primer paso");
    }

    @Test
    @DisplayName("Plantarse otra vez, o fuera de turno, se ignora")
    void plantarseRepetido() {
        Partida p = Ayuda.partida(K, OCHO, K, SEIS);
        p.plantarse();                                   // antes de apostar
        assertEquals(EstadoRonda.ESPERANDO_APUESTA, p.getEstado());
        p.iniciarRonda(100);
        p.plantarse();
        p.plantarse();
        assertEquals(EstadoRonda.TURNO_COMPUTADOR, p.getEstado());
        assertEquals(900, p.getSaldo());
    }

    // =====================================================================
    //  Turno del computador (HU06)
    // =====================================================================

    @Test
    @DisplayName("El primer paso del computador le reparte sus dos cartas y cada paso siguiente una más")
    void computadorPasoAPaso() {
        // jugador K+8; computador 5+3 = 8, luego pide 2 (10) y luego 9 (19)
        Partida p = Ayuda.partida(K, OCHO, CINCO, TRES, DOS, NUEVE);
        p.iniciarRonda(100);
        p.plantarse();

        p.jugarPasoComputador();
        assertEquals(2, p.getManoComputador().getNumeroCartas());
        assertEquals(EstadoRonda.TURNO_COMPUTADOR, p.getEstado());

        p.jugarPasoComputador();
        assertEquals(3, p.getManoComputador().getNumeroCartas());
        assertEquals(10, p.getManoComputador().puntaje());
        assertEquals(EstadoRonda.TURNO_COMPUTADOR, p.getEstado());

        p.jugarPasoComputador();
        assertEquals(4, p.getManoComputador().getNumeroCartas());
        assertEquals(19, p.getManoComputador().puntaje());
        assertEquals(EstadoRonda.RONDA_TERMINADA, p.getEstado());
    }

    @Test
    @DisplayName("Fuera de su turno, jugarPasoComputador no hace nada")
    void computadorFueraDeTurno() {
        Partida p = Ayuda.partida(K, OCHO, K, SEIS);
        p.jugarPasoComputador();                         // antes de apostar
        p.iniciarRonda(100);
        p.jugarPasoComputador();                         // turno del jugador
        assertTrue(p.getManoComputador().estaVacia());
        assertEquals(EstadoRonda.TURNO_JUGADOR, p.getEstado());
    }

    @Test
    @DisplayName("El computador pide con 16")
    void computadorPideCon16() {
        Partida p = Ayuda.partida(K, NUEVE, DIEZ, SEIS, DOS);   // 10 + 6 = 16, pide un 2 -> 18
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(3, p.getManoComputador().getNumeroCartas());
        assertEquals(18, p.getManoComputador().puntaje());
    }

    @Test
    @DisplayName("El computador se planta con 17 (10 + 7)")
    void computadorSePlantaCon17() {
        Partida p = Ayuda.partida(K, NUEVE, DIEZ, SIETE, DOS);
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(2, p.getManoComputador().getNumeroCartas());
        assertEquals(17, p.getManoComputador().puntaje());
    }

    @Test
    @DisplayName("El computador se planta con As + 6 (17)")
    void computadorSePlantaConAsYSeis() {
        Partida p = Ayuda.partida(K, NUEVE, AS, SEIS, DOS);
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(2, p.getManoComputador().getNumeroCartas());
        assertEquals(17, p.getManoComputador().puntaje());
    }

    @Test
    @DisplayName("El computador se planta con 17 aunque vaya perdiendo: decide solo por su mano")
    void computadorNoMiraAlJugador() {
        Partida p = Ayuda.partida(K, Q, DIEZ, SIETE, DOS);      // jugador 20, computador 17
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(2, p.getManoComputador().getNumeroCartas());
        assertEquals(ResultadoRonda.VICTORIA, p.getResultado());
    }

    @Test
    @DisplayName("El computador con dos cartas que suman 14 pide carta (no se queda con Veinte y medio)")
    void computadorConCatorcePide() {
        Partida p = Ayuda.partida(K, NUEVE, J, CUATRO, TRES);   // 14 + 3 = 17
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(3, p.getManoComputador().getNumeroCartas());
        assertEquals(17, p.getManoComputador().puntaje());
    }

    // =====================================================================
    //  Resultados y pagos (HU08, HU09). Apuesta 100, saldo inicial 1000.
    // =====================================================================

    @Test
    @DisplayName("Derrota: el jugador 18 contra el computador 20 deja el saldo en 900")
    void derrota() {
        Partida p = Ayuda.partida(K, OCHO, K, Q);
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(ResultadoRonda.DERROTA, p.getResultado());
        assertEquals(900, p.getSaldo());
        assertEquals(0, p.getPagoRonda());
        assertEquals(-100, p.getGananciaNeta());
    }

    @Test
    @DisplayName("Empate: ambos con 18 devuelve la apuesta (saldo 1000)")
    void empate() {
        Partida p = Ayuda.partida(K, OCHO, Q, OCHO);
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(ResultadoRonda.EMPATE, p.getResultado());
        assertEquals(1000, p.getSaldo());
        assertEquals(100, p.getPagoRonda());
        assertEquals(0, p.getGananciaNeta());
    }

    @Test
    @DisplayName("Victoria ordinaria: 18 contra 17 paga el doble (saldo 1100)")
    void victoria() {
        Partida p = Ayuda.partida(K, OCHO, K, SIETE);
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(ResultadoRonda.VICTORIA, p.getResultado());
        assertEquals(1100, p.getSaldo());
        assertEquals(200, p.getPagoRonda());
        assertEquals(100, p.getGananciaNeta());
    }

    @Test
    @DisplayName("Victoria con Blackjack paga 2,5 veces la apuesta (saldo 1150)")
    void victoriaConBlackjack() {
        Partida p = Ayuda.partida(AS, K, K, Q);
        p.iniciarRonda(100);
        Ayuda.jugarComputador(p);
        assertEquals(ResultadoRonda.VICTORIA, p.getResultado());
        assertEquals(1150, p.getSaldo());
        assertEquals(250, p.getPagoRonda());
        assertEquals(150, p.getGananciaNeta());
    }

    @Test
    @DisplayName("Blackjack del jugador: el computador no pide cartas, solo se comprueba su Blackjack")
    void blackjackJugadorComputadorSoloRecibeDos() {
        Partida p = Ayuda.partida(AS, K, DOS, TRES, DIEZ);       // computador 5: podría pedir, pero no debe
        p.iniciarRonda(100);
        Ayuda.jugarComputador(p);
        assertEquals(2, p.getManoComputador().getNumeroCartas());
        assertEquals(ResultadoRonda.VICTORIA, p.getResultado());
        assertEquals(1150, p.getSaldo());
    }

    @Test
    @DisplayName("Blackjack de ambos es empate y se devuelve la apuesta")
    void blackjackDoble() {
        Partida p = Ayuda.partida(AS, K, AS, Q);
        p.iniciarRonda(100);
        Ayuda.jugarComputador(p);
        assertEquals(ResultadoRonda.EMPATE, p.getResultado());
        assertEquals(1000, p.getSaldo());
    }

    @Test
    @DisplayName("Un 21 de tres cartas pierde contra el Blackjack inicial del computador")
    void veintiunoPierdeContraBlackjack() {
        Partida p = Ayuda.partida(SIETE, SIETE, SIETE, AS, K);   // jugador 7+7+7 = 21; computador A+K
        p.iniciarRonda(100);
        p.pedirCarta();
        Ayuda.jugarComputador(p);
        assertEquals(ResultadoRonda.DERROTA, p.getResultado());
        assertEquals(900, p.getSaldo());
    }

    @Test
    @DisplayName("Un 21 que no es Blackjack gana con pago ordinario (saldo 1100, no 1150)")
    void veintiunoPagoOrdinario() {
        Partida p = Ayuda.partida(SIETE, SIETE, SIETE, K, OCHO);
        p.iniciarRonda(100);
        p.pedirCarta();
        Ayuda.jugarComputador(p);
        assertEquals(ResultadoRonda.VICTORIA, p.getResultado());
        assertEquals(1100, p.getSaldo());
    }

    @Test
    @DisplayName("Si el computador se pasa, gana el jugador (computador 22)")
    void computadorSePasa() {
        Partida p = Ayuda.partida(K, Q, K, SEIS, SEIS);          // computador 10 + 6 = 16, pide 6 -> 22
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(22, p.getManoComputador().puntaje());
        assertEquals(ResultadoRonda.VICTORIA, p.getResultado());
        assertEquals(1100, p.getSaldo());
    }

    @Test
    @DisplayName("Veinte y medio gana a una mano de 19 (pago ordinario, sin pago extra)")
    void veinteYMedioGanaA19() {
        Partida p = Ayuda.partida(J, CUATRO, K, NUEVE);
        p.iniciarRonda(100);
        assertEquals(Jugada.VEINTE_Y_MEDIO, p.getManoJugador().getJugada());
        p.plantarse();
        Ayuda.jugarComputador(p);
        assertEquals(19, p.getManoComputador().puntaje());
        assertEquals(ResultadoRonda.VICTORIA, p.getResultado());
        assertEquals(1100, p.getSaldo());
    }

    @Test
    @DisplayName("Veinte y medio pierde contra un 21 del computador")
    void veinteYMedioPierdeContra21() {
        Partida p = Ayuda.partida(J, CUATRO, SIETE, SIETE, SIETE);   // computador 7 + 7 = 14, pide 7 -> 21
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(21, p.getManoComputador().puntaje());
        assertEquals(ResultadoRonda.DERROTA, p.getResultado());
        assertEquals(900, p.getSaldo());
    }

    @Test
    @DisplayName("Veinte y medio pierde contra el Blackjack del computador")
    void veinteYMedioPierdeContraBlackjack() {
        Partida p = Ayuda.partida(J, CUATRO, AS, K);
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(ResultadoRonda.DERROTA, p.getResultado());
        assertEquals(900, p.getSaldo());
    }

    @Test
    @DisplayName("Si el jugador pide carta tras un 14 inicial, ya no cuenta como Veinte y medio")
    void catorceYPedirYaNoEsVeinteYMedio() {
        // jugador J+4, pide 2 -> 16; computador 10+8 = 18: gana el computador por puntaje normal
        Partida p = Ayuda.partida(J, CUATRO, DOS, DIEZ, OCHO);
        p.iniciarRonda(100);
        assertEquals(Jugada.VEINTE_Y_MEDIO, p.getManoJugador().getJugada());
        p.pedirCarta();
        assertEquals(Jugada.NINGUNA, p.getManoJugador().getJugada());
        p.plantarse();
        Ayuda.jugarComputador(p);
        assertEquals(ResultadoRonda.DERROTA, p.getResultado());
    }

    @Test
    @DisplayName("Jugador con 20 contra computador con 22 gana el jugador")
    void veinteContra22() {
        Partida p = Ayuda.partida(K, Q, DIEZ, SEIS, SEIS);
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(ResultadoRonda.VICTORIA, p.getResultado());
    }

    @Test
    @DisplayName("Al terminar, hay mensaje de resultado; antes de terminar está vacío")
    void mensajeDeResultado() {
        Partida p = Ayuda.partida(K, OCHO, K, SEIS, DOS);
        assertEquals("", p.getMensajeResultado());
        p.iniciarRonda(100);
        assertEquals("", p.getMensajeResultado());
        p.plantarse();
        Ayuda.jugarComputador(p);
        assertFalse(p.getMensajeResultado().isBlank());
    }

    // =====================================================================
    //  Ronda cerrada: el saldo se aplica una sola vez (HU08, HU09)
    // =====================================================================

    @Test
    @DisplayName("Con la ronda terminada, repetir acciones no cambia saldo, manos ni resultado")
    void rondaCerrada() {
        Partida p = Ayuda.partida(K, OCHO, K, SEIS, DOS);
        Ayuda.rondaPlantandose(p, 100);
        int saldo = p.getSaldo();
        ResultadoRonda resultado = p.getResultado();
        int cartasJugador = p.getManoJugador().getNumeroCartas();
        int cartasComputador = p.getManoComputador().getNumeroCartas();

        for (int i = 0; i < 5; i++) {
            p.pedirCarta();
            p.plantarse();
            p.jugarPasoComputador();
        }

        assertEquals(EstadoRonda.RONDA_TERMINADA, p.getEstado());
        assertEquals(saldo, p.getSaldo());
        assertEquals(resultado, p.getResultado());
        assertEquals(cartasJugador, p.getManoJugador().getNumeroCartas());
        assertEquals(cartasComputador, p.getManoComputador().getNumeroCartas());
    }

    @Test
    @DisplayName("La ganancia neta es 0 mientras la ronda no termina")
    void gananciaNetaAntesDeTerminar() {
        Partida p = Ayuda.partida(K, OCHO, K, SEIS);
        p.iniciarRonda(100);
        assertEquals(0, p.getGananciaNeta());
    }

    // =====================================================================
    //  Otra ronda (HU10)
    // =====================================================================

    @Test
    @DisplayName("Otra ronda conserva el saldo, vacía las manos y renueva el resultado")
    void otraRonda() {
        Partida p = Ayuda.partida(K, OCHO, K, Q);               // primera ronda: se pierde (18 vs 20)
        Ayuda.rondaPlantandose(p, 100);
        assertEquals(900, p.getSaldo());
        assertTrue(p.puedeIniciarRonda());

        p.iniciarRonda(100);                                    // segunda ronda con mazo nuevo barajado
        assertEquals(800, p.getSaldo(), "El saldo continúa, no vuelve a 1000");
        assertTrue(p.getEstado() == EstadoRonda.TURNO_JUGADOR || p.getEstado() == EstadoRonda.TURNO_COMPUTADOR,
                "Con el mazo barajado el jugador puede abrir con Blackjack y pasar directo al computador");
        assertEquals(2, p.getManoJugador().getNumeroCartas());
        assertTrue(p.getManoComputador().estaVacia());
        assertEquals(ResultadoRonda.SIN_RESULTADO, p.getResultado());
        assertEquals("", p.getMensajeResultado());
    }

    @Test
    @DisplayName("Sin fichas suficientes no se puede seguir apostando")
    void sinFichas() {
        Partida p = new Partida(10, Ayuda.mazo(K, OCHO, K, Q));
        Ayuda.rondaPlantandose(p, 10);
        assertEquals(0, p.getSaldo());
        assertEquals(EstadoRonda.RONDA_TERMINADA, p.getEstado());
        assertFalse(p.haySaldoSuficiente());
        assertFalse(p.puedeIniciarRonda());
        assertNotNull(p.validarApuesta("10"));
    }

    // =====================================================================
    //  Integridad con el mazo real barajado
    // =====================================================================

    @Test
    @DisplayName("En 2000 rondas aleatorias no hay cartas repetidas, errores ni saldos negativos")
    void rondasAleatorias() {
        Random azar = new Random(42);
        Partida p = new Partida();
        for (int ronda = 0; ronda < 2000; ronda++) {
            if (!p.puedeIniciarRonda()) {
                p = new Partida();
            }
            int maximo = Math.max(1, Math.min(10, p.getSaldo() / 10));
            int apuesta = 10 * (1 + azar.nextInt(maximo));
            int saldoAntes = p.getSaldo();

            p.iniciarRonda(apuesta);
            assertEquals(saldoAntes - apuesta, p.getSaldo(), "La apuesta se descuenta una sola vez");

            while (p.getEstado() == EstadoRonda.TURNO_JUGADOR) {
                if (p.getManoJugador().puntaje() < 12 + azar.nextInt(6)) {
                    p.pedirCarta();
                } else {
                    p.plantarse();
                }
            }
            Ayuda.jugarComputador(p);

            assertEquals(EstadoRonda.RONDA_TERMINADA, p.getEstado());
            assertTrue(p.getSaldo() >= 0, "El saldo no puede ser negativo");
            assertEquals(saldoAntes - apuesta + p.getPagoRonda(), p.getSaldo(), "El pago se aplica una sola vez");

            Set<Carta> vistas = new HashSet<>();
            List<Carta> todas = new ArrayList<>(p.getManoJugador().getCartas());
            todas.addAll(p.getManoComputador().getCartas());
            for (Carta carta : todas) {
                assertTrue(vistas.add(carta), "Carta repetida en la ronda " + ronda + ": " + carta);
            }
        }
    }
}
