package ar.edu.unlu.poo.truco;

public enum TipoApuesta {
    ENVIDO(1),
    REAL_ENVIDO(2),
    FALTA_ENVIDO(3);

    private final int nivel;

    TipoApuesta(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }
}