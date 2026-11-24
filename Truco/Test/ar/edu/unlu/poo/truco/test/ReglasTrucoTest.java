package ar.edu.unlu.poo.truco.test;

import ar.edu.unlu.poo.truco.Carta;
import ar.edu.unlu.poo.truco.Palo;
import ar.edu.unlu.poo.truco.ReglasTruco;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReglasTrucoTest {

    @Test
    void testAnchoDeEspadaEsLaCartaMasFuerte() {
        ReglasTruco reglas = new ReglasTruco();
        Carta anchoEspada = new Carta(1, Palo.ESPADA);
        Carta anchoBasto = new Carta(1, Palo.BASTO);

        // Comparamos que la fuerza de la espada sea mayor
        assertTrue(reglas.obtenerFuerza(anchoEspada) > reglas.obtenerFuerza(anchoBasto));
    }

    @Test
    void testLosTresValenLoMismoYLeGananALosDos() {
        ReglasTruco reglas = new ReglasTruco();
        Carta tresDeCopa = new Carta(3, Palo.COPA);
        Carta tresDeOro = new Carta(3, Palo.ORO);
        Carta dosDeBasto = new Carta(2, Palo.BASTO);

        assertEquals(reglas.obtenerFuerza(tresDeCopa), reglas.obtenerFuerza(tresDeOro));
        assertTrue(reglas.obtenerFuerza(tresDeCopa) > reglas.obtenerFuerza(dosDeBasto));
    }

    @Test
    void testLosCuatroSonLasCartasMasDebiles() {
        ReglasTruco reglas = new ReglasTruco();
        Carta cuatroDeEspada = new Carta(4, Palo.ESPADA);
        Carta cincoDeBasto = new Carta(5, Palo.BASTO);

        assertTrue(reglas.obtenerFuerza(cuatroDeEspada) < reglas.obtenerFuerza(cincoDeBasto));
    }
}