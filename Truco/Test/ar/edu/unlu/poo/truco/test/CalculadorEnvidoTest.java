package ar.edu.unlu.poo.truco.test;

import ar.edu.unlu.poo.truco.CalculadorEnvido;
import ar.edu.unlu.poo.truco.Carta;
import ar.edu.unlu.poo.truco.Palo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculadorEnvidoTest {

    private CalculadorEnvido calculador;

    @BeforeEach
    void setUp() {
        calculador = new CalculadorEnvido();
    }

    @Test
    void testDosCartasMismoPaloSuman20MasSusValores() {
        List<Carta> mano = Arrays.asList(
                new Carta(7, Palo.ESPADA),
                new Carta(5, Palo.ESPADA),
                new Carta(4, Palo.BASTO)
        );
        // 20 + 7 + 5 = 32
        assertEquals(32, calculador.calcular(mano));
    }

    @Test
    void testTresCartasDistintoPaloDevuelveLaMasAlta() {
        List<Carta> mano = Arrays.asList(
                new Carta(7, Palo.ESPADA),
                new Carta(6, Palo.BASTO),
                new Carta(5, Palo.ORO)
        );
        // Al ser palos distintos, gana la carta de mayor valor numérico
        assertEquals(7, calculador.calcular(mano));
    }

    @Test
    void testLasFigurasValenCeroParaElEnvido() {
        List<Carta> mano = Arrays.asList(
                new Carta(10, Palo.COPA),
                new Carta(11, Palo.COPA),
                new Carta(2, Palo.ESPADA)
        );
        // 20 + 0 (del 10) + 0 (del 11) = 20
        assertEquals(20, calculador.calcular(mano));
    }

    @Test
    void testTresCartasMismoPaloSumaLasDosMasAltas() {
        List<Carta> mano = Arrays.asList(
                new Carta(7, Palo.ORO),
                new Carta(6, Palo.ORO),
                new Carta(2, Palo.ORO)
        );
        // 20 + 7 + 6 = 33 (se ignora el 2)
        assertEquals(33, calculador.calcular(mano));
    }
}