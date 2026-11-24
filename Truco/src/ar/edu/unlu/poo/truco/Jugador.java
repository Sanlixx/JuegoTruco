package ar.edu.unlu.poo.truco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jugador {
    private static final int CARTAS_POR_MANO = 3;

    private String nombre;
    // Las 3 cartas repartidas al inicio de la ronda. No cambia al jugar cartas:
    // el envido se calcula siempre con la mano original.
    private List<Carta> manoRepartida;
    // Las cartas que todavía no jugó.
    private List<Carta> cartasEnMano;
    private CalculadorEnvido calculadorEnvido;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.manoRepartida = new ArrayList<>();
        this.cartasEnMano = new ArrayList<>();
        this.calculadorEnvido = new CalculadorEnvido();
    }

    public String getNombre() {
        return nombre;
    }

    public void recibirCarta(Carta carta) {
        // Se controla contra la mano repartida (y no contra las cartas que quedan),
        // para que no se pueda recibir una cuarta carta después de haber jugado una.
        if (manoRepartida.size() >= CARTAS_POR_MANO) {
            throw new IllegalStateException("El jugador ya recibió sus 3 cartas.");
        }
        manoRepartida.add(carta);
        cartasEnMano.add(carta);
    }

    public Carta jugarCarta(int indice) {
        if (indice < 0 || indice >= cartasEnMano.size()) {
            throw new IllegalArgumentException("Índice de carta inválido.");
        }
        return cartasEnMano.remove(indice);
    }

    public int obtenerPuntosEnvido() {
        if (manoRepartida.size() != CARTAS_POR_MANO) {
            throw new IllegalStateException("Se necesitan 3 cartas para calcular el envido.");
        }
        // El envido se calcula con las 3 cartas repartidas, aunque ya se hayan jugado algunas.
        return calculadorEnvido.calcular(manoRepartida);
    }

    // Encapsulamiento: retornamos una vista de solo lectura para evitar que
    // otras clases modifiquen la lista con un .add() o .clear() desde afuera.
    // Devuelve solo las cartas que todavía no se jugaron.
    public List<Carta> getCartasEnMano() {
        return Collections.unmodifiableList(cartasEnMano);
    }

    public void vaciarMano() {
        manoRepartida.clear();
        cartasEnMano.clear();
    }
}
