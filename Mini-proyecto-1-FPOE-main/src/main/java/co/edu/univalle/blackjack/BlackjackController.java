package co.edu.univalle.blackjack;

import co.edu.univalle.blackjack.modelo.Carta;
import co.edu.univalle.blackjack.modelo.Mano;
import co.edu.univalle.blackjack.modelo.Partida;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 (Persona B).
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
     */
    @FXML
    private void onIniciarRonda() {
        String error = partida.validarApuesta(campoApuesta.getText());
        if (error != null) {
            lblMensaje.setText(error);   // apuesta incorrecta: solo se muestra el motivo
            return;
        }
        partida.iniciarRonda(Integer.parseInt(campoApuesta.getText().trim()));
        actualizarVista();
    }
    /** Se ejecuta al pulsar "Pedir carta". */
    @FXML
    private void onPedirCarta() {
        partida.pedirCarta();   // le pedimos al modelo que reparta una carta más
        actualizarVista();      // volvemos a leer el modelo y repintamos
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


    /** Si la mano está vacía muestra "-"; si no, el puntaje. */
    private String puntajeTexto(Mano mano) {
        return mano.estaVacia() ? "-" : String.valueOf(mano.puntaje());
    }
}
