package ar.edu.unlu.poo.truco.test;

import ar.edu.unlu.poo.truco.Carta;
import ar.edu.unlu.poo.truco.Mazo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MazoTest {
    private Mazo mazo;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de cada @Test para garantizar un estado limpio
        mazo = new Mazo();
    }

    @Test
    void testMazoNuevoTiene40Cartas() {
        assertEquals(40, mazo.getCantidadCartas());
    }

    @Test
    void testSacarCartaReduceCantidad() {
        Carta cartaExtraida = mazo.sacarCarta();

        assertNotNull(cartaExtraida);
        assertEquals(39, mazo.getCantidadCartas());
    }

    @Test
    void testMazoVacioLanzaExcepcion() {
        // Vaciamos el mazo sacando las 40 cartas
        for (int i = 0; i < 40; i++) {
            mazo.sacarCarta();
        }

        // La carta 41 debe lanzar error
        assertThrows(IllegalStateException.class, () -> {
            mazo.sacarCarta();
        });
    }
}