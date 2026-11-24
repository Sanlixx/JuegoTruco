package ar.edu.unlu.poo.truco;

public class CartaJugada {
    private final Jugador jugador;
    private final Carta carta;

    public CartaJugada(Jugador jugador, Carta carta) {
        this.jugador = jugador;
        this.carta = carta;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Carta getCarta() {
        return carta;
    }
}