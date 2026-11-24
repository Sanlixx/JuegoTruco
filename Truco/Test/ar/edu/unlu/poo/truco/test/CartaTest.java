package ar.edu.unlu.poo.truco.test;

import ar.edu.unlu.poo.truco.Carta;
import ar.edu.unlu.poo.truco.Palo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

    public class CartaTest {

        @Test
        void testCrearCartaValida() {
            Carta carta = new Carta(1, Palo.ESPADA);

            assertEquals(1, carta.getNumero());
            assertEquals(Palo.ESPADA, carta.getPalo());
        }

        @Test
        void testCrearCartaInvalidaLanzaExcepcion() {
            // En el Truco no se juega con 8 ni con 9
            assertThrows(IllegalArgumentException.class, () -> {
                new Carta(8, Palo.ORO);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new Carta(9, Palo.COPA);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new Carta(13, Palo.BASTO);
            });
        }
    }
