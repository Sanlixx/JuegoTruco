package ar.edu.unlu.poo.observer;

/**
 * Todo lo que puede pasar en el juego y que la pantalla tiene que mostrar.
 */
public enum Evento {
    INICIO_RONDA,       // se repartieron cartas nuevas
    CARTA_JUGADA,       // un jugador tiró una carta
    CAMBIO_TURNO,       // ahora le toca a otro jugador
    BAZA_TERMINADA,     // se jugaron las 4 cartas de una baza

    TRUCO_CANTADO,      // alguien cantó Truco, Retruco o Vale Cuatro
    TRUCO_QUERIDO,
    TRUCO_NO_QUERIDO,   // la ronda termina en el acto

    ENVIDO_CANTADO,     // alguien cantó Envido, Real Envido o Falta Envido
    ENVIDO_QUERIDO,     // se compararon los puntos y se sumaron
    ENVIDO_NO_QUERIDO,

    JUGADOR_AL_MAZO,    // un jugador se fue al mazo
    RONDA_TERMINADA,    // un equipo ganó la ronda y se sumaron los puntos
    PARTIDA_TERMINADA   // un equipo llegó a los puntos necesarios
}
