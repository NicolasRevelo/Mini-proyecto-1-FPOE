package co.edu.univalle.blackjack.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Cartas de un participante.
 *
 * VERSION PROVISIONAL: usa un ArrayList para que la interfaz pueda probarse ya.
 * La Persona A debe reemplazar el almacenamiento interno por una LISTA ENLAZADA
 * (la exige el enunciado) SIN cambiar los metodos publicos de esta clase.
 */
public class Mano {

    /**Interruptor, en falso 20 y medio es igual a cualquier par de cartas que some 14 y en verdadero solo una figura*/
    static final boolean VEINTE_Y_MEDIO_SOLO_FIGURA_Y_CUATRO = false;

    private Nodo primero;
    private int numeroCartas;

    /**Para agregar una carta al final de la mano*/
    public void agregar (Carta carta){
        if (carta == null) throw new IllegalArgumentException("La carta no puede ser null");
        Nodo nuevo = new Nodo(carta);
        if (primero == null){
            primero = nuevo;
        }else{
            Nodo actual = primero;
            while (actual.getSiguiente() != null){
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        numeroCartas++;
    }

    public void vaciar(){
        primero = null;
        numeroCartas = 0;
    }
    public boolean estaVacia(){
        return primero == null;
    }
    public int getNumeroCartas(){
        return numeroCartas;
    }
    /**Esta parte es para que la vista dibuje una  imagen por cada carta, en orden*/
    public List <Carta> getCartas(){
        List<Carta> copia = new ArrayList<>();
        for (Nodo actual = primero; actual != null; actual = actual.getSiguiente()) {
            copia.add(actual.getCarta());
        }
        return copia;
    }

    /** Mejor puntaje posible sin pasarse de 21 (los ases valen 1 u 11). */
    public int puntaje() {
        int total = 0;
        boolean hayAs = false;
        for (Nodo actual = primero; actual != null; actual = actual.getSiguiente()) {
            Carta carta = actual.getCarta();
            total += carta.getValor();
            if (carta.getRango().esAs()) hayAs = true;
        }
        // Solo un as puede valer 11 a la vez: si cabe, se suman 10 mas.
        if (hayAs && total + 10 <= 21) total += 10;
        return total;
    }

    /** Blackjack: exactamente dos cartas que suman 21 (As + diez). */
    public boolean esBlackjack() { return numeroCartas == 2 && puntaje() == 21; }

    /**
     * Veinte y medio: dos cartas que suman 14.*/
    /**Decido dejar un switch en caso de que no sea correcto, tambien podemos borrar la linea y ponerlo como
     estaba, segun la logica funcionaria igual.*/
    public boolean esVeinteYMedio() {
        if (numeroCartas != 2 || puntaje() != 14) return false;
        if (!VEINTE_Y_MEDIO_SOLO_FIGURA_Y_CUATRO) return true;
        Rango a = primero.getCarta().getRango();
        Rango b = primero.getSiguiente().getCarta().getRango();
        return (esFigura(a) && b == Rango.CUATRO) || (a == Rango.CUATRO && esFigura(b));
    }

    private static boolean esFigura(Rango rango) {
        return rango == Rango.J || rango == Rango.Q || rango == Rango.K;
    }

    /** Mas de 21 despues de ajustar los ases. */
    public boolean sePaso() { return puntaje() > 21; }

    /** Jugada que corresponde a la mano en este momento. */
    public Jugada getJugada() {
        if (sePaso()) return Jugada.PASARSE;
        if (esBlackjack()) return Jugada.BLACKJACK;
        if (puntaje() == 21) return Jugada.VEINTIUNO;
        if (esVeinteYMedio()) return Jugada.VEINTE_Y_MEDIO;
        return Jugada.NINGUNA;
    }
}
