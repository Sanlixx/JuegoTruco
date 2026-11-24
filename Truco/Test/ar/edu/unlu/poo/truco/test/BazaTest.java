package ar.edu.unlu.poo.truco.test;

import ar.edu.unlu.poo.truco.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BazaTest {
    private Equipo equipoA;
    private Equipo equipoB;
    private Jugador j1, j2, j3, j4;

    @BeforeEach
    void setUp() {
        j1 = new Jugador("Facu");
        j2 = new Jugador("Rival1");
        j3 = new Jugador("Compañero");
        j4 = new Jugador("Rival2");

        equipoA = new Equipo("Equipo A", j1, j3);
        equipoB = new Equipo("Equipo B", j2, j4);
    }

    @Test
    void testCartaMasFuerteGanaLaBaza() {
        Baza baza = new Baza();

        baza.jugarCarta(j1, new Carta(1, Palo.ESPADA)); // Fuerza 14 (Equipo A)
        baza.jugarCarta(j2, new Carta(1, Palo.BASTO));  // Fuerza 13 (Equipo B)
        baza.jugarCarta(j3, new Carta(4, Palo.COPA));   // Fuerza 1  (Equipo A)
        baza.jugarCarta(j4, new Carta(3, Palo.ORO));    // Fuerza 10 (Equipo B)

        assertEquals(equipoA, baza.determinarGanadorDirecto(equipoA, equipoB));
    }

    @Test
    void testEmpateEntreRivalesDevuelveNull() {
        Baza baza = new Baza();

        baza.jugarCarta(j1, new Carta(3, Palo.COPA)); // Fuerza 10 (Equipo A)
        baza.jugarCarta(j2, new Carta(3, Palo.ORO));  // Fuerza 10 (Equipo B)
        baza.jugarCarta(j3, new Carta(2, Palo.ESPADA)); // Fuerza 9
        baza.jugarCarta(j4, new Carta(2, Palo.BASTO));  // Fuerza 9

        assertNull(baza.determinarGanadorDirecto(equipoA, equipoB));
    }

    @Test
    void testEmpateEntreCompanerosNoEsParda() {
        Baza baza = new Baza();

        baza.jugarCarta(j1, new Carta(3, Palo.COPA)); // Fuerza 10 (Equipo A)
        baza.jugarCarta(j2, new Carta(2, Palo.ORO));  // Fuerza 9  (Equipo B)
        baza.jugarCarta(j3, new Carta(3, Palo.ORO));  // Fuerza 10 (Equipo A)
        baza.jugarCarta(j4, new Carta(4, Palo.BASTO)); // Fuerza 1  (Equipo B)

        assertEquals(equipoA, baza.determinarGanadorDirecto(equipoA, equipoB));
    }

    @Test
    void testPardaSeRompeSiUnaCartaSuperiorSeJuegaDespues() {
        Baza baza = new Baza();

        baza.jugarCarta(j1, new Carta(3, Palo.COPA)); // Fuerza 10
        baza.jugarCarta(j2, new Carta(3, Palo.ORO));  // Fuerza 10 (empata con J1)
        baza.jugarCarta(j3, new Carta(1, Palo.BASTO)); // Fuerza 13: rompe la parda
        baza.jugarCarta(j4, new Carta(4, Palo.COPA));

        assertEquals(equipoA, baza.determinarGanadorDirecto(equipoA, equipoB));
    }

    @Test
    void testDeterminarJugadorGanadorDevuelveAlQueTiroLaCartaMasFuerte() {
        Baza baza = new Baza();

        baza.jugarCarta(j1, new Carta(4, Palo.COPA));
        baza.jugarCarta(j2, new Carta(7, Palo.ESPADA)); // Fuerza 12: la más fuerte
        baza.jugarCarta(j3, new Carta(5, Palo.ORO));
        baza.jugarCarta(j4, new Carta(6, Palo.BASTO));

        assertSame(j2, baza.determinarJugadorGanador(equipoA, equipoB));
    }

    @Test
    void testDeterminarJugadorGanadorEnParda() {
        Baza baza = new Baza();

        baza.jugarCarta(j1, new Carta(3, Palo.COPA));
        baza.jugarCarta(j2, new Carta(3, Palo.ORO));
        baza.jugarCarta(j3, new Carta(4, Palo.ESPADA));
        baza.jugarCarta(j4, new Carta(5, Palo.BASTO));

        assertNull(baza.determinarJugadorGanador(equipoA, equipoB));
    }

    @Test
    void testCompanerosConCartasIgualesGanaElQueJugoPrimero() {
        Baza baza = new Baza();

        baza.jugarCarta(j1, new Carta(4, Palo.COPA));
        baza.jugarCarta(j2, new Carta(5, Palo.ORO));
        baza.jugarCarta(j3, new Carta(3, Palo.ESPADA)); // Fuerza 10, la juega primero
        baza.jugarCarta(j4, new Carta(2, Palo.BASTO));
        baza.jugarCarta(j1, new Carta(3, Palo.COPA));   // Fuerza 10, la juega después

        assertSame(j3, baza.determinarJugadorGanador(equipoA, equipoB));
    }

    @Test
    void testBazaSinCartasLanzaExcepcion() {
        Baza baza = new Baza();

        assertThrows(IllegalStateException.class, () -> baza.determinarGanadorDirecto(equipoA, equipoB));
    }

    @Test
    void testLasJugadasNoSePuedenModificarDesdeAfuera() {
        Baza baza = new Baza();
        baza.jugarCarta(j1, new Carta(1, Palo.ESPADA));

        assertThrows(UnsupportedOperationException.class, () -> baza.getJugadas().clear());
        assertEquals(1, baza.getJugadas().size());
    }
}
