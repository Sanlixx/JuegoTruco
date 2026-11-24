package ar.edu.unlu.poo;

import ar.edu.unlu.poo.controlador.Controlador;
import ar.edu.unlu.poo.vista.IVista;
import ar.edu.unlu.poo.vista.VistaConsola;

public class Main {
    public static void main(String[] args) {
        IVista vista = new VistaConsola();
        new Controlador(vista);   // el controlador se presenta solo a la vista (setControlador)
        vista.iniciar();
    }
}
