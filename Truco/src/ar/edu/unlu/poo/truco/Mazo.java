package ar.edu.unlu.poo.truco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mazo {
    private List<Carta> cartas;

    public Mazo() {
        this.cartas = new ArrayList<>();
        inicializarMazo();
    }

    private void inicializarMazo() {
        int[] numerosValidos = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12};

        for (Palo palo : Palo.values()) {
            for (int numero : numerosValidos) {
                cartas.add(new Carta(numero, palo));
            }
        }
    }

    public void mezclar() {
        Collections.shuffle(cartas);
    }

    public Carta sacarCarta() {
        if (cartas.isEmpty()) {
            throw new IllegalStateException("No hay más cartas en el mazo.");
        }
        // Sacamos la "carta de arriba" (la última de la lista es más eficiente de remover)
        return cartas.remove(cartas.size() - 1);
    }

    public int getCantidadCartas() {
        return cartas.size();
    }
}