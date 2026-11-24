package ar.edu.unlu.poo.truco;

import java.util.Arrays;
import java.util.List;

public class Equipo {
    private String nombreEquipo;
    private Jugador jugador1;
    private Jugador jugador2;
    private int puntos;

    public Equipo(String nombreEquipo, Jugador jugador1, Jugador jugador2) {
        this.nombreEquipo = nombreEquipo;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.puntos = 0;
    }

    public String getNombre() {
        return nombreEquipo;
    }

    public void sumarPuntos(int puntos) {
        this.puntos += puntos;
    }

    public int getPuntos() {
        return puntos;
    }

    public List<Jugador> getJugadores() {
        return Arrays.asList(jugador1, jugador2);
    }

    /** Devuelve el otro jugador del equipo. */
    public Jugador getCompaneroDe(Jugador jugador) {
        if (jugador == jugador1) {
            return jugador2;
        }
        if (jugador == jugador2) {
            return jugador1;
        }
        throw new IllegalArgumentException(jugador.getNombre() + " no es de este equipo.");
    }

    public int getMejorEnvido() {
        int envidoJ1 = jugador1.obtenerPuntosEnvido();
        int envidoJ2 = jugador2.obtenerPuntosEnvido();
        return Math.max(envidoJ1, envidoJ2);
    }
}