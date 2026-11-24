package ar.edu.unlu.poo.truco;

public class Carta {
    private int numero;
    private Palo palo;

    public Carta(int numero, Palo palo) {
        if (!esNumeroValido(numero)) {
            throw new IllegalArgumentException("Número de carta inválido para el Truco: " + numero);
        }
        this.numero = numero;
        this.palo = palo;
    }

    public int getNumero() {
        return numero;
    }

    public Palo getPalo() {
        return palo;
    }

    private boolean esNumeroValido(int numero) {
        return (numero >= 1 && numero <= 7) || (numero >= 10 && numero <= 12);
    }

    @Override
    public String toString() {
        return numero + " de " + palo;
    }
}