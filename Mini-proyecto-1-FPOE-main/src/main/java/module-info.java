module co.edu.univalle.blackjack {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.univalle.blackjack to javafx.fxml;
    exports co.edu.univalle.blackjack;
}