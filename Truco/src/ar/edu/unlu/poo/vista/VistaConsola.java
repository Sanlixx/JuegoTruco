package ar.edu.unlu.poo.vista;

import ar.edu.unlu.poo.controlador.Controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interfaz de consola del Truco.
 *
 * Responsabilidades:
 *  - Pedir los datos de la partida (nombres y a cuántos puntos se juega).
 *  - En cada turno, mostrar el menú y leer lo que elige el jugador.
 *  - Mostrar en pantalla lo que el Controlador le pide (cartas jugadas, cantos, puntos...).
 *
 * No conoce al modelo: todo lo pide y lo hace a través del Controlador.
 */
public class VistaConsola implements IVista {
    private final Scanner scanner = new Scanner(System.in);
    private Controlador controlador;

    @Override
    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    // =====================================================================
    // Arranque y bucle principal
    // =====================================================================

    @Override
    public void iniciar() {
        System.out.println("==============================");
        System.out.println("            TRUCO");
        System.out.println("==============================");

        List<String> nombres = new ArrayList<>();
        nombres.add(pedirTexto("Equipo 1 - nombre del jugador 1: "));
        nombres.add(pedirTexto("Equipo 1 - nombre del jugador 2: "));
        nombres.add(pedirTexto("Equipo 2 - nombre del jugador 1: "));
        nombres.add(pedirTexto("Equipo 2 - nombre del jugador 2: "));
        int puntos = pedirPuntosParaGanar();

        controlador.iniciarPartida(nombres, puntos);

        while (!controlador.isPartidaTerminada()) {
            if (controlador.hayCantoPendiente()) {
                mostrarMenuRespuesta();
            } else {
                mostrarMenuTurno();
            }
        }
        System.out.println("¡Gracias por jugar!");
    }

    /** Menú normal: el jugador en turno tira una carta, canta o se va al mazo. */
    private void mostrarMenuTurno() {
        System.out.println();
        System.out.println("---- Baza " + controlador.getNumeroBaza() + " | Turno de "
                + controlador.getJugadorQueDebeActuar() + " ----");

        List<String> mesa = controlador.getCartasEnLaMesa();
        if (mesa.isEmpty()) {
            System.out.println("En la mesa: (vacía)");
        } else {
            System.out.println("En la mesa: " + String.join(" | ", mesa));
        }

        List<String> cartas = controlador.getCartasDelJugadorQueDebeActuar();
        // Las cartas del compañero (en el juego real se pasan con señas)
        List<String> cartasCompanero = controlador.getCartasDelCompanero();
        if (cartasCompanero.isEmpty()) {
            System.out.println("Tu compañero " + controlador.getCompanero() + " ya jugó todas sus cartas");
        } else {
            System.out.println("Cartas de tu compañero " + controlador.getCompanero() + ": "
                    + String.join(" | ", cartasCompanero));
        }

        System.out.println("Tus cartas:");
        for (int i = 0; i < cartas.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + cartas.get(i));
        }

        if (controlador.sePuedeCantarTruco()) {
            System.out.println("  T) Cantar " + controlador.getProximoCantoTruco());
        }
        if (controlador.sePuedeCantarEnvido()) {
            System.out.println("  E) Envido   R) Real Envido   F) Falta Envido");
        }
        System.out.println("  M) Irse al mazo");

        String opcion = pedirTexto("Elegí una opción: ").toUpperCase();

        if (esNumero(opcion)) {
            // El usuario ve las cartas numeradas desde 1, pero la lista empieza en 0
            controlador.jugarCarta(Integer.parseInt(opcion) - 1);
        } else if (opcion.equals("T")) {
            controlador.cantarTruco();
        } else if (opcion.equals("E")) {
            controlador.cantarEnvido();
        } else if (opcion.equals("R")) {
            controlador.cantarRealEnvido();
        } else if (opcion.equals("F")) {
            controlador.cantarFaltaEnvido();
        } else if (opcion.equals("M")) {
            controlador.irseAlMazo();
        } else {
            mostrarError("Opción inválida.");
        }
    }

    /** Menú de respuesta: alguien cantó y responde cualquiera de los dos rivales. */
    private void mostrarMenuRespuesta() {
        System.out.println();
        System.out.println(controlador.getJugadorQueCanto() + " cantó " + controlador.getCantoPendiente()
                + ". Responde " + controlador.getEquipoQueResponde() + ".");

        // Paso 1: se muestran los dos rivales con sus cartas, y se elige cuál responde
        List<String> rivales = controlador.getRivalesQuePuedenResponder();
        for (int i = 0; i < rivales.size(); i++) {
            List<String> cartas = controlador.getCartasDeRival(i + 1);
            System.out.println("  " + (i + 1) + ") " + rivales.get(i) + " -> " + String.join(" | ", cartas));
        }
        String eleccion = pedirTexto("¿Quién responde? ");
        if (!esNumero(eleccion)) {
            mostrarError("Elegí el número del jugador.");
            return; // el while de iniciar() vuelve a mostrar este menú
        }
        if (!controlador.elegirRespondedor(Integer.parseInt(eleccion))) {
            return;
        }

        // Paso 2: el elegido decide qué contestar
        System.out.println("Responde " + controlador.getJugadorQueDebeActuar() + ":");

        System.out.println("  Q) Quiero");
        System.out.println("  N) No quiero");
        if (controlador.hayTrucoPendiente() && controlador.sePuedeCantarTruco()) {
            System.out.println("  T) Quiero " + controlador.getProximoCantoTruco());
        }
        if (controlador.hayEnvidoPendiente()) {
            System.out.println("  E) Envido   R) Real Envido   F) Falta Envido");
        }

        String opcion = pedirTexto("Elegí una opción: ").toUpperCase();

        if (opcion.equals("Q")) {
            controlador.quiero();
        } else if (opcion.equals("N")) {
            controlador.noQuiero();
        } else if (opcion.equals("T")) {
            controlador.cantarTruco();
        } else if (opcion.equals("E")) {
            controlador.cantarEnvido();
        } else if (opcion.equals("R")) {
            controlador.cantarRealEnvido();
        } else if (opcion.equals("F")) {
            controlador.cantarFaltaEnvido();
        } else {
            mostrarError("Opción inválida.");
        }
    }

    // =====================================================================
    // Lo que el Controlador le pide mostrar (métodos de IVista)
    // =====================================================================

    @Override
    public void mostrarInicioRonda(String jugadorMano) {
        System.out.println();
        System.out.println("==============================");
        System.out.println("Nueva ronda. Es mano: " + jugadorMano);
    }

    @Override
    public void mostrarMarcador(String equipoA, int puntosA, String equipoB, int puntosB) {
        System.out.println("[ " + equipoA + ": " + puntosA + " | " + equipoB + ": " + puntosB + " ]");
    }

    @Override
    public void mostrarCartaJugada(String jugador, String carta) {
        System.out.println(">> " + jugador + " tira el " + carta);
    }

    @Override
    public void mostrarGanadorBaza(String jugador) {
        System.out.println(">> La baza la gana " + jugador);
    }

    @Override
    public void mostrarBazaParda() {
        System.out.println(">> La baza quedó parda");
    }

    @Override
    public void mostrarCanto(String jugador, String canto) {
        System.out.println(">> " + jugador + ": ¡" + canto + "!");
    }

    @Override
    public void mostrarRespuesta(String jugador, String respuesta) {
        System.out.println(">> " + jugador + ": " + respuesta);
    }

    @Override
    public void mostrarResultadoEnvido(String equipo, int tanto, int puntos) {
        System.out.println(">> " + equipo + " gana el envido con " + tanto + " y suma " + puntos + " punto(s)");
    }

    @Override
    public void mostrarPuntosNoQuerido(String equipo, int puntos) {
        System.out.println(">> " + equipo + " suma " + puntos + " punto(s)");
    }

    @Override
    public void mostrarJugadorAlMazo(String jugador) {
        System.out.println(">> " + jugador + " se fue al mazo");
    }

    @Override
    public void mostrarGanadorRonda(String equipo, int puntos) {
        System.out.println(">> " + equipo + " gana la ronda y suma " + puntos + " punto(s)");
    }

    @Override
    public void mostrarGanadorPartida(String equipo) {
        System.out.println();
        System.out.println("******************************");
        System.out.println("  ¡GANÓ LA PARTIDA " + equipo.toUpperCase() + "!");
        System.out.println("******************************");
    }

    @Override
    public void mostrarError(String mensaje) {
        System.out.println("!! " + mensaje);
    }

    // =====================================================================
    // Ayudas para leer del teclado
    // =====================================================================

    private String pedirTexto(String mensaje) {
        System.out.print(mensaje);
        String texto = scanner.nextLine().trim();
        while (texto.isEmpty()) {
            System.out.print(mensaje);
            texto = scanner.nextLine().trim();
        }
        return texto;
    }

    private int pedirPuntosParaGanar() {
        String opcion = pedirTexto("¿A cuántos puntos? (15 o 30): ");
        while (!opcion.equals("15") && !opcion.equals("30")) {
            mostrarError("Tiene que ser 15 o 30.");
            opcion = pedirTexto("¿A cuántos puntos? (15 o 30): ");
        }
        return Integer.parseInt(opcion);
    }

    private boolean esNumero(String texto) {
        if (texto.isEmpty() || texto.length() > 2) {
            return false; // las cartas van del 1 al 3, no hace falta aceptar números largos
        }
        for (int i = 0; i < texto.length(); i++) {
            if (!Character.isDigit(texto.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
