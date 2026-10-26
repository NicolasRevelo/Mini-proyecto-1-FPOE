package co.edu.univalle.blackjack;

import co.edu.univalle.blackjack.modelo.Mano;
import co.edu.univalle.blackjack.modelo.Partida;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Controlador de la pantalla principal (Persona B).
 * Regla de oro del contrato: este controlador NUNCA cambia el saldo, las manos
 * ni el estado directamente. Le pide cosas a la Partida (el modelo) y luego
 * vuelve a leer para dibujar la pantalla.
 */
public class BlackjackController {

    /** El modelo. Todo lo que pasa en el juego vive aquí. */
    private final Partida partida = new Partida();

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

    /**
     * Se ejecuta al pulsar "Iniciar ronda" (onAction="#onIniciarRonda" en el FXML).
     * Por ahora solo valida la apuesta (HU02). El reparto de cartas es del Día 3.
     */
    @FXML
    private void onIniciarRonda() {
        String error = partida.validarApuesta(campoApuesta.getText());
        if (error != null) {
            lblMensaje.setText(error);   // apuesta incorrecta: solo se muestra el motivo
            return;
        }
        lblMensaje.setText("Apuesta válida. El reparto de cartas se programa el Día 3.");
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

        // Los botones se habilitan o deshabilitan según el estado (lo decide el modelo)
        btnIniciar.setDisable(!partida.puedeIniciarRonda());
        campoApuesta.setDisable(!partida.puedeIniciarRonda());
        btnPedir.setDisable(!partida.puedePedirCarta());
        btnPlantarse.setDisable(!partida.puedePlantarse());
        // "Ver reglas" y "Cerrar juego" quedan siempre habilitados (HU01 criterio 1.4)
    }

    /** Si la mano está vacía muestra "-"; si no, el puntaje. */
    private String puntajeTexto(Mano mano) {
        return mano.estaVacia() ? "-" : String.valueOf(mano.puntaje());
    }
}
