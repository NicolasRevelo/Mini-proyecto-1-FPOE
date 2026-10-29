package co.edu.univalle.blackjack;

import co.edu.univalle.blackjack.modelo.Carta;
import co.edu.univalle.blackjack.modelo.Jugada;
import co.edu.univalle.blackjack.modelo.Mano;
import co.edu.univalle.blackjack.modelo.Partida;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

/**
 (Persona B).
 * Regla de oro del contrato: este controlador NUNCA cambia el saldo, las manos
 * ni el estado directamente. Le pide cosas a la Partida (el modelo) y luego
 * vuelve a leer para dibujar la pantalla.
 */
public class BlackjackController {

    /** El modelo. Todo lo que pasa en el juego vive aquí. */
    private final Partida partida = new Partida();
    private static final String REGLAS = """
        OBJETIVO
        Acercarte a 21 sin pasarte y superar al computador.

        VALORES
        Del 2 al 10 valen su número; J, Q y K valen 10. El As vale 11 o 1, el que más te
        convenga sin pasarte de 21 (si todas las combinaciones se pasan, cuenta 1). Los palos no cuentan.

        APUESTAS
        Empiezas con 1000 fichas. La apuesta es un número entero, múltiplo de 10, mínimo 10
        y no mayor que tu saldo. Se descuenta al iniciar la ronda y no cambia durante ella.

        TU TURNO
        Recibes dos cartas. Puedes pedir carta o plantarte. Si te pasas de 21, pierdes de
        inmediato y el computador no juega. Con 21 o Blackjack, el turno pasa solo al computador.

        COMPUTADOR
        Recibe dos cartas al comenzar su turno. Pide carta mientras tenga menos de 17 y se
        planta con 17 a 21. Si se pasa de 21, pierde. Decide solo por su propia mano.

        JUGADAS
        Blackjack: un As y una carta de valor 10 en las dos primeras cartas.
        Veintiuno: 21 que no es Blackjack (por ejemplo, de tres cartas).
        Veinte y medio: las dos primeras cartas suman 14.
        Prioridad: Blackjack > Veintiuno > Veinte y medio > cualquier otro puntaje.
        Con jugadas del mismo nivel gana el mayor puntaje; si son iguales, hay empate.
        Veinte y medio no da victoria automática ni pago extra.

        PAGOS (ejemplo con apuesta de 100 y saldo de 1000)
        Derrota: pierdes la apuesta (900). Empate: se devuelve (1000).
        Victoria: recibes el doble (1100). Victoria con Blackjack: 2,5 veces (1150).
        """;
    // ---- Elementos de la pantalla (se conectan por el fx:id del FXML) ----
    @FXML private Label lblSaldo;
    @FXML private Label lblApuesta;
    @FXML private Label lblPuntajeJugador;
    @FXML private Label lblPuntajeComputador;
    @FXML private Label lblMensaje;
    @FXML private HBox cartasJugador;
    @FXML private HBox cartasComputador;
    @FXML private TextField campoApuesta;
    @FXML private Button btnIniciar;
    @FXML private Button btnPedir;
    @FXML private Button btnPlantarse;
    @FXML private Button btnReglas;
    @FXML private Button btnCerrar;

