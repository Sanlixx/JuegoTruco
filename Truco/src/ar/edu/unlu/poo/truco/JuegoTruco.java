package ar.edu.unlu.poo.truco;

import ar.edu.unlu.poo.observer.Evento;
import ar.edu.unlu.poo.observer.IObservable;
import ar.edu.unlu.poo.observer.IObservador;

import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada al modelo. Es la clase con la que habla el Controlador.
 *
 * Responsabilidades:
 *  - Llevar el flujo de la partida: de quién es el turno, qué baza se está jugando,
 *    cuándo termina una ronda y cuándo termina la partida.
 *  - Validar que cada acción (jugar carta, cantar, querer...) sea válida en ese momento.
 *  - Avisar a los observadores cada vez que algo cambia (patrón Observer).
 *
 * Las reglas "finas" las sigue resolviendo cada clase que ya existía:
 * Baza decide quién gana la baza, RondaTruco quién gana la ronda, Partida reparte y
 * resuelve el envido, ApuestaTruco / ApuestaEnvido llevan la escalera de cantos.
 */
public class JuegoTruco implements IObservable {
    public static final int CARTAS_POR_BAZA = 4;
    public static final int BAZAS_POR_RONDA = 3;
    // Jugando a 30: de 0 a 14 son las "malas" y de 15 en adelante las "buenas"
    public static final int PUNTOS_MALAS = 15;

    private final List<IObservador> observadores = new ArrayList<>();

    private final Equipo equipoA;
    private final Equipo equipoB;   
    private final Partida partida;
    private final int puntosParaGanar;

    // --- Estado de la ronda actual ---
    private RondaTruco rondaActual;
    private Baza bazaActual;
    private int numeroBaza;            // 1, 2 o 3
    private int indiceTurno;           // posición en el orden de la mesa del jugador que tiene que tirar

    // --- Cantos ---
    private CantoPendiente cantoPendiente = CantoPendiente.NINGUNO;
    private Jugador jugadorQueCanto;   // quién hizo el último canto que espera respuesta
    private Equipo equipoQueResponde;  // el equipo rival: puede responder cualquiera de sus jugadores
    private Jugador jugadorQueRespondio; // quién dijo el último quiero / no quiero

    private ApuestaTruco apuestaTruco;
    private Equipo equipoQuePuedeSubirTruco; // null = cualquiera (todavía nadie cantó)

    private ApuestaEnvido apuestaEnvido;
    private boolean envidoYaJugado;

    // --- Resultados, para que la vista los pueda consultar después de cada evento ---
    private CartaJugada ultimaCartaJugada;
    private Jugador ganadorUltimaBaza;        // null si fue parda
    private Equipo ganadorUltimaRonda;
    private int puntosUltimaRonda;
    private Equipo ganadorUltimoEnvido;
    private int puntosUltimoEnvido;
    private Jugador ultimoJugadorAlMazo;
    private Equipo ganadorPartida;            // null mientras se sigue jugando

    public JuegoTruco(Equipo equipoA, Equipo equipoB, int puntosParaGanar) {
        if (puntosParaGanar != 15 && puntosParaGanar != 30) {
            throw new IllegalArgumentException("Se juega a 15 o a 30 puntos.");
        }
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.puntosParaGanar = puntosParaGanar;
        this.partida = new Partida(equipoA, equipoB);
    }

    // =====================================================================
    // Acciones (las llama el Controlador)
    // =====================================================================

    public void iniciar() {
        iniciarRonda();
    }

    /** El jugador que tiene el turno tira la carta que está en la posición indicada de su mano. */
    public void jugarCarta(int indiceCarta) {
        validarPartidaEnCurso();
        if (hayCantoPendiente()) {
            throw new IllegalStateException("Hay un canto sin responder.");
        }

        Jugador jugador = getJugadorEnTurno();
        Carta carta = jugador.jugarCarta(indiceCarta);
        bazaActual.jugarCarta(jugador, carta);
        ultimaCartaJugada = new CartaJugada(jugador, carta);
        notificarObservadores(Evento.CARTA_JUGADA);

        if (bazaActual.getJugadas().size() < CARTAS_POR_BAZA) {
            indiceTurno = siguienteIndice(indiceTurno);
            notificarObservadores(Evento.CAMBIO_TURNO);
        } else {
            terminarBaza();
        }
    }

