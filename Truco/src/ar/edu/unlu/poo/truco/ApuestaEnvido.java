package ar.edu.unlu.poo.truco;

public class ApuestaEnvido {
    private static final int MAX_ENVIDOS_SEGUIDOS = 2;

    private final TipoApuesta tipo;
    private final int puntosAcumulados;
    private final int puntosRechazados;
    // Cantidad de "Envido" simples cantados hasta ahora (se pueden cantar hasta 2).
    private final int cantidadEnvidos;

    // Constructor inicial para el primer canto
    public ApuestaEnvido(TipoApuesta tipo) {
        this.tipo = tipo;
        this.puntosRechazados = 1;
        this.puntosAcumulados = calcularPuntos(tipo, 0);
        if (tipo == TipoApuesta.ENVIDO) {
            this.cantidadEnvidos = 1;
        } else {
            this.cantidadEnvidos = 0;
        }
    }

    // Constructor privado para subir la apuesta
    private ApuestaEnvido(TipoApuesta tipo, int puntosPrevios, int envidosPrevios) {
        this.tipo = tipo;
        this.puntosRechazados = puntosPrevios;
        this.puntosAcumulados = calcularPuntos(tipo, puntosPrevios);
        if (tipo == TipoApuesta.ENVIDO) {
            this.cantidadEnvidos = envidosPrevios + 1;
        } else {
            this.cantidadEnvidos = envidosPrevios;
        }
    }

    public ApuestaEnvido subirApuesta(TipoApuesta nuevaApuesta) {
        if (nuevaApuesta.getNivel() < this.tipo.getNivel()) {
            throw new IllegalStateException("No se puede responder con una apuesta de menor jerarquía.");
        }
        if (nuevaApuesta == this.tipo) {
            // Mismo canto: solo se permite "Envido, Envido" (hasta 2 veces).
            // Real Envido y Falta Envido no se pueden repetir.
            if (this.tipo != TipoApuesta.ENVIDO) {
                throw new IllegalStateException("No se puede repetir " + this.tipo + ".");
            }
            if (this.cantidadEnvidos >= MAX_ENVIDOS_SEGUIDOS) {
                throw new IllegalStateException("Solo se puede cantar Envido hasta " + MAX_ENVIDOS_SEGUIDOS + " veces.");
            }
        }
        return new ApuestaEnvido(nuevaApuesta, this.puntosAcumulados, this.cantidadEnvidos);
    }

    public int getPuntosQuerido() {
        return puntosAcumulados;
    }

    public int getPuntosNoQuerido() {
        return puntosRechazados;
    }

    public TipoApuesta getTipo() {
        return tipo;
    }

    private int calcularPuntos(TipoApuesta tipo, int puntosPrevios) {
        switch (tipo) {
            case ENVIDO:
                return puntosPrevios + 2;
            case REAL_ENVIDO:
                return puntosPrevios + 3;
            case FALTA_ENVIDO:
                // La Falta Envido depende de los puntos de los equipos (malas o buenas),
                // que esta clase no conoce. El valor real lo calcula JuegoTruco
                // en calcularFaltaEnvido(); este 15 no se usa cuando se quiere la falta.
                return 15;
            default:
                return 0;
        }
    }
}
