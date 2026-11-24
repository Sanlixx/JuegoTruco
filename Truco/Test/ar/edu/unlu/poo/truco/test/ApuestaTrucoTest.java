package ar.edu.unlu.poo.truco.test;

import ar.edu.unlu.poo.truco.ApuestaTruco;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApuestaTrucoTest {

    @Test
    void testTrucoQueridoSuma2PuntosYNoQuerido1() {
        ApuestaTruco apuesta = new ApuestaTruco(); // Inicia sin cantos (vale 1)
        apuesta.cantarTruco();

        assertEquals(2, apuesta.getPuntosQuerido());
        assertEquals(1, apuesta.getPuntosNoQuerido());
    }

    @Test
    void testRetrucoQueridoSuma3PuntosYNoQuerido2() {
        ApuestaTruco apuesta = new ApuestaTruco();
        apuesta.cantarTruco();
        apuesta.cantarRetruco();

        assertEquals(3, apuesta.getPuntosQuerido());
        assertEquals(2, apuesta.getPuntosNoQuerido());
    }

    @Test
    void testValeCuatroQueridoSuma4PuntosYNoQuerido3() {
        ApuestaTruco apuesta = new ApuestaTruco();
        apuesta.cantarTruco();
        apuesta.cantarRetruco();
        apuesta.cantarValeCuatro();

        assertEquals(4, apuesta.getPuntosQuerido());
        assertEquals(3, apuesta.getPuntosNoQuerido());
    }

    @Test
    void testNoSePuedeCantarRetrucoSinTrucoPrevio() {
        ApuestaTruco apuesta = new ApuestaTruco(); // Nadie cantó nada

        // Debe fallar si se saltean la jerarquía
        assertThrows(IllegalStateException.class, () -> {
            apuesta.cantarRetruco();
        });
    }
}