    /**
     * Canta Truco, Retruco o Vale Cuatro según corresponda.
     * Si no hay canto pendiente, canta el jugador en turno. Si hay un truco pendiente,
     * cualquier jugador del equipo que responde puede subir ("quiero retruco").
     */
    public void cantarTruco(Jugador cantor) {
        validarPartidaEnCurso();
        validarQuePuedeActuar(cantor);
        if (cantoPendiente == CantoPendiente.ENVIDO) {
            throw new IllegalStateException("Primero hay que responder el envido.");
        }
        if (apuestaTruco.getEstado() == EstadoTruco.VALE_CUATRO) {
            throw new IllegalStateException("Ya se cantó Vale Cuatro, no se puede subir más.");
        }

        if (cantoPendiente == CantoPendiente.NINGUNO
                && equipoQuePuedeSubirTruco != null
                && equipoQuePuedeSubirTruco != equipoDe(cantor)) {
            throw new IllegalStateException("Solo puede subir la apuesta el equipo que dijo quiero.");
        }

        switch (apuestaTruco.getEstado()) {
            case SIN_CANTO:
                apuestaTruco.cantarTruco();
                break;
            case TRUCO:
                apuestaTruco.cantarRetruco();
                break;
            case RETRUCO:
                apuestaTruco.cantarValeCuatro();
                break;
            default:
                throw new IllegalStateException("Estado de truco inesperado.");
        }

        esperarRespuesta(CantoPendiente.TRUCO, cantor);
        notificarObservadores(Evento.TRUCO_CANTADO);
    }

    /** Canta Envido, Real Envido o Falta Envido, o sube un envido que todavía no se respondió. */
    public void cantarEnvido(Jugador cantor, TipoApuesta tipo) {
        validarPartidaEnCurso();
        validarQuePuedeActuar(cantor);
        if (cantoPendiente == CantoPendiente.TRUCO) {
            throw new IllegalStateException("Primero hay que responder el truco.");
        }

        if (cantoPendiente == CantoPendiente.ENVIDO) {
            // Respuesta subiendo la apuesta: "Envido, Real Envido", etc.
            apuestaEnvido = apuestaEnvido.subirApuesta(tipo);
        } else {
            if (envidoYaJugado) {
                throw new IllegalStateException("El envido ya se jugó en esta ronda.");
            }
            if (numeroBaza != 1) {
                throw new IllegalStateException("El envido solo se puede cantar en la primera baza.");
            }
            if (apuestaTruco.getEstado() != EstadoTruco.SIN_CANTO) {
                throw new IllegalStateException("No se puede cantar envido después del truco.");
            }
            if (!esPie(cantor)) {
                throw new IllegalStateException("El envido lo canta el pie: el último de cada equipo en jugar.");
            }
            apuestaEnvido = new ApuestaEnvido(tipo);
        }

        esperarRespuesta(CantoPendiente.ENVIDO, cantor);
        notificarObservadores(Evento.ENVIDO_CANTADO);
    }

    /** Responde "quiero" al canto pendiente. Puede responder cualquier jugador del equipo rival. */
    public void quiero(Jugador quienResponde) {
        validarPartidaEnCurso();
        validarQueHayCantoPendiente();
        validarQuePuedeActuar(quienResponde);
        jugadorQueRespondio = quienResponde;
        switch (cantoPendiente) {
            case TRUCO:
                // Quien acepta pasa a ser el único que puede volver a subir la apuesta.
                equipoQuePuedeSubirTruco = equipoDe(quienResponde);
                limpiarCantoPendiente();
                notificarObservadores(Evento.TRUCO_QUERIDO);
                break;
            case ENVIDO:
                ganadorUltimoEnvido = partida.resolverEnvido();
                puntosUltimoEnvido = puntosEnvidoQuerido();
                ganadorUltimoEnvido.sumarPuntos(puntosUltimoEnvido);
                envidoYaJugado = true;
                limpiarCantoPendiente();
                notificarObservadores(Evento.ENVIDO_QUERIDO);
                verificarFinDePartida();
                break;
            default:
                throw new IllegalStateException("No hay ningún canto para responder.");
        }
    }

