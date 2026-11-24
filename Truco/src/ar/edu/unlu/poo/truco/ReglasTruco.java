package ar.edu.unlu.poo.truco;

public class ReglasTruco {

    public int obtenerFuerza(Carta carta) {
        int numero = carta.getNumero();
        Palo palo = carta.getPalo();

        // Cartas especiales (Las 4 matas)
        if (numero == 1 && palo == Palo.ESPADA) return 14;
        if (numero == 1 && palo == Palo.BASTO) return 13;
        if (numero == 7 && palo == Palo.ESPADA) return 12;
        if (numero == 7 && palo == Palo.ORO) return 11;

        // Cartas generales
        if (numero == 3) return 10;
        if (numero == 2) return 9;
        if (numero == 1) return 8; // 1 falsos (Copa y Oro)
        if (numero == 12) return 7;
        if (numero == 11) return 6;
        if (numero == 10) return 5;
        if (numero == 7) return 4; // 7 falsos (Copa y Basto)
        if (numero == 6) return 3;
        if (numero == 5) return 2;
        if (numero == 4) return 1;

        return 0; // Fallback por seguridad
    }
}