package ar.edu.unlu.poo.vista;

import ar.edu.unlu.poo.controlador.Controlador;

/**
 * Lo que el Controlador le puede pedir a cualquier pantalla.
 * La VistaConsola la implementa ahora; más adelante una vista gráfica (JavaFX)
 * va a implementar esta misma interfaz, y ni el modelo ni el controlador cambian.
 *
 * La vista recibe datos simples (textos y números) para no depender del modelo.
 */
public interface IVista {

    void setControlador(Controlador controlador);

    /** Arranca la vista: pide los datos de la partida y empieza a jugar. */
    void iniciar();

    void mostrarInicioRonda(String jugadorMano);

    void mostrarMarcador(String equipoA, int puntosA, String equipoB, int puntosB);

    void mostrarCartaJugada(String jugador, String carta);

    void mostrarGanadorBaza(String jugador);

    void mostrarBazaParda();

    void mostrarCanto(String jugador, String canto);

    void mostrarRespuesta(String jugador, String respuesta);

    void mostrarResultadoEnvido(String equipo, int tanto, int puntos);

    void mostrarPuntosNoQuerido(String equipo, int puntos);

    void mostrarJugadorAlMazo(String jugador);

    void mostrarGanadorRonda(String equipo, int puntos);

    void mostrarGanadorPartida(String equipo);

    void mostrarError(String mensaje);
}