    /** Responde "no quiero" al canto pendiente. Puede responder cualquier jugador del equipo rival. */
    public void noQuiero(Jugador quienResponde) {
        validarPartidaEnCurso();
        validarQueHayCantoPendiente();
        validarQuePuedeActuar(quienResponde);
        jugadorQueRespondio = quienResponde;
        switch (cantoPendiente) {
            case TRUCO:
                Equipo ganador = equipoDe(jugadorQueCanto);
                int puntos = apuestaTruco.getPuntosNoQuerido();
                limpiarCantoPendiente();
                notificarObservadores(Evento.TRUCO_NO_QUERIDO);
                terminarRonda(ganador, puntos);
                break;
            case ENVIDO:
                ganadorUltimoEnvido = equipoDe(jugadorQueCanto);
                puntosUltimoEnvido = apuestaEnvido.getPuntosNoQuerido();
                ganadorUltimoEnvido.sumarPuntos(puntosUltimoEnvido);
                envidoYaJugado = true;
                limpiarCantoPendiente();
                notificarObservadores(Evento.ENVIDO_NO_QUERIDO);
                verificarFinDePartida();
                break;
            default:
                throw new IllegalStateException("No hay ningún canto para responder.");
        }
    }

    /** El jugador en turno abandona la ronda: el equipo rival se lleva lo que valía el truco. */
    public void irseAlMazo() {
        validarPartidaEnCurso();
        if (hayCantoPendiente()) {
            throw new IllegalStateException("Hay un canto sin responder: respondé con quiero o no quiero.");
        }
        ultimoJugadorAlMazo = getJugadorEnTurno();
        notificarObservadores(Evento.JUGADOR_AL_MAZO);
        terminarRonda(rivalDe(equipoDe(ultimoJugadorAlMazo)), apuestaTruco.getPuntosQuerido());
    }

    // =====================================================================
    // Lógica interna
    // =====================================================================

    private void iniciarRonda() {
        partida.repartirCartas();
        rondaActual = new RondaTruco(equipoA, equipoB, partida.getJugadorMano());
        bazaActual = new Baza();
        numeroBaza = 1;
        indiceTurno = partida.getOrdenMesa().indexOf(partida.getJugadorMano());

        apuestaTruco = new ApuestaTruco();
        equipoQuePuedeSubirTruco = null;
        apuestaEnvido = null;
        envidoYaJugado = false;
        limpiarCantoPendiente();

        ultimaCartaJugada = null;
        ganadorUltimaBaza = null;
        notificarObservadores(Evento.INICIO_RONDA);
    }

    private void terminarBaza() {
        rondaActual.registrarBaza(bazaActual);
        ganadorUltimaBaza = bazaActual.determinarJugadorGanador(equipoA, equipoB);
        notificarObservadores(Evento.BAZA_TERMINADA);

        Equipo ganadorRonda = rondaActual.determinarGanadorRonda();
        if (ganadorRonda != null) {
            terminarRonda(ganadorRonda, apuestaTruco.getPuntosQuerido());
            return;
        }

        // Sigue la ronda: arranca la baza siguiente el que ganó esta.
        // Si fue parda, arranca el jugador mano.
        Jugador quienArranca;
        if (ganadorUltimaBaza != null) {
            quienArranca = ganadorUltimaBaza;
        } else {
            quienArranca = partida.getJugadorMano();
        }
        indiceTurno = partida.getOrdenMesa().indexOf(quienArranca);
        bazaActual = new Baza();
        numeroBaza++;
        notificarObservadores(Evento.CAMBIO_TURNO);
    }

    private void terminarRonda(Equipo ganador, int puntos) {
        ganadorUltimaRonda = ganador;
        puntosUltimaRonda = puntos;
        ganador.sumarPuntos(puntos);
        notificarObservadores(Evento.RONDA_TERMINADA);

        if (!verificarFinDePartida()) {
            partida.pasarMano();
            iniciarRonda();
        }
    }

    /** Devuelve true si algún equipo llegó a los puntos necesarios (y avisa). */
    private boolean verificarFinDePartida() {
        if (equipoA.getPuntos() >= puntosParaGanar) {
            ganadorPartida = equipoA;
        } else if (equipoB.getPuntos() >= puntosParaGanar) {
            ganadorPartida = equipoB;
        }
        if (ganadorPartida != null) {
            limpiarCantoPendiente();
            notificarObservadores(Evento.PARTIDA_TERMINADA);
            return true;
        }
        return false;
    }

    private int puntosEnvidoQuerido() {
        if (apuestaEnvido.getTipo() == TipoApuesta.FALTA_ENVIDO) {
            return calcularFaltaEnvido();
        }
        return apuestaEnvido.getPuntosQuerido();
    }

    /**
     * Cuánto vale la Falta Envido querida. Se mira siempre al equipo que va adelante:
     *  - Jugando a 30, si está en las malas (menos de 15): lo que le falta para llegar a 15.
     *  - Jugando a 30 en las buenas, o jugando a 15: lo que le falta para ganar la partida.
     * Ejemplos a 30: va adelante con 10 -> vale 5 | va adelante con 22 -> vale 8.
     */
    private int calcularFaltaEnvido() {
        int puntosDelQueVaAdelante = Math.max(equipoA.getPuntos(), equipoB.getPuntos());
        if (puntosParaGanar == 30 && puntosDelQueVaAdelante < PUNTOS_MALAS) {
            return PUNTOS_MALAS - puntosDelQueVaAdelante;
        }
        return puntosParaGanar - puntosDelQueVaAdelante;
    }

    private void esperarRespuesta(CantoPendiente canto, Jugador cantor) {
        cantoPendiente = canto;
        jugadorQueCanto = cantor;
        equipoQueResponde = rivalDe(equipoDe(cantor));
    }

    private void limpiarCantoPendiente() {
        cantoPendiente = CantoPendiente.NINGUNO;
        jugadorQueCanto = null;
        equipoQueResponde = null;
    }

    /**
     * Controla que el jugador pueda hacer algo ahora:
     *  - si hay un canto esperando respuesta, tiene que ser del equipo que responde;
     *  - si no, tiene que ser el jugador que tiene el turno.
     */
    private void validarQuePuedeActuar(Jugador jugador) {
        if (hayCantoPendiente()) {
            if (equipoDe(jugador) != equipoQueResponde) {
                throw new IllegalStateException("Tiene que responder un jugador de " + equipoQueResponde.getNombre() + ".");
            }
        } else {
            if (jugador != getJugadorEnTurno()) {
                throw new IllegalStateException("No es el turno de " + jugador.getNombre() + ".");
            }
        }
    }

    private void validarQueHayCantoPendiente() {
        if (!hayCantoPendiente()) {
            throw new IllegalStateException("No hay ningún canto para responder.");
        }
    }

    private void validarPartidaEnCurso() {
        if (isPartidaTerminada()) {
            throw new IllegalStateException("La partida ya terminó.");
        }
        if (rondaActual == null) {
            throw new IllegalStateException("La partida todavía no empezó.");
        }
    }

    // --- Ayudas para moverse por la mesa ---

    private int siguienteIndice(int indice) {
        return (indice + 1) % partida.getOrdenMesa().size();
    }

    private Equipo equipoDe(Jugador jugador) {
        if (equipoA.getJugadores().contains(jugador)) {
            return equipoA;
        }
        return equipoB;
    }

    private Equipo rivalDe(Equipo equipo) {
        if (equipo == equipoA) {
            return equipoB;
        }
        return equipoA;
    }

    // =====================================================================
    // Consultas (la vista pregunta lo que necesita mostrar)
    // =====================================================================

    public Jugador getJugadorEnTurno() {
        return partida.getOrdenMesa().get(indiceTurno);
    }

    /** El equipo que tiene que responder el canto pendiente, o null si no hay canto. */
    public Equipo getEquipoQueResponde() {
        return equipoQueResponde;
    }

    /** Los jugadores que pueden responder el canto pendiente (lista vacía si no hay canto). */
    public List<Jugador> getJugadoresQuePuedenResponder() {
        if (equipoQueResponde == null) {
            return new ArrayList<>();
        }
        return equipoQueResponde.getJugadores();
    }

    public boolean hayCantoPendiente() {
        return cantoPendiente != CantoPendiente.NINGUNO;
    }

    public CantoPendiente getCantoPendiente() {
        return cantoPendiente;
    }

    public Jugador getJugadorQueCanto() {
        return jugadorQueCanto;
    }