    /**
     * JavaFX llama a este método una sola vez, cuando termina de cargar el FXML.
     * Aquí dejamos la pantalla en su estado inicial (HU01).
     */
    @FXML
    private void initialize() {
        lblMensaje.setText("Ingresa una apuesta para comenzar.");
        actualizarVista();
    }
    /*
    Metodo que abre la ventana de las reglas     */
    @FXML
    private void onVerReglas() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.initOwner(btnReglas.getScene().getWindow());
        alerta.setTitle("Reglas");
        alerta.setHeaderText("Reglas de 21 Black Jack");
        TextArea texto = new TextArea(REGLAS);
        texto.setEditable(false);
        texto.setWrapText(true);
        texto.setPrefSize(540, 400);
        alerta.getDialogPane().setContent(texto);
        alerta.showAndWait();
    }
    /**
     * Se ejecuta al pulsar "Iniciar ronda" (onAction="#onIniciarRonda" en el FXML).
     */
    @FXML
    private void onIniciarRonda() {
        String error = partida.validarApuesta(campoApuesta.getText());
        if (error != null) {
            lblMensaje.setText(error);   // apuesta incorrecta: solo se muestra el motivo
            return;
        }
        partida.iniciarRonda(Integer.parseInt(campoApuesta.getText().trim()));
        avanzar();
    }
    /** Se ejecuta al pulsar "Pedir carta". */
    @FXML
    private void onPedirCarta() {
        partida.pedirCarta();   // le pedimos al modelo que reparta una carta más
        avanzar();
    }
    /** Repinta y decide qué sigue, según el estado que dice el modelo. */
    private void avanzar() {
        actualizarVista();
        switch (partida.getEstado()) {
            case TURNO_JUGADOR -> {
                if (partida.getManoJugador().getJugada() == Jugada.VEINTE_Y_MEDIO) {
                    lblMensaje.setText("¡Veinte y medio! Pide carta o plántate.");
                } else {
                    lblMensaje.setText("Tu turno: pide carta o plántate.");
                }
            }
            case TURNO_COMPUTADOR -> turnoComputador();
            case RONDA_TERMINADA -> {
                String mensaje = partida.getMensajeResultado();
                if (!partida.haySaldoSuficiente()) {
                    mensaje += " Te quedaste sin fichas suficientes: no es posible seguir apostando.";
                }
                lblMensaje.setText(mensaje);
            }
            default -> { }
        }
    }

    /** El computador juega un paso por segundo para que se vea lo que hace. */
    private void turnoComputador() {
        String jugada = partida.getManoJugador().getJugada() == Jugada.BLACKJACK ? "¡Blackjack! " : "";
        lblMensaje.setText(jugada + "Turno del computador...");
        PauseTransition pausa = new PauseTransition(Duration.seconds(1));
        pausa.setOnFinished(e -> {
            partida.jugarPasoComputador();
            avanzar();
        });
        pausa.play();
    }
    @FXML
    private void onPlantarse() {
        partida.plantarse();   // solo cambia el estado a TURNO_COMPUTADOR
        avanzar();
    }
    /**
     * Pinta TODA la pantalla según lo que diga el modelo.
     * La idea: la pantalla siempre es un reflejo del estado de la Partida.
     */
    private void actualizarVista() {
        lblSaldo.setText("Saldo: " + partida.getSaldo());
        lblApuesta.setText(partida.getApuesta() > 0 ? "Apuesta: " + partida.getApuesta() : "Apuesta: -");
        lblPuntajeJugador.setText("Puntaje: " + puntajeTexto(partida.getManoJugador()));
        lblPuntajeComputador.setText("Puntaje: " + puntajeTexto(partida.getManoComputador()));
        dibujarMano(cartasJugador, partida.getManoJugador());
        dibujarMano(cartasComputador, partida.getManoComputador());

        // Los botones se habilitan o deshabilitan según el estado (lo decide el modelo)
        btnIniciar.setDisable(!partida.puedeIniciarRonda());
        campoApuesta.setDisable(!partida.puedeIniciarRonda());
        btnPedir.setDisable(!partida.puedePedirCarta());
        btnPlantarse.setDisable(!partida.puedePlantarse());
        // "Ver reglas" y "Cerrar juego" quedan siempre habilitados (HU01 criterio 1.4)
    }
        /** Dibuja una imagen por cada carta de la mano. */
        private void dibujarMano(HBox zona, Mano mano) {
            zona.getChildren().clear();                       // borra lo que había
            for (Carta carta : mano.getCartas()) {            // recorre las cartas
                zona.getChildren().add(crearImagenCarta(carta));  // una imagen por carta
            }
        }

/** Arma la imagen de una carta a partir de su rango y su palo. */
        private ImageView crearImagenCarta(Carta carta) {
            String ruta = "/imagenes/" + carta.getRango().getSimbolo() + "_"
                    + carta.getPalo().name().toLowerCase() + ".png";
            ImageView imagen = new ImageView(new Image(getClass().getResourceAsStream(ruta)));
            imagen.setFitWidth(110);
            imagen.setPreserveRatio(true);
            return imagen;
        }
    /** Pregunta si de verdad quiere salir. Devuelve true solo si confirma. */
    boolean confirmarCierre() {
        ButtonType salir = new ButtonType("Salir", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "Se perderá la partida en curso. ¿Quieres cerrar el juego?", salir, cancelar);
        alerta.initOwner(btnCerrar.getScene().getWindow());
        alerta.setTitle("Cerrar juego");
        alerta.setHeaderText("Confirmar cierre");
        return alerta.showAndWait().orElse(cancelar) == salir;
    }

    @FXML
    private void onCerrar() {
        if (confirmarCierre()) {
            Platform.exit();
        }
    }


    /** Si la mano está vacía muestra "-"; si no, el puntaje. */
    private String puntajeTexto(Mano mano) {
        return mano.estaVacia() ? "-" : String.valueOf(mano.puntaje());
    }
}
