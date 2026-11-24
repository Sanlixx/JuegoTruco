package ar.edu.unlu.poo.truco.test;

import ar.edu.unlu.poo.truco.Baza;
import ar.edu.unlu.poo.truco.Carta;
import ar.edu.unlu.poo.truco.Equipo;
import ar.edu.unlu.poo.truco.Jugador;
import ar.edu.unlu.poo.truco.Palo;
import ar.edu.unlu.poo.truco.RondaTruco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RondaTrucoTest {
    private Equipo equipoA;
    private Equipo equipoB;
    private Jugador j1, j2, j3, j4;

    @BeforeEach
    void setUp() {
        j1 = new Jugador("J1");
        j2 = new Jugador("J2");
        j3 = new Jugador("J3");
        j4 = new Jugador("J4");

        equipoA = new Equipo("Equipo A", j1, j3);
        equipoB = new Equipo("Equipo B", j2, j4);
    }

    // --- Helpers para armar bazas rápido ---

    private Baza bazaGanadaPorA() {
        Baza baza = new Baza();
        baza.jugarCarta(j1, new Carta(1, Palo.ESPADA)); // Fuerza 14
        baza.jugarCarta(j2, new Carta(4, Palo.BASTO));
        baza.jugarCarta(j3, new Carta(5, Palo.ORO));
        baza.jugarCarta(j4, new Carta(6, Palo.COPA));
        return baza;
    }

    private Baza bazaGanadaPorB() {
        Baza baza = new Baza();
        baza.jugarCarta(j1, new Carta(4, Palo.ESPADA));
        baza.jugarCarta(j2, new Carta(1, Palo.ESPADA)); // Fuerza 14
        baza.jugarCarta(j3, new Carta(5, Palo.ORO));
        baza.jugarCarta(j4, new Carta(6, Palo.COPA));
        return baza;
    }

    private Baza bazaParda() {
        Baza baza = new Baza();
        baza.jugarCarta(j1, new Carta(3, Palo.ESPADA)); // Fuerza 10
        baza.jugarCarta(j2, new Carta(3, Palo.BASTO));  // Fuerza 10
        baza.jugarCarta(j3, new Carta(4, Palo.ORO));
        baza.jugarCarta(j4, new Carta(5, Palo.COPA));
        return baza;
    }

    // --- Tests ---

    @Test
    void testEquipoGanaDosBazasSeguidasGanaLaRonda() {
        RondaTruco ronda = new RondaTruco(equipoA, equipoB, j1);

        // Baza 1: Gana Equipo A
        Baza baza1 = new Baza();
        baza1.jugarCarta(j1, new Carta(1, Palo.ESPADA)); // Carta invencible
        baza1.jugarCarta(j2, new Carta(4, Palo.BASTO));
        baza1.jugarCarta(j3, new Carta(5, Palo.ORO));
        baza1.jugarCarta(j4, new Carta(6, Palo.COPA));
        ronda.registrarBaza(baza1);

        // Baza 2: Gana Equipo A
        Baza baza2 = new Baza();
        baza2.jugarCarta(j1, new Carta(1, Palo.BASTO)); // Segunda mejor
        baza2.jugarCarta(j2, new Carta(4, Palo.ORO));
        baza2.jugarCarta(j3, new Carta(5, Palo.COPA));
        baza2.jugarCarta(j4, new Carta(6, Palo.ESPADA));
        ronda.registrarBaza(baza2);

        assertEquals(equipoA, ronda.determinarGanadorRonda());
    }

    @Test
    void testPardaEnPrimeraBazaGanaQuienHaceLaSegunda() {
        RondaTruco ronda = new RondaTruco(equipoA, equipoB, j1);

        // Baza 1: Parda (Empate en fuerza 10)
        Baza baza1 = new Baza();
        baza1.jugarCarta(j1, new Carta(3, Palo.ESPADA));
        baza1.jugarCarta(j2, new Carta(3, Palo.BASTO));
        baza1.jugarCarta(j3, new Carta(4, Palo.ORO));
        baza1.jugarCarta(j4, new Carta(5, Palo.COPA));
        ronda.registrarBaza(baza1);

        // Baza 2: Gana Equipo B
        Baza baza2 = new Baza();
        baza2.jugarCarta(j1, new Carta(4, Palo.ESPADA));
        baza2.jugarCarta(j2, new Carta(7, Palo.ESPADA)); // Fuerza 12, gana B
        baza2.jugarCarta(j3, new Carta(5, Palo.ORO));
        baza2.jugarCarta(j4, new Carta(6, Palo.COPA));
        ronda.registrarBaza(baza2);

        // Si la 1ra es parda, el ganador de la 2da se lleva la ronda automáticamente
        assertEquals(equipoB, ronda.determinarGanadorRonda());
    }

    @Test
    void testSinBazasNoHayGanador() {
        RondaTruco ronda = new RondaTruco(equipoA, equipoB, j1);

        assertNull(ronda.determinarGanadorRonda());
    }

    @Test
    void testUnaBazaCadaUnoSigueEnJuego() {
        RondaTruco ronda = new RondaTruco(equipoA, equipoB, j1);
        ronda.registrarBaza(bazaGanadaPorA());
        ronda.registrarBaza(bazaGanadaPorB());

        // 1 a 1: falta la tercera baza
        assertNull(ronda.determinarGanadorRonda());
    }

    @Test
    void testPardaEnSegundaBazaGanaQuienHizoLaPrimera() {
        RondaTruco ronda = new RondaTruco(equipoA, equipoB, j1);
        ronda.registrarBaza(bazaGanadaPorB());
        ronda.registrarBaza(bazaParda());

        assertEquals(equipoB, ronda.determinarGanadorRonda());
    }

    @Test
    void testPardaEnTerceraBazaGanaQuienHizoLaPrimera() {
        RondaTruco ronda = new RondaTruco(equipoA, equipoB, j1);
        ronda.registrarBaza(bazaGanadaPorA());
        ronda.registrarBaza(bazaGanadaPorB());
        ronda.registrarBaza(bazaParda());

        assertEquals(equipoA, ronda.determinarGanadorRonda());
    }

    @Test
    void testDosPardasSeguidasYTerceraBazaDecide() {
        RondaTruco ronda = new RondaTruco(equipoA, equipoB, j1);
        ronda.registrarBaza(bazaParda());
        ronda.registrarBaza(bazaParda());

        // Con dos pardas todavía se juega la tercera
        assertNull(ronda.determinarGanadorRonda());

        ronda.registrarBaza(bazaGanadaPorB());
        assertEquals(equipoB, ronda.determinarGanadorRonda());
    }

    @Test
    void testTresPardasGanaElEquipoDelMano() {
        RondaTruco rondaManoA = new RondaTruco(equipoA, equipoB, j1);
        RondaTruco rondaManoB = new RondaTruco(equipoA, equipoB, j2);

        for (int i = 0; i < 3; i++) {
            rondaManoA.registrarBaza(bazaParda());
            rondaManoB.registrarBaza(bazaParda());
        }

        assertEquals(equipoA, rondaManoA.determinarGanadorRonda());
        assertEquals(equipoB, rondaManoB.determinarGanadorRonda());
    }

    @Test
    void testNoSePuedenRegistrarMasDeTresBazas() {
        RondaTruco ronda = new RondaTruco(equipoA, equipoB, j1);
        ronda.registrarBaza(bazaParda());
        ronda.registrarBaza(bazaParda());
        ronda.registrarBaza(bazaParda());

        assertThrows(IllegalStateException.class, () -> ronda.registrarBaza(bazaParda()));
    }
}