    public Jugador getJugadorQueRespondio() {
        return jugadorQueRespondio;
    }

    /** Si el jugador que tiene que actuar puede cantar (o subir) el truco en este momento. */
    public boolean sePuedeCantarTruco() {
        if (cantoPendiente == CantoPendiente.ENVIDO) {
            return false;
        }
        if (apuestaTruco.getEstado() == EstadoTruco.VALE_CUATRO) {
            return false;
        }
        if (cantoPendiente == CantoPendiente.TRUCO) {
            return true; // el que responde puede subir: "quiero retruco"
        }
        if (equipoQuePuedeSubirTruco == null) {
            return true; // todavía nadie cantó truco en la ronda
        }
        return equipoQuePuedeSubirTruco == equipoDe(getJugadorEnTurno());
    }

    public EstadoTruco getEstadoTruco() {
        return apuestaTruco.getEstado();
    }

    /** El último envido cantado en la ronda, o null si no se cantó. */
    public TipoApuesta getUltimoEnvidoCantado() {
        if (apuestaEnvido == null) {
            return null;
        }
        return apuestaEnvido.getTipo();
    }

    /** Si el jugador en turno puede empezar un envido (no cubre subir un envido que ya se cantó). */
    public boolean sePuedeCantarEnvido() {
        return !envidoYaJugado
                && numeroBaza == 1
                && apuestaTruco.getEstado() == EstadoTruco.SIN_CANTO
                && esPie(getJugadorEnTurno());
    }

    /**
     * El "pie" es el último jugador de su equipo en jugar, contando desde la mano.
     * Con la mesa Ana(1) - Caro(2) - Beto(1) - Dani(2) y Ana de mano:
     * el pie del equipo 1 es Beto y el del equipo 2 es Dani.
     */
    public boolean esPie(Jugador jugador) {
        List<Jugador> mesa = partida.getOrdenMesa();
        int indiceMano = mesa.indexOf(partida.getJugadorMano());
        Jugador ultimoDeSuEquipo = null;
        for (int i = 0; i < mesa.size(); i++) {
            Jugador candidato = mesa.get((indiceMano + i) % mesa.size());
            if (equipoDe(candidato) == equipoDe(jugador)) {
                ultimoDeSuEquipo = candidato; // se va pisando: al final queda el último
            }
        }
        return ultimoDeSuEquipo == jugador;
    }

    public Equipo getEquipoA() {
        return equipoA;
    }

    public Equipo getEquipoB() {
        return equipoB;
    }

    public Equipo getEquipoDe(Jugador jugador) {
        return equipoDe(jugador);
    }

    public List<Jugador> getOrdenMesa() {
        return partida.getOrdenMesa();
    }

    public Jugador getJugadorMano() {
        return partida.getJugadorMano();
    }

    public int getNumeroBaza() {
        return numeroBaza;
    }

    public List<CartaJugada> getCartasDeLaBazaActual() {
        return bazaActual.getJugadas();
    }

    public CartaJugada getUltimaCartaJugada() {
        return ultimaCartaJugada;
    }

    /** null si la última baza fue parda. */
    public Jugador getGanadorUltimaBaza() {
        return ganadorUltimaBaza;
    }

    public Equipo getGanadorUltimaRonda() {
        return ganadorUltimaRonda;
    }

    public int getPuntosUltimaRonda() {
        return puntosUltimaRonda;
    }

    public Equipo getGanadorUltimoEnvido() {
        return ganadorUltimoEnvido;
    }

    public int getPuntosUltimoEnvido() {
        return puntosUltimoEnvido;
    }

    public Jugador getUltimoJugadorAlMazo() {
        return ultimoJugadorAlMazo;
    }

    public int getPuntosParaGanar() {
        return puntosParaGanar;
    }

    public boolean isPartidaTerminada() {
        return ganadorPartida != null;
    }

    public Equipo getGanadorPartida() {
        return ganadorPartida;
    }

    // =====================================================================
    // Observer
    // =====================================================================

    @Override
    public void agregarObservador(IObservador observador) {
        observadores.add(observador);
    }

    @Override
    public void quitarObservador(IObservador observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(Evento evento) {
        for (IObservador observador : observadores) {
            observador.actualizar(evento, this);
        }
    }
}
