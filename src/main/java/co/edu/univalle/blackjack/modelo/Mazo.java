package co.edu.univalle.blackjack.modelo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**En esta clase tengo planteado realizar un arreglo de 52 posiciones para el mazo,
 no se deben entregar dos veces la misma carta hasta que se haga un mazo nuevo*/

public class Mazo {
    public static final int TAMANO = 52;
    private final Carta[] cartas = new Carta[TAMANO];
    private int cantidad;
    private int siguiente;

    /**Aqui pongo el mazo completo en orden fijo, se mezcla con la función mezclar.*/

    public Mazo() {
        for(Palo palo : Palo.values()){
            for (Rango rango : Rango.values()){
                cartas[cantidad++] = new Carta(rango, palo);
            }
        }
    }

    /**Mazo de orden fijo para las pruebas, la primera carta del arreglo se reparte,
     sin cartas repetidas ni nulas*/

    public Mazo(Carta[] ordenFijo) {
        Objects.requireNonNull(ordenFijo, "El orden no puede ser null");
        if(ordenFijo.length == 0 || ordenFijo.length > TAMANO){
            throw new IllegalArgumentException("El mazo debe tener entre 1 y " + TAMANO + " cartas.");
        }
        Set<Carta> vistas = new HashSet<>();
        for (Carta carta : ordenFijo) {
            if(carta == null){
                throw new IllegalArgumentException("El mazo no puede tener cartas null");
            }
            if (!vistas.add(carta)) {
                throw new IllegalArgumentException("Carta repetida en el mazo: " + carta);
            }
            cartas[cantidad++] = carta;
        }
    }
    /**Realizo la función para mezclar las cartas me ayudo de la ia para esta logica, agegado en REGISTRO_IA.md*/
    public void mezclar(){
        for (int i = cantidad - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            Carta temporal = cartas[i];
            cartas[i] = cartas[j];
            cartas[j] = temporal;
        }
        siguiente = 0;
    }

    /** ESta parte entrega la siguiente carta del mazo*/
    public Carta repartir(){
        if (siguiente >=cantidad){
            throw new IllegalStateException("El mazo no tiene más cartas.");
        }
        return cartas[siguiente++];
    }
    /**Cartas que sobran*/
    public int cartasRestantes(){
        return cantidad - siguiente;
    }
}