package ar.edu.unlu.poo.truco;

public enum EstadoTruco {
    SIN_CANTO(1),
    TRUCO(2),
    RETRUCO(3),
    VALE_CUATRO(4);

    private final int puntos;

    EstadoTruco(int puntos) {
        this.puntos = puntos;
    }

    public int getPuntos() {
        return puntos;
    }
}