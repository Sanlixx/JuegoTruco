package ar.edu.unlu.poo.truco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baza {
    private final List<CartaJugada> jugadas;
    private final ReglasTruco reglas;

    public Baza() {
        this.jugadas = new ArrayList<>();
        this.reglas = new ReglasTruco();
    }

    public void jugarCarta(Jugador jugador, Carta carta) {
        jugadas.add(new CartaJugada(jugador, carta));
    }

    public Equipo determinarGanadorDirecto(Equipo equipoA, Equipo equipoB) {
        if (jugadas.isEmpty()) {
            throw new IllegalStateException("No se han jugado cartas en esta baza.");
        }

        int maxFuerza = -1;
        Equipo equipoGanador = null;
        boolean hayParda = false;

        for (CartaJugada cj : jugadas) {
            int fuerzaActual = reglas.obtenerFuerza(cj.getCarta());
            Equipo equipoActual;
            if (equipoA.getJugadores().contains(cj.getJugador())) {
                equipoActual = equipoA;
            } else {
                equipoActual = equipoB;
            }

            if (fuerzaActual > maxFuerza) {
                maxFuerza = fuerzaActual;
                equipoGanador = equipoActual;
                hayParda = false; // Se rompió la parda porque alguien tiró una carta superior
            } else if (fuerzaActual == maxFuerza && equipoActual != equipoGanador) {
                // Hay empate (parda) entre rivales
                hayParda = true;
            }
        }

        // Si hay parda, no hay ganador directo en esta baza.
        // La ronda (que conoce el orden de la mesa y quién es mano) decidirá el desempate.
        if (hayParda) {
            return null;
        }
        return equipoGanador;
    }

    // Devuelve el jugador que ganó la baza (el que abre la baza siguiente),
    // o null si hubo parda. Si los dos compañeros tiraron cartas de igual fuerza
    // y ganaron, se considera ganador al que la jugó primero.
    public Jugador determinarJugadorGanador(Equipo equipoA, Equipo equipoB) {
        Equipo equipoGanador = determinarGanadorDirecto(equipoA, equipoB);
        if (equipoGanador == null) {
            return null; // Parda
        }

        int maxFuerza = -1;
        for (CartaJugada cj : jugadas) {
            maxFuerza = Math.max(maxFuerza, reglas.obtenerFuerza(cj.getCarta()));
        }

        for (CartaJugada cj : jugadas) {
            boolean esDelEquipoGanador = equipoGanador.getJugadores().contains(cj.getJugador());
            if (esDelEquipoGanador && reglas.obtenerFuerza(cj.getCarta()) == maxFuerza) {
                return cj.getJugador();
            }
        }

        return null; // No debería llegar acá: si hay equipo ganador, hay una carta ganadora
    }

    // Vista de solo lectura, para que nadie modifique las jugadas desde afuera.
    public List<CartaJugada> getJugadas() {
        return Collections.unmodifiableList(jugadas);
    }
}
