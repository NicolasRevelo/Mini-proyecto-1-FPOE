package co.edu.univalle.blackjack.modelo;

/**
 * VERSION FALSA / PROVISIONAL de Partida.
 *
 * Sirve para que la Persona B construya y pruebe la interfaz mientras la Persona A
 * escribe el modelo real. Tiene los MISMOS metodos publicos que tendra la version real
 * (ver CONTRATO.md), pero en lugar de un mazo mezclado usa un GUION fijo de cartas,
 * que rota entre 3 escenarios en cada ronda:
 *
 *   Ronda 1 -> normal:          K+5 (15), pide 4 -> 19. Computador 10+6, pide 3 -> 19. Empate.
 *                               Si el jugador pide otra carta (9) se pasa con 28.
 *   Ronda 2 -> Blackjack:       A+K. Gana con Blackjack (computador 9+7).
 *   Ronda 3 -> Veinte y medio:  J+4 (14). Si se planta contra 10+8 gana por veinte y medio.
 *   Ronda 4 -> vuelve al escenario 1, y asi sucesivamente.
 *
 * La Persona A debe reemplazar el interior de esta clase (Mazo de 52 en arreglo, mezcla,
 * reglas completas) y conservar las firmas de los metodos publicos.
 */
public class Partida {

    public static final int SALDO_INICIAL = 1000;
    public static final int APUESTA_MINIMA = 10;
    public static final int MULTIPLO_APUESTA = 10;

    // ---------- Estado de la partida ----------
    private int saldo = SALDO_INICIAL;
    private int apuesta = 0;
    private EstadoRonda estado = EstadoRonda.ESPERANDO_APUESTA;
    private final Mano manoJugador = new Mano();
    private final Mano manoComputador = new Mano();
    private ResultadoRonda resultado = ResultadoRonda.SIN_RESULTADO;
    private String mensajeResultado = "";
    private int pagoRonda = 0;

    // ---------- Guion falso (SOLO en esta version provisional) ----------
    private static Carta c(Rango r, Palo p) { return new Carta(r, p); }

    private static final Carta[][] JUGADOR_INICIAL = {
        { c(Rango.K, Palo.CORAZONES), c(Rango.CINCO, Palo.PICAS) },
        { c(Rango.AS, Palo.PICAS),    c(Rango.K, Palo.DIAMANTES) },
        { c(Rango.J, Palo.PICAS),     c(Rango.CUATRO, Palo.TREBOLES) }
    };
    private static final Carta[][] JUGADOR_EXTRA = {
        { c(Rango.CUATRO, Palo.DIAMANTES), c(Rango.NUEVE, Palo.TREBOLES) },
        { },
        { c(Rango.CINCO, Palo.CORAZONES),  c(Rango.NUEVE, Palo.TREBOLES) }
    };
    private static final Carta[][] COMPUTADOR_INICIAL = {
        { c(Rango.DIEZ, Palo.TREBOLES),  c(Rango.SEIS, Palo.DIAMANTES) },
        { c(Rango.NUEVE, Palo.CORAZONES), c(Rango.SIETE, Palo.TREBOLES) },
        { c(Rango.DIEZ, Palo.DIAMANTES), c(Rango.OCHO, Palo.TREBOLES) }
    };
    private static final Carta[][] COMPUTADOR_EXTRA = {
        { c(Rango.TRES, Palo.CORAZONES), c(Rango.SIETE, Palo.PICAS) },
        { },
        { }
    };

    private int siguienteEscenario = 0;   // rota 0, 1, 2, 0, ...
    private int escenario = 0;            // escenario de la ronda actual
    private int extrasJugador = 0;
    private int extrasComputador = 0;

    // =====================================================
    //  CONSULTAS (la vista solo lee con estos metodos)
    // =====================================================

    public int getSaldo() { return saldo; }

    /** Apuesta de la ronda actual (0 si todavia no se ha iniciado ninguna). */
    public int getApuesta() { return apuesta; }

    public EstadoRonda getEstado() { return estado; }

    public Mano getManoJugador() { return manoJugador; }

    /** Vacia mientras el computador no ha jugado en la ronda. */
    public Mano getManoComputador() { return manoComputador; }

    /** SIN_RESULTADO hasta que el estado sea RONDA_TERMINADA. */
    public ResultadoRonda getResultado() { return resultado; }

