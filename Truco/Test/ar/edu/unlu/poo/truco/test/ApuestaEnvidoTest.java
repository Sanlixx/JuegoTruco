package ar.edu.unlu.poo.truco.test;

import ar.edu.unlu.poo.truco.ApuestaEnvido;
import ar.edu.unlu.poo.truco.TipoApuesta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApuestaEnvidoTest {

    @Test
    void testEnvidoQueridoSuma2Puntos() {
        ApuestaEnvido apuesta = new ApuestaEnvido(TipoApuesta.ENVIDO);
        assertEquals(2, apuesta.getPuntosQuerido());
    }

    @Test
    void testEnvidoNoQueridoSuma1Punto() {
        ApuestaEnvido apuesta = new ApuestaEnvido(TipoApuesta.ENVIDO);
        assertEquals(1, apuesta.getPuntosNoQuerido());
    }

    @Test
    void testSubirApuestaAEnvidoRealSuma5PuntosSiEsQuerido() {
        ApuestaEnvido base = new ApuestaEnvido(TipoApuesta.ENVIDO);
        ApuestaEnvido subida = base.subirApuesta(TipoApuesta.REAL_ENVIDO);

        // Envido (2) + Real Envido (3) = 5
        assertEquals(5, subida.getPuntosQuerido());

        // Si no quieren el Real Envido, se llevan los puntos de la apuesta anterior (2)
        assertEquals(2, subida.getPuntosNoQuerido());
    }

    @Test
    void testNoSePuedeBajarLaApuesta() {
        ApuestaEnvido base = new ApuestaEnvido(TipoApuesta.REAL_ENVIDO);

        // No se puede cantar Envido si ya cantaron Real Envido
        assertThrows(IllegalStateException.class, () -> {
            base.subirApuesta(TipoApuesta.ENVIDO);
        });
    }

    @Test
    void testEnvidoEnvidoSuma4PuntosSiEsQuerido() {
        ApuestaEnvido apuesta = new ApuestaEnvido(TipoApuesta.ENVIDO)
                .subirApuesta(TipoApuesta.ENVIDO);

        // Envido (2) + Envido (2) = 4
        assertEquals(4, apuesta.getPuntosQuerido());
        // Si no quieren el segundo Envido, se llevan los 2 del primero
        assertEquals(2, apuesta.getPuntosNoQuerido());
    }

    @Test
    void testEnvidoEnvidoRealEnvidoSuma7PuntosSiEsQuerido() {
        ApuestaEnvido apuesta = new ApuestaEnvido(TipoApuesta.ENVIDO)
                .subirApuesta(TipoApuesta.ENVIDO)
                .subirApuesta(TipoApuesta.REAL_ENVIDO);

        // 2 + 2 + 3 = 7
        assertEquals(7, apuesta.getPuntosQuerido());
        assertEquals(4, apuesta.getPuntosNoQuerido());
    }

    @Test
    void testNoSePuedeCantarEnvidoTresVeces() {
        ApuestaEnvido dosEnvidos = new ApuestaEnvido(TipoApuesta.ENVIDO)
                .subirApuesta(TipoApuesta.ENVIDO);

        assertThrows(IllegalStateException.class, () -> {
            dosEnvidos.subirApuesta(TipoApuesta.ENVIDO);
        });
    }

    @Test
    void testNoSePuedeRepetirRealEnvido() {
        ApuestaEnvido real = new ApuestaEnvido(TipoApuesta.REAL_ENVIDO);

        assertThrows(IllegalStateException.class, () -> {
            real.subirApuesta(TipoApuesta.REAL_ENVIDO);
        });
    }

    @Test
    void testNoSePuedeRepetirFaltaEnvido() {
        ApuestaEnvido falta = new ApuestaEnvido(TipoApuesta.FALTA_ENVIDO);

        assertThrows(IllegalStateException.class, () -> {
            falta.subirApuesta(TipoApuesta.FALTA_ENVIDO);
        });
    }

    @Test
    void testDespuesDeFaltaEnvidoNoSePuedeSubirMas() {
        ApuestaEnvido falta = new ApuestaEnvido(TipoApuesta.FALTA_ENVIDO);

        assertThrows(IllegalStateException.class, () -> falta.subirApuesta(TipoApuesta.ENVIDO));
        assertThrows(IllegalStateException.class, () -> falta.subirApuesta(TipoApuesta.REAL_ENVIDO));
    }

    @Test
    void testSubirAFaltaEnvidoDesdeEnvidoSeLlevaLosPuntosPreviosSiNoQuieren() {
        ApuestaEnvido apuesta = new ApuestaEnvido(TipoApuesta.ENVIDO)
                .subirApuesta(TipoApuesta.FALTA_ENVIDO);

        // El valor del Falta Envido querido es provisorio (15) hasta definir el tema de los puntos
        assertEquals(15, apuesta.getPuntosQuerido());
        // Si no quieren, se llevan lo que valía el Envido anterior
        assertEquals(2, apuesta.getPuntosNoQuerido());
    }
}
