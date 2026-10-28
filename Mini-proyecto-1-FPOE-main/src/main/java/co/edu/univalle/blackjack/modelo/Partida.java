package co.edu.univalle.blackjack.modelo;

/** Clase principal del modelo de nuestro juego, con esta clase planteo manejar el saldo,
 * las apuestas, el mazo, las manos y los turnos, junto con el resultado y los pagos */


public class Partida {

    public static final int SALDO_INICIAL = 1000;
    public static final int APUESTA_MINIMA = 10;
    public static final int MULTIPLO_APUESTA = 10;
    /** Con este valor privado hago que el computador pida el request mientras tenga
     menos de 17, para plantarse en este o seguir a mas*/
    private static final int PUNTAJE_PLANTARSE_COMPUTADOR = 17;

    // ---------- Estado de la partida ----------
    private int saldo;
    private int apuesta = 0;
    private EstadoRonda estado = EstadoRonda.ESPERANDO_APUESTA;
    private final Mano manoJugador = new Mano();
    private final Mano manoComputador = new Mano();
    private ResultadoRonda resultado = ResultadoRonda.SIN_RESULTADO;
    private String mensajeResultado = "";
    private int pagoRonda = 0;

    /** Elimino el guion falso dado por mi compañero para poder trabajar por separado
     y comienzo con la declaración de los atributos y constructores
     para que la clase pueda funcionar en el juego real */

    private Mazo mazo;
    private Mazo mazoDePrueba;

    /** Una partida normal con un saldo de 1000 y un mazo nuevo */
    public Partida (){
        this(SALDO_INICIAL, null);
    }



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