    /** Explica quien gano y por que. Texto vacio hasta que la ronda termine. */
    public String getMensajeResultado() { return mensajeResultado; }

    /** Fichas acreditadas al terminar la ronda (0 en derrota, apuesta en empate, etc.). */
    public int getPagoRonda() { return pagoRonda; }

    /** Pago menos apuesta: -100, 0, +100 o +150 con apuesta de 100. */
    public int getGananciaNeta() {
        return estado == EstadoRonda.RONDA_TERMINADA ? pagoRonda - apuesta : 0;
    }

    /** true si el saldo alcanza para la apuesta minima. */
    public boolean haySaldoSuficiente() { return saldo >= APUESTA_MINIMA; }

    // Ayudas para habilitar o deshabilitar botones desde un solo lugar:
    public boolean puedeIniciarRonda() {
        return (estado == EstadoRonda.ESPERANDO_APUESTA || estado == EstadoRonda.RONDA_TERMINADA)
                && haySaldoSuficiente();
    }
    public boolean puedePedirCarta() { return estado == EstadoRonda.TURNO_JUGADOR; }
    public boolean puedePlantarse()  { return estado == EstadoRonda.TURNO_JUGADOR; }

    // =====================================================
    //  ACCIONES
    // =====================================================

    /**
     * Valida el texto del campo de apuesta SIN modificar nada.
     * @return null si es valida; si no, un mensaje que explica como corregirla.
     */
    public String validarApuesta(String texto) {
        if (estado == EstadoRonda.TURNO_JUGADOR || estado == EstadoRonda.TURNO_COMPUTADOR) {
            return "No puedes cambiar la apuesta mientras la ronda está en juego.";
        }
        if (!haySaldoSuficiente()) {
            return "No tienes fichas suficientes para otra ronda.";
        }
        if (texto == null || texto.trim().isEmpty()) {
            return "Ingresa una apuesta para comenzar.";
        }
        int valor;
        try {
            valor = Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return "La apuesta debe ser un número entero, sin letras ni decimales.";
        }
        if (valor <= 0) {
            return "La apuesta debe ser mayor que cero.";
        }
        if (valor < APUESTA_MINIMA) {
            return "La apuesta mínima es de " + APUESTA_MINIMA + " fichas.";
        }
        if (valor % MULTIPLO_APUESTA != 0) {
            return "La apuesta debe ser múltiplo de " + MULTIPLO_APUESTA + ".";
        }
        if (valor > saldo) {
            return "La apuesta no puede superar tu saldo de " + saldo + " fichas.";
        }
        return null;
    }

    /**
     * Inicia una ronda: descuenta la apuesta una sola vez y reparte dos cartas al jugador.
     * Llamar solo despues de que validarApuesta devolvio null.
     * @throws IllegalArgumentException si la apuesta no es valida o no se puede iniciar ahora.
     */
    public void iniciarRonda(int apuestaNueva) {
        String error = validarApuesta(String.valueOf(apuestaNueva));
        if (error != null) throw new IllegalArgumentException(error);

        manoJugador.vaciar();
        manoComputador.vaciar();
        resultado = ResultadoRonda.SIN_RESULTADO;
        mensajeResultado = "";
        pagoRonda = 0;

        apuesta = apuestaNueva;
        saldo -= apuesta;                         // se descuenta UNA sola vez

        escenario = siguienteEscenario;           // guion falso
        siguienteEscenario = (siguienteEscenario + 1) % JUGADOR_INICIAL.length;
        extrasJugador = 0;
        extrasComputador = 0;

        for (Carta carta : JUGADOR_INICIAL[escenario]) manoJugador.agregar(carta);

        if (manoJugador.puntaje() == 21) {        // 21 o Blackjack: pasa solo al computador
            estado = EstadoRonda.TURNO_COMPUTADOR;
        } else {
            estado = EstadoRonda.TURNO_JUGADOR;
        }
    }

    /** El jugador pide una carta. Fuera de su turno no hace nada. */
    public void pedirCarta() {
        if (estado != EstadoRonda.TURNO_JUGADOR) return;
        if (extrasJugador >= JUGADOR_EXTRA[escenario].length) {
            throw new IllegalStateException("Versión falsa: se acabaron las cartas del guion.");
        }
        manoJugador.agregar(JUGADOR_EXTRA[escenario][extrasJugador++]);

        if (manoJugador.sePaso()) {
            resolver();                           // pierde de inmediato; el computador no juega
        } else if (manoJugador.puntaje() == 21) {
            estado = EstadoRonda.TURNO_COMPUTADOR;
        }
    }

    /** El jugador se planta. Fuera de su turno no hace nada. */
    public void plantarse() {
        if (estado != EstadoRonda.TURNO_JUGADOR) return;
        estado = EstadoRonda.TURNO_COMPUTADOR;
    }

    /**
     * Ejecuta UN paso del turno del computador. El controlador la llama repetidamente
     * (con una pausa entre llamadas) mientras getEstado() sea TURNO_COMPUTADOR.
     *   Paso 1: recibe sus dos cartas.  Pasos siguientes: pide una carta por paso.
     * Cuando el turno termina, el estado pasa a RONDA_TERMINADA con el resultado listo.
     * Fuera de TURNO_COMPUTADOR no hace nada.
     */
    public void jugarPasoComputador() {
        if (estado != EstadoRonda.TURNO_COMPUTADOR) return;

        if (manoComputador.estaVacia()) {
            for (Carta carta : COMPUTADOR_INICIAL[escenario]) manoComputador.agregar(carta);
            boolean hayBlackjack = manoJugador.esBlackjack() || manoComputador.esBlackjack();
            if (hayBlackjack || manoComputador.puntaje() >= 17) resolver();
            return;
        }

        manoComputador.agregar(COMPUTADOR_EXTRA[escenario][extrasComputador++]);
        if (manoComputador.sePaso() || manoComputador.puntaje() >= 17) resolver();
    }

    // =====================================================
    //  Resolucion (version simplificada; A implementa las reglas completas)
    // =====================================================

    private void resolver() {
        Mano j = manoJugador;
        Mano c = manoComputador;

        if (j.sePaso()) {
            cerrar(ResultadoRonda.DERROTA, 0,
                   "Te pasaste de 21 con " + j.puntaje() + ". Pierdes la ronda.");
        } else if (c.sePaso()) {
            cerrar(ResultadoRonda.VICTORIA, 2 * apuesta,
                   "El computador se pasó con " + c.puntaje() + ". ¡Ganas!");
        } else if (j.esBlackjack() || c.esBlackjack()) {
            if (j.esBlackjack() && c.esBlackjack()) {
                cerrar(ResultadoRonda.EMPATE, apuesta, "Ambos tienen Blackjack. Empate.");
            } else if (j.esBlackjack()) {
                cerrar(ResultadoRonda.VICTORIA, apuesta * 5 / 2, "¡Blackjack! Ganas con pago 3 a 2.");
            } else {
                cerrar(ResultadoRonda.DERROTA, 0, "El computador tiene Blackjack. Pierdes la ronda.");
            }
        } else if (j.esVeinteYMedio() && c.puntaje() < 21) {
            // PENDIENTE (A): revisar prioridades de Veinte y medio contra 21 y contra otro Veinte y medio.
            cerrar(ResultadoRonda.VICTORIA, 2 * apuesta,
                   "Veinte y medio le gana a tu rival (" + c.puntaje() + "). ¡Ganas!");
        } else if (j.puntaje() > c.puntaje()) {
            cerrar(ResultadoRonda.VICTORIA, 2 * apuesta,
                   "Ganas con " + j.puntaje() + " contra " + c.puntaje() + ".");
        } else if (j.puntaje() < c.puntaje()) {
            cerrar(ResultadoRonda.DERROTA, 0,
                   "Pierdes: " + j.puntaje() + " contra " + c.puntaje() + ".");
        } else {
            cerrar(ResultadoRonda.EMPATE, apuesta,
                   "Empate con " + j.puntaje() + " puntos. Se devuelve tu apuesta.");
        }
    }

    /** Cierra la ronda y aplica el pago al saldo UNA sola vez. */
    private void cerrar(ResultadoRonda res, int pago, String mensaje) {
        if (estado == EstadoRonda.RONDA_TERMINADA) return;   // evita pagar dos veces
        resultado = res;
        pagoRonda = pago;
        mensajeResultado = mensaje;
        saldo += pago;
        estado = EstadoRonda.RONDA_TERMINADA;
    }
}
