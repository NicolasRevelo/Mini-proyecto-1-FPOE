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
    public Partida () {
        this(SALDO_INICIAL, null);
    }
    /**Constructor para las pruebas*/
    public Partida (Mazo mazoDePrueba) {
        this(SALDO_INICIAL, mazoDePrueba);
    }
    /** El mismo contructor para pruebas pero con un saldo inicial diferente*/
    Partida(int saldoInicial, Mazo mazoDePrueba) {
        this.saldo = saldoInicial;
        this.mazoDePrueba= mazoDePrueba;
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

        if (!puedeIniciarRonda()) return;

        String error = validarApuesta(String.valueOf(apuestaNueva));
        if (error != null) throw new IllegalArgumentException(error);

        manoJugador.vaciar();
        manoComputador.vaciar();
        resultado = ResultadoRonda.SIN_RESULTADO;
        mensajeResultado = "";
        pagoRonda = 0;

        apuesta = apuestaNueva;
        saldo -= apuesta;                         // se descuenta UNA sola vez

        /** De nuevo elimino el guion falso y agrego un orden fijo*/

        if(mazoDePrueba != null) {
            mazo = mazoDePrueba;
            mazoDePrueba = null;
        } else {
            mazo = new Mazo();
            mazo.mezclar();
        }

        /** El jugador recibe dos cartas*/

        manoJugador.agregar(mazo.repartir());
        manoJugador.agregar(mazo.repartir());

        if (manoJugador.puntaje() == 21) {        // 21 o Blackjack: pasa solo al computador
            estado = EstadoRonda.TURNO_COMPUTADOR;
        } else {
            estado = EstadoRonda.TURNO_JUGADOR;
        }
    }

    /** El jugador pide una carta. Fuera de su turno no hace nada. */
    public void pedirCarta() {
        if (estado != EstadoRonda.TURNO_JUGADOR) return;

        manoJugador.agregar(mazo.repartir());

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

        /**Elimino esta parte para simplificar, a la vez no es necesario
         realizar un escenario en el juego real*/

        if (manoComputador.estaVacia()){
            manoComputador.agregar(mazo.repartir());
            manoComputador.agregar(mazo.repartir());
            if (manoJugador.esBlackjack() || manoComputador.esBlackjack()) {
                resolver();
                return;
            }
        } else if (manoComputador.puntaje() < PUNTAJE_PLANTARSE_COMPUTADOR){
            manoComputador.agregar(mazo.repartir());
        }

        if (manoComputador.sePaso() || manoComputador.puntaje() >= PUNTAJE_PLANTARSE_COMPUTADOR) {
            resolver();
        }
    }
    /** En este punto quiero que si el jugador tenga blackjack solo importe
     si la maquina tambien lo tiene, si empatan el computador no pierde las cartas
     y si la maquina saca black jack gana, ademas el ulitmo if se asegura de que las decisiones de la maquina dependen de su mano
     */


    // =====================================================
    //  Resolución de la ronda, con los guiones ya borrados.
    // =====================================================
    /**Primero quiero comparar una mano valida*/
    private static int fuerza (Mano mano){
        if (mano.esBlackjack()) return 4;
        if (mano.puntaje() == 21) return 3;
        if (mano.esVeinteYMedio()) return 2;
        return 1;
    }

    private void resolver() {
        Mano j = manoJugador;
        Mano c = manoComputador;

        /**Para que si el jugador se pase pierda*/
        if (j.sePaso()){
            cerrar(ResultadoRonda.DERROTA,0,"Te pasaste de 21 con" + j.puntaje() + "Pierdes la ronda.");
            return;
        }
        if (c.sePaso()){
            cerrar(ResultadoRonda.VICTORIA,2 * apuesta, "El computador se paso de 21 con " + c.puntaje() + ". Ganaste!");
            return;
        }

        int fj = fuerza(j);
        int fc = fuerza(c);

        /**Para el black jack*/
        if (fj == 4 && fc == 4){
            cerrar(ResultadoRonda.EMPATE, apuesta, "Ambos sacan Black Jack, quedan empatados.");
        } else if (fj == 4){
            cerrar(ResultadoRonda.VICTORIA, apuesta * 5/2, "Blackjack! a favor del jugador, ganas con bonus.");
        } else if (fc == 4){
            cerrar(ResultadoRonda.DERROTA, 0, "La maquina saca Black Jack"+ c.puntaje() + "en sus dos primeras rondas, pierdes la ronda.");
            /**Para una jugada del mismo nivel, aqui hago uso de la ia, reportado en REGISTRO_IA.md*/
        }else if (fj == fc){
            if (fj == 1 && j.puntaje() > c.puntaje()) {
                cerrar(ResultadoRonda.VICTORIA, 2 * apuesta, "Ganas con " + j.puntaje() + " contra " + c.puntaje() + ".");
            } else if (fj == 1 && j.puntaje() < c.puntaje()) {
                cerrar(ResultadoRonda.DERROTA, 0, "Pierdes: " + j.puntaje() + " contra " + c.puntaje() + ".");
            } else {
                cerrar(ResultadoRonda.EMPATE, apuesta, "Empate con " + j.puntaje() + " puntos. Se devuelve tu apuesta.");
            }
        /**Para las jugadas de distinto nivel solo gana el que tenga mas fuerza*/
            } else if (fj > fc){
                cerrar(ResultadoRonda.VICTORIA, 2 * apuesta, explicarNivel(true, j, c));
            }else{
                cerrar(ResultadoRonda.DERROTA, 0, explicarNivel(false, j, c));
            }
    }
    /**Cuando el resultado se decida por la prioridad entre jugadas*/
    private String explicarNivel(boolean ganaJugador, Mano j, Mano c) {
        if (ganaJugador) {
            if (fuerza(j) == 3) {
                return "Ganas con 21 en contra de" + c.puntaje() + ".";
            }
                return "Veinte y medio le gana " + c.puntaje() + " de la maquina, Victoria!";
        }
        if (fuerza(c) == 3) {
            if (fuerza(j) == 2) {
                return "Tu 20 y medio pierde frente al 21 de la maquina";
            }
                return "Pierdes contra el computador que hizo 21 y tu" + j.puntaje() + ".";
            }
            return "Pierdes contra el computador que tiene 20 y medio.";

        }
    /**Este comando cierra la ronda y aplica el pago*/
    private void cerrar(ResultadoRonda res, int pago, String mensaje){
        if(estado == EstadoRonda.RONDA_TERMINADA) return;
        resultado = res;
        pagoRonda = pago;
        mensajeResultado = mensaje;
        saldo += pago;
        estado = EstadoRonda.RONDA_TERMINADA;
    }
}
