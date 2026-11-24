package ar.edu.unlu.poo.truco;

import java.util.List;

public class CalculadorEnvido {

    public int calcular(List<Carta> mano) {
        if (mano.size() != 3) {
            throw new IllegalArgumentException("La mano debe tener exactamente 3 cartas.");
        }

        Carta c1 = mano.get(0);
        Carta c2 = mano.get(1);
        Carta c3 = mano.get(2);

        int v1 = valorEnvido(c1);
        int v2 = valorEnvido(c2);
        int v3 = valorEnvido(c3);

        // Caso 1: Las 3 cartas son del mismo palo
        if (c1.getPalo() == c2.getPalo() && c2.getPalo() == c3.getPalo()) {
            return 20 + Math.max(v1 + v2, Math.max(v1 + v3, v2 + v3));
        }

        // Caso 2: Dos cartas son del mismo palo
        if (c1.getPalo() == c2.getPalo()) return 20 + v1 + v2;
        if (c1.getPalo() == c3.getPalo()) return 20 + v1 + v3;
        if (c2.getPalo() == c3.getPalo()) return 20 + v2 + v3;

        // Caso 3: Tres cartas de distinto palo
        return Math.max(v1, Math.max(v2, v3));
    }

    private int valorEnvido(Carta carta) {
        int numero = carta.getNumero();
        // Las figuras (10, 11 y 12) valen 0 para el envido
        if (numero >= 10) {
            return 0;
        }
        return numero;
    }
}