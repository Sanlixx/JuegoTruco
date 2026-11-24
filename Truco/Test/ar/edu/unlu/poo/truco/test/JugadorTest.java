package ar.edu.unlu.poo.truco.test;

import ar.edu.unlu.poo.truco.Carta;
import ar.edu.unlu.poo.truco.Jugador;
import ar.edu.unlu.poo.truco.Palo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JugadorTest {
    private Jugador jugador;

    @BeforeEach
    void setUp() {
        jugador = new Jugador("Facundo");
    }

    private void repartirManoDeEnvido32() {
        jugador.recibirCarta(new Carta(7, Palo.ESPADA));
        jugador.recibirCarta(new Carta(5, Palo.ESPADA));
        jugador.recibirCarta(new Carta(4, Palo.BASTO));
    }

    @Test
    void testJugadorIniciaSinCartas() {
        assertEquals(0, jugador.getCartasEnMano().size());
        assertEquals("Facundo", jugador.getNombre());
    }

    @Test
    void testRecibirCartas() {
        jugador.recibirCarta(new Carta(1, Palo.ESPADA));
        jugador.recibirCarta(new Carta(7, Palo.ORO));
        jugador.recibirCarta(new Carta(3, Palo.COPA));

        assertEquals(3, jugador.getCartasEnMano().size());
    }

    @Test
    void testNoPuedeRecibirMasDeTresCartas() {
        jugador.recibirCarta(new Carta(1, Palo.ESPADA));
        jugador.recibirCarta(new Carta(7, Palo.ORO));
        jugador.recibirCarta(new Carta(3, Palo.COPA));

        assertThrows(IllegalStateException.class, () -> {
            jugador.recibirCarta(new Carta(4, Palo.BASTO));
        });
    }

    @Test
    void testNoPuedeRecibirUnaCuartaCartaDespuesDeJugarUna() {
        repartirManoDeEnvido32();
        jugador.jugarCarta(0);

        assertThrows(IllegalStateException.class, () -> {
            jugador.recibirCarta(new Carta(1, Palo.ORO));
        });
    }

    @Test
    void testJugarCartaReduceLaMano() {
        Carta anchoEspada = new Carta(1, Palo.ESPADA);
        jugador.recibirCarta(anchoEspada);

        Carta jugada = jugador.jugarCarta(0);

        assertEquals(anchoEspada, jugada);
        assertEquals(0, jugador.getCartasEnMano().size());
    }

    @Test
    void testJugarCartaConIndiceInvalidoLanzaExcepcion() {
        repartirManoDeEnvido32();

        assertThrows(IllegalArgumentException.class, () -> jugador.jugarCarta(3));
        assertThrows(IllegalArgumentException.class, () -> jugador.jugarCarta(-1));
    }

    @Test
    void testCantarEnvidoDelegaAlCalculador() {
        repartirManoDeEnvido32();

        // 20 + 7 + 5 = 32
        assertEquals(32, jugador.obtenerPuntosEnvido());
    }

    @Test
    void testEnvidoSeCalculaConLaManoOriginalAunqueYaSeHayanJugadoCartas() {
        repartirManoDeEnvido32();

        // Juega el 7 de espada en la primera baza: el envido sigue valiendo 32
        jugador.jugarCarta(0);
        assertEquals(2, jugador.getCartasEnMano().size());
        assertEquals(32, jugador.obtenerPuntosEnvido());

        // Y también después de jugar la segunda carta
        jugador.jugarCarta(0);
        assertEquals(32, jugador.obtenerPuntosEnvido());
    }

    @Test
    void testEnvidoSinLasTresCartasLanzaExcepcion() {
        jugador.recibirCarta(new Carta(7, Palo.ESPADA));

        assertThrows(IllegalStateException.class, () -> jugador.obtenerPuntosEnvido());
    }

    @Test
    void testVaciarManoPermiteRepartirDeNuevo() {
        repartirManoDeEnvido32();
        jugador.jugarCarta(0);

        jugador.vaciarMano();
        assertEquals(0, jugador.getCartasEnMano().size());

        jugador.recibirCarta(new Carta(1, Palo.ESPADA));
        jugador.recibirCarta(new Carta(1, Palo.BASTO));
        jugador.recibirCarta(new Carta(3, Palo.COPA));
        assertEquals(3, jugador.getCartasEnMano().size());
    }

    @Test
    void testLaManoNoSePuedeModificarDesdeAfuera() {
        repartirManoDeEnvido32();

        assertThrows(UnsupportedOperationException.class, () -> jugador.getCartasEnMano().clear());
    }
}
