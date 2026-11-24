package ar.edu.unlu.poo.truco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partida {
    private static final int CARTAS_POR_JUGADOR = 3;

    private Equipo equipoA;
    private Equipo equipoB;
    private Mazo mazo;

    // Lista para mantener el orden exacto de los turnos en la mesa
    private List<Jugador> ordenMesa;
    // Posición (dentro de ordenMesa) del jugador que es mano en la ronda actual
    private int indiceMano;

    public Partida(Equipo equipoA, Equipo equipoB) {
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.ordenMesa = new ArrayList<>();

        // El orden de la mesa se intercala: uno del A, uno del B, el otro del A, el otro del B
        ordenMesa.add(equipoA.getJugadores().get(0));
        ordenMesa.add(equipoB.getJugadores().get(0));
        ordenMesa.add(equipoA.getJugadores().get(1));
        ordenMesa.add(equipoB.getJugadores().get(1));

        // La primera mano es el primero de la mesa
        this.indiceMano = 0;
    }

    public List<Jugador> getOrdenMesa() {
        return Collections.unmodifiableList(ordenMesa);
    }

    public Jugador getJugadorMano() {
        return ordenMesa.get(indiceMano);
    }

    // Al terminar una ronda, la mano pasa al siguiente jugador de la mesa
    public void pasarMano() {
        indiceMano = (indiceMano + 1) % ordenMesa.size();
    }

    public void repartirCartas() {
        // Vaciamos las manos de los 4 jugadores
        for (Jugador jugador : ordenMesa) {
            jugador.vaciarMano();
        }

        // Mazo nuevo y mezclado
        mazo = new Mazo();
        mazo.mezclar();

        // Repartimos 3 cartas a cada jugador, de a una, empezando por la mano
        for (int ronda = 0; ronda < CARTAS_POR_JUGADOR; ronda++) {
            for (int i = 0; i < ordenMesa.size(); i++) {
                Jugador jugador = ordenMesa.get((indiceMano + i) % ordenMesa.size());
                jugador.recibirCarta(mazo.sacarCarta());
            }
        }
    }

    // Los puntos se suman al equipo, no a un jugador aislado
    public void sumarPuntosEquipoA(int puntos) {
        equipoA.sumarPuntos(puntos);
    }

    public void sumarPuntosEquipoB(int puntos) {
        equipoB.sumarPuntos(puntos);
    }

    public Equipo resolverEnvido() {
        int puntosA = equipoA.getMejorEnvido();
        int puntosB = equipoB.getMejorEnvido();

        if (puntosA > puntosB) {
            return equipoA;
        } else if (puntosB > puntosA) {
            return equipoB;
        } else {
            // Empate: gana el equipo del jugador que esté más cerca de la mano
            return desempatarPorCercaniaALaMano(puntosA);
        }
    }

    private Equipo desempatarPorCercaniaALaMano(int puntosEmpatados) {
        // Recorremos la mesa a partir de la mano. El primer jugador (incluyendo
        // la mano misma) que tenga el puntaje empatado le da la victoria a su equipo.
        for (int i = 0; i < ordenMesa.size(); i++) {
            // Usamos módulo para dar la vuelta a la mesa si llegamos al final de la lista
            Jugador j = ordenMesa.get((indiceMano + i) % ordenMesa.size());

            if (j.obtenerPuntosEnvido() == puntosEmpatados) {
                if (equipoA.getJugadores().contains(j)) {
                    return equipoA;
                }
                return equipoB;
            }
        }

        // Fallback de seguridad (nunca debería llegar acá: algún jugador tiene el puntaje empatado)
        return equipoA;
    }
}
