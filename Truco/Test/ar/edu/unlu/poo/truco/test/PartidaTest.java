package ar.edu.unlu.poo.truco.test;

import ar.edu.unlu.poo.truco.Carta;
import ar.edu.unlu.poo.truco.Equipo;
import ar.edu.unlu.poo.truco.Jugador;
import ar.edu.unlu.poo.truco.Palo;
import ar.edu.unlu.poo.truco.Partida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PartidaTest {
    private Equipo equipoA;
    private Equipo equipoB;
    private Jugador j1, j2, j3, j4;
    private Partida partida;

    @BeforeEach
    void setUp() {
        j1 = new Jugador("J1");
        j2 = new Jugador("J2");
        j3 = new Jugador("J3");
        j4 = new Jugador("J4");

        equipoA = new Equipo("Equipo A", j1, j3);
        equipoB = new Equipo("Equipo B", j2, j4);
        partida = new Partida(equipoA, equipoB);
    }

    private void dar(Jugador jugador, Carta c1, Carta c2, Carta c3) {
        jugador.recibirCarta(c1);
        jugador.recibirCarta(c2);
        jugador.recibirCarta(c3);
    }

    // Deja a J1 (equipo A) y J2 (equipo B) con 33 de envido, y a sus compañeros con menos
    private void armarEmpateDeEnvido33() {
        dar(j1, new Carta(7, Palo.ESPADA), new Carta(6, Palo.ESPADA), new Carta(1, Palo.COPA));
        dar(j2, new Carta(7, Palo.ORO), new Carta(6, Palo.ORO), new Carta(1, Palo.BASTO));
        dar(j3, new Carta(4, Palo.COPA), new Carta(5, Palo.BASTO), new Carta(10, Palo.ORO));
        dar(j4, new Carta(1, Palo.COPA), new Carta(2, Palo.BASTO), new Carta(3, Palo.ORO));
    }

    @Test
    void testLaMesaIntercalaLosEquipos() {
        assertEquals(List.of(j1, j2, j3, j4), partida.getOrdenMesa());
    }

    @Test
    void testRepartirDa3CartasDistintasACadaJugador() {
        partida.repartirCartas();

        Set<String> cartasRepartidas = new HashSet<>();
        for (Jugador jugador : partida.getOrdenMesa()) {
            assertEquals(3, jugador.getCartasEnMano().size());
            jugador.getCartasEnMano().forEach(c -> cartasRepartidas.add(c.toString()));
        }
        assertEquals(12, cartasRepartidas.size());
    }

    @Test
    void testRepartirDosVecesVaciaLasManosAnteriores() {
        partida.repartirCartas();
        j1.jugarCarta(0);

        partida.repartirCartas();

        for (Jugador jugador : partida.getOrdenMesa()) {
            assertEquals(3, jugador.getCartasEnMano().size());
        }
    }

    @Test
    void testLaPrimeraManoEsElPrimeroDeLaMesa() {
        assertSame(j1, partida.getJugadorMano());
    }

    @Test
    void testPasarManoAvanzaEnElOrdenDeLaMesaYDaLaVuelta() {
        partida.pasarMano();
        assertSame(j2, partida.getJugadorMano());
        partida.pasarMano();
        assertSame(j3, partida.getJugadorMano());
        partida.pasarMano();
        assertSame(j4, partida.getJugadorMano());
        partida.pasarMano();
        assertSame(j1, partida.getJugadorMano());
    }

    @Test
    void testEnvidoGanaElEquipoConMejorPuntaje() {
        dar(j1, new Carta(7, Palo.ESPADA), new Carta(5, Palo.ESPADA), new Carta(4, Palo.BASTO)); // 32
        dar(j2, new Carta(1, Palo.COPA), new Carta(2, Palo.BASTO), new Carta(3, Palo.ORO));      // 3
        dar(j3, new Carta(4, Palo.COPA), new Carta(5, Palo.BASTO), new Carta(10, Palo.ORO));     // 5
        dar(j4, new Carta(10, Palo.COPA), new Carta(11, Palo.BASTO), new Carta(12, Palo.ORO));   // 0

        assertSame(equipoA, partida.resolverEnvido());
    }

    @Test
    void testEnvidoUsaElMejorJugadorDelEquipo() {
        dar(j1, new Carta(1, Palo.COPA), new Carta(2, Palo.BASTO), new Carta(3, Palo.ORO));      // 3
        dar(j2, new Carta(4, Palo.COPA), new Carta(5, Palo.BASTO), new Carta(10, Palo.ORO));     // 5
        dar(j3, new Carta(10, Palo.COPA), new Carta(11, Palo.BASTO), new Carta(12, Palo.ORO));   // 0
        dar(j4, new Carta(7, Palo.ORO), new Carta(6, Palo.ORO), new Carta(1, Palo.BASTO));       // 33

        assertSame(equipoB, partida.resolverEnvido());
    }

    @Test
    void testEmpateDeEnvidoGanaElEquipoDelJugadorMasCercanoALaMano() {
        armarEmpateDeEnvido33();

        // Mano = J1 (equipo A)
        assertSame(equipoA, partida.resolverEnvido());

        // Mano = J2 (equipo B): ahora J2 está más cerca de la mano que J1
        partida.pasarMano();
        assertSame(equipoB, partida.resolverEnvido());
    }

    @Test
    void testEnvidoSeResuelveAunqueYaSeHayanJugadoCartas() {
        armarEmpateDeEnvido33();
        j1.jugarCarta(0);
        j2.jugarCarta(0);

        // Regresión: antes lanzaba IllegalStateException porque a los jugadores les faltaban cartas
        assertDoesNotThrow(() -> partida.resolverEnvido());
    }

    @Test
    void testSumarPuntosVaAlEquipoCorrespondiente() {
        partida.sumarPuntosEquipoA(3);
        partida.sumarPuntosEquipoB(2);
        partida.sumarPuntosEquipoA(1);

        assertEquals(4, equipoA.getPuntos());
        assertEquals(2, equipoB.getPuntos());
    }
}
