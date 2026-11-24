package ar.edu.unlu.poo.controlador;

import ar.edu.unlu.poo.observer.Evento;
import ar.edu.unlu.poo.observer.IObservable;
import ar.edu.unlu.poo.observer.IObservador;
import ar.edu.unlu.poo.truco.CantoPendiente;
import ar.edu.unlu.poo.truco.Carta;
import ar.edu.unlu.poo.truco.CartaJugada;
import ar.edu.unlu.poo.truco.Equipo;
import ar.edu.unlu.poo.truco.EstadoTruco;
import ar.edu.unlu.poo.truco.JuegoTruco;
import ar.edu.unlu.poo.truco.Jugador;
import ar.edu.unlu.poo.truco.TipoApuesta;
import ar.edu.unlu.poo.vista.IVista;

import java.util.ArrayList;
import java.util.List;

/**
 * Intermediario entre la vista y el modelo (la "C" de MVC).
 *
 * Responsabilidades:
 *  - Recibir lo que el usuario hace en la vista y pedírselo al modelo.
 *  - Observar al modelo: cuando el modelo avisa un Evento, le dice a la vista qué mostrar.
 *  - Traducir los objetos del modelo a datos simples (textos, números) para la vista.
 */
public class Controlador implements IObservador {
    private final IVista vista;
    private JuegoTruco modelo;
    // Cuando hay un canto, cualquiera de los dos rivales puede responder.
    // Acá se guarda cuál de los dos eligió responder en la vista.
    private Jugador respondedorElegido;

    public Controlador(IVista vista) {
        this.vista = vista;
        this.vista.setControlador(this);
    }

    // =====================================================================
    // La vista pide hacer algo -> el controlador se lo pide al modelo
    // =====================================================================

    /**
     * nombres: los 4 jugadores, en este orden: equipo 1 (dos jugadores), equipo 2 (dos jugadores).
     */
    public void iniciarPartida(List<String> nombres, int puntosParaGanar) {
        Equipo equipo1 = new Equipo("Equipo 1", new Jugador(nombres.get(0)), new Jugador(nombres.get(1)));
        Equipo equipo2 = new Equipo("Equipo 2", new Jugador(nombres.get(2)), new Jugador(nombres.get(3)));
        modelo = new JuegoTruco(equipo1, equipo2, puntosParaGanar);
        modelo.agregarObservador(this);
        modelo.iniciar();
    }

