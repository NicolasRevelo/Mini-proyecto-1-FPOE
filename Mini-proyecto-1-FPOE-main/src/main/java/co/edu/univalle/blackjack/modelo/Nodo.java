package co.edu.univalle.blackjack.modelo;

/**Este archivo esta basicamente planteado para que la Mano guarde sus cartas,
 los nodos guardan una carta y apuntan a null si es el ultimo*/

public class Nodo {
    private final Carta carta;
    private Nodo siguiente;

    Nodo(Carta carta) {
        this.carta = carta;
    }
    Carta getCarta() {return carta;}
    Nodo getSiguiente() {return siguiente;}
    void setSiguiente(Nodo siguiente) { this.siguiente = siguiente; }
}
