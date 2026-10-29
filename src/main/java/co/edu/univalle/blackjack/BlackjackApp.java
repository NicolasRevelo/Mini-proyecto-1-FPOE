package co.edu.univalle.blackjack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase que arranca la aplicación: carga la pantalla (FXML) y abre la ventana.
 * El FXML ya sabe cuál es su controlador (fx:controller), así que aquí no hace falta crearlo.
 */
public class BlackjackApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader cargador = new FXMLLoader(BlackjackApp.class.getResource("blackjack-view.fxml"));
        Scene escena = new Scene(cargador.load(), 900, 680);
        stage.setTitle("21 Black Jack");
        stage.setScene(escena);
        stage.setMinWidth(760);
        stage.setMinHeight(620);
        BlackjackController controlador = cargador.getController();
        stage.setOnCloseRequest(evento -> {
            if (!controlador.confirmarCierre()) {
                evento.consume();   // consume = "cancelar el cierre": la ventana sigue abierta
            }
        });
        stage.show();
    }
}