    public void jugarCarta(int indiceCarta) {
        try {
            modelo.jugarCarta(indiceCarta);
        } catch (IllegalStateException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    /**
     * Cuando hay un canto pendiente, la vista primero elige cuál de los rivales responde.
     * numero: 1 o 2, según la lista que devuelve getRivalesQuePuedenResponder().
     */
    public boolean elegirRespondedor(int numero) {
        List<Jugador> posibles = modelo.getJugadoresQuePuedenResponder();
        if (numero < 1 || numero > posibles.size()) {
            vista.mostrarError("Ese jugador no puede responder.");
            return false;
        }
        respondedorElegido = posibles.get(numero - 1);
        return true;
    }

    public void cantarTruco() {
        try {
            modelo.cantarTruco(jugadorQueActua());
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void cantarEnvido() {
        cantarEnvido(TipoApuesta.ENVIDO);
    }

    public void cantarRealEnvido() {
        cantarEnvido(TipoApuesta.REAL_ENVIDO);
    }

    public void cantarFaltaEnvido() {
        cantarEnvido(TipoApuesta.FALTA_ENVIDO);
    }

    private void cantarEnvido(TipoApuesta tipo) {
        try {
            modelo.cantarEnvido(jugadorQueActua(), tipo);
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void quiero() {
        try {
            modelo.quiero(jugadorQueActua());
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void noQuiero() {
        try {
            modelo.noQuiero(jugadorQueActua());
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void irseAlMazo() {
        try {
            modelo.irseAlMazo();
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // =====================================================================
    // La vista pregunta algo -> el controlador consulta al modelo
    // =====================================================================

    public boolean isPartidaTerminada() {
        return modelo.isPartidaTerminada();
    }

    public String getJugadorQueDebeActuar() {
        return jugadorQueActua().getNombre();
    }

    public List<String> getCartasDelJugadorQueDebeActuar() {
        List<String> textos = new ArrayList<>();
        for (Carta carta : jugadorQueActua().getCartasEnMano()) {
            textos.add(carta.toString());
        }
        return textos;
    }

    public String getCompanero() {
        return companeroDelQueActua().getNombre();
    }

    /** Las cartas que le quedan en la mano al compañero del jugador que está actuando. */
    public List<String> getCartasDelCompanero() {
        List<String> textos = new ArrayList<>();
        for (Carta carta : companeroDelQueActua().getCartasEnMano()) {
            textos.add(carta.toString());
        }
        return textos;
    }

    public List<String> getCartasEnLaMesa() {
        List<String> textos = new ArrayList<>();
        for (CartaJugada jugada : modelo.getCartasDeLaBazaActual()) {
            textos.add(jugada.getJugador().getNombre() + ": " + jugada.getCarta());
        }
        return textos;
    }

    public int getNumeroBaza() {
        return modelo.getNumeroBaza();
    }

    public boolean hayCantoPendiente() {
        return modelo.hayCantoPendiente();
    }

    public boolean hayTrucoPendiente() {
        return modelo.getCantoPendiente() == CantoPendiente.TRUCO;
    }

    public boolean hayEnvidoPendiente() {
        return modelo.getCantoPendiente() == CantoPendiente.ENVIDO;
    }

    public String getJugadorQueCanto() {
        return modelo.getJugadorQueCanto().getNombre();
    }

    public String getEquipoQueResponde() {
        return modelo.getEquipoQueResponde().getNombre();
    }

    /** Los nombres de los rivales que pueden responder el canto pendiente. */
    public List<String> getRivalesQuePuedenResponder() {
        List<String> nombres = new ArrayList<>();
        for (Jugador jugador : modelo.getJugadoresQuePuedenResponder()) {
            nombres.add(jugador.getNombre());
        }
        return nombres;
    }

    /**
     * Las cartas que todavía tiene en la mano uno de los rivales que pueden responder.
     * numero: 1 o 2, en el mismo orden que getRivalesQuePuedenResponder().
     */
    public List<String> getCartasDeRival(int numero) {
        List<String> textos = new ArrayList<>();
        Jugador rival = modelo.getJugadoresQuePuedenResponder().get(numero - 1);
        for (Carta carta : rival.getCartasEnMano()) {
            textos.add(carta.toString());
        }
        return textos;
    }

    /** El texto del canto que espera respuesta, por ejemplo "Truco" o "Real Envido". */
    public String getCantoPendiente() {
        if (hayTrucoPendiente()) {
            return textoTruco(modelo.getEstadoTruco());
        }
        if (hayEnvidoPendiente()) {
            return textoEnvido(modelo.getUltimoEnvidoCantado());
        }
        return "";
    }

    public boolean sePuedeCantarTruco() {
        return modelo.sePuedeCantarTruco();
    }

    /** Qué se cantaría si se canta truco ahora: "Truco", "Retruco" o "Vale Cuatro". */
    public String getProximoCantoTruco() {
        switch (modelo.getEstadoTruco()) {
            case SIN_CANTO:
                return "Truco";
            case TRUCO:
                return "Retruco";
            default:
                return "Vale Cuatro";
        }
    }

    public boolean sePuedeCantarEnvido() {
        // Se puede empezar un envido, o subir uno que está esperando respuesta
        return modelo.sePuedeCantarEnvido() || hayEnvidoPendiente();
    }

    // =====================================================================
    // Observer: el modelo avisa qué pasó -> el controlador actualiza la vista
    // =====================================================================

    @Override
    public void actualizar(Evento evento, IObservable observable) {
        switch (evento) {
            case INICIO_RONDA:
                vista.mostrarInicioRonda(modelo.getJugadorMano().getNombre());
                mostrarMarcador();
                break;

            case CARTA_JUGADA:
                CartaJugada jugada = modelo.getUltimaCartaJugada();
                vista.mostrarCartaJugada(jugada.getJugador().getNombre(), jugada.getCarta().toString());
                break;

            case BAZA_TERMINADA:
                Jugador ganadorBaza = modelo.getGanadorUltimaBaza();
                if (ganadorBaza == null) {
                    vista.mostrarBazaParda();
                } else {
                    vista.mostrarGanadorBaza(ganadorBaza.getNombre());
                }
                break;

            case TRUCO_CANTADO:
                respondedorElegido = null; // hay un canto nuevo: todavía no se eligió quién responde
                vista.mostrarCanto(modelo.getJugadorQueCanto().getNombre(), textoTruco(modelo.getEstadoTruco()));
                break;

            case ENVIDO_CANTADO:
                respondedorElegido = null;
                vista.mostrarCanto(modelo.getJugadorQueCanto().getNombre(), textoEnvido(modelo.getUltimoEnvidoCantado()));
                break;

            case TRUCO_QUERIDO:
                vista.mostrarRespuesta(modelo.getJugadorQueRespondio().getNombre(), "Quiero");
                break;

            case TRUCO_NO_QUERIDO:
                // Los puntos se muestran después, con el evento RONDA_TERMINADA
                vista.mostrarRespuesta(modelo.getJugadorQueRespondio().getNombre(), "No quiero");
                break;

            case ENVIDO_QUERIDO:
                Equipo ganadorEnvido = modelo.getGanadorUltimoEnvido();
                vista.mostrarRespuesta(modelo.getJugadorQueRespondio().getNombre(), "Quiero");
                vista.mostrarResultadoEnvido(ganadorEnvido.getNombre(), ganadorEnvido.getMejorEnvido(),
                        modelo.getPuntosUltimoEnvido());
                mostrarMarcador();
                break;

            case ENVIDO_NO_QUERIDO:
                vista.mostrarRespuesta(modelo.getJugadorQueRespondio().getNombre(), "No quiero");
                vista.mostrarPuntosNoQuerido(modelo.getGanadorUltimoEnvido().getNombre(), modelo.getPuntosUltimoEnvido());
                mostrarMarcador();
                break;

            case JUGADOR_AL_MAZO:
                vista.mostrarJugadorAlMazo(modelo.getUltimoJugadorAlMazo().getNombre());
                break;

            case RONDA_TERMINADA:
                vista.mostrarGanadorRonda(modelo.getGanadorUltimaRonda().getNombre(), modelo.getPuntosUltimaRonda());
                mostrarMarcador();
                break;

            case PARTIDA_TERMINADA:
                vista.mostrarGanadorPartida(modelo.getGanadorPartida().getNombre());
                break;

            default:
                // CAMBIO_TURNO: la consola no necesita hacer nada, el menú del próximo turno
                // se muestra solo. Una vista gráfica sí lo podría usar (por ejemplo, para resaltar al jugador).
                break;
        }
    }

    // =====================================================================
    // Ayudas
    // =====================================================================

    /**
     * Quién está haciendo la acción ahora:
     *  - si hay un canto pendiente, el rival que se eligió para responder;
     *  - si no, el jugador que tiene el turno.
     */
    private Jugador jugadorQueActua() {
        if (modelo.hayCantoPendiente() && respondedorElegido != null) {
            return respondedorElegido;
        }
        return modelo.getJugadorEnTurno();
    }

    private Jugador companeroDelQueActua() {
        Jugador jugador = jugadorQueActua();
        return modelo.getEquipoDe(jugador).getCompaneroDe(jugador);
    }

    private void mostrarMarcador() {
        Equipo a = modelo.getEquipoA();
        Equipo b = modelo.getEquipoB();
        vista.mostrarMarcador(a.getNombre(), a.getPuntos(), b.getNombre(), b.getPuntos());
    }

    private String textoTruco(EstadoTruco estado) {
        switch (estado) {
            case TRUCO:
                return "Truco";
            case RETRUCO:
                return "Retruco";
            case VALE_CUATRO:
                return "Vale Cuatro";
            default:
                return "";
        }
    }

    private String textoEnvido(TipoApuesta tipo) {
        switch (tipo) {
            case ENVIDO:
                return "Envido";
            case REAL_ENVIDO:
                return "Real Envido";
            case FALTA_ENVIDO:
                return "Falta Envido";
            default:
                return "";
        }
    }
}
