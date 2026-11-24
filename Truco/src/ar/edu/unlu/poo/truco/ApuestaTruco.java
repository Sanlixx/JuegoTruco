package ar.edu.unlu.poo.truco;

public class ApuestaTruco {
    private EstadoTruco estadoActual;

    public ApuestaTruco() {
        // Toda mano arranca valiendo 1 punto
        this.estadoActual = EstadoTruco.SIN_CANTO;
    }

    public void cantarTruco() {
        if (estadoActual != EstadoTruco.SIN_CANTO) {
            throw new IllegalStateException("Solo se puede cantar Truco si no hay cantos previos.");
        }
        estadoActual = EstadoTruco.TRUCO;
    }

    public void cantarRetruco() {
        if (estadoActual != EstadoTruco.TRUCO) {
            throw new IllegalStateException("Solo se puede cantar Retruco si se cantó Truco previamente.");
        }
        estadoActual = EstadoTruco.RETRUCO;
    }

    public void cantarValeCuatro() {
        if (estadoActual != EstadoTruco.RETRUCO) {
            throw new IllegalStateException("Solo se puede cantar Vale Cuatro si se cantó Retruco previamente.");
        }
        estadoActual = EstadoTruco.VALE_CUATRO;
    }

    public int getPuntosQuerido() {
        return estadoActual.getPuntos();
    }

    public int getPuntosNoQuerido() {
        if (estadoActual == EstadoTruco.SIN_CANTO) {
            return 1; // Fallback por seguridad
        }
        // Si no quieren, el equipo que cantó se lleva los puntos del nivel anterior
        return estadoActual.getPuntos() - 1;
    }

    public EstadoTruco getEstado() {
        return estadoActual;
    }
}