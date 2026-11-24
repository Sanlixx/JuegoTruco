package ar.edu.unlu.poo.truco;

import java.util.ArrayList;
import java.util.List;

public class RondaTruco {
    private final Equipo equipoA;
    private final Equipo equipoB;
    private final Jugador jugadorMano;
    private final List<Baza> historialBazas;

    public RondaTruco(Equipo equipoA, Equipo equipoB, Jugador jugadorMano) {
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.jugadorMano = jugadorMano;
        this.historialBazas = new ArrayList<>();
    }

    public void registrarBaza(Baza baza) {
        if (historialBazas.size() >= 3) {
            throw new IllegalStateException("La ronda ya tiene el máximo de 3 bazas.");
        }
        historialBazas.add(baza);
    }

    public Equipo determinarGanadorRonda() {
        if (historialBazas.isEmpty()) {
            return null; // Aún no hay juego
        }

        List<Equipo> ganadoresParciales = new ArrayList<>();

        for (Baza baza : historialBazas) {
            ganadoresParciales.add(baza.determinarGanadorDirecto(equipoA, equipoB));
        }

        int triunfosA = 0;
        int triunfosB = 0;

        for (Equipo ganador : ganadoresParciales) {
            if (ganador == equipoA) triunfosA++;
            if (ganador == equipoB) triunfosB++;
        }

        // Regla 1: Ganador directo (hizo 2 bazas)
        if (triunfosA >= 2) return equipoA;
        if (triunfosB >= 2) return equipoB;

        // Regla 2: Hubo alguna parda (null en la lista de ganadores parciales)
        if (ganadoresParciales.contains(null)) {
            Equipo ganadorPrimera = ganadoresParciales.get(0);

            // Si la primera fue parda, gana quien hizo la segunda (o tercera)
            if (ganadorPrimera == null) {
                if (ganadoresParciales.size() > 1 && ganadoresParciales.get(1) != null) {
                    return ganadoresParciales.get(1);
                }
                if (ganadoresParciales.size() > 2 && ganadoresParciales.get(2) != null) {
                    return ganadoresParciales.get(2);
                }
            }
            // Si la parda fue en la 2da o 3ra, gana el que hizo la primera
            else {
                return ganadorPrimera;
            }
        }

        // Regla 3: Si se jugaron las 3 bazas, fueron TODAS pardas, gana el equipo del jugador Mano.
        if (historialBazas.size() == 3) {
            if (equipoA.getJugadores().contains(jugadorMano)) {
                return equipoA;
            }
            return equipoB;
        }

        // Si la ronda aún está en curso y no se cumplen reglas de victoria anticipada
        return null;
    }
}