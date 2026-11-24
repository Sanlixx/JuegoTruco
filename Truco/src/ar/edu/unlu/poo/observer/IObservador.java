package ar.edu.unlu.poo.observer;

/**
 * Lo implementa quien quiere enterarse de los cambios del modelo (en nuestro caso, el Controlador).
 * El modelo le avisa QUÉ pasó con un Evento; después el observador le pregunta al modelo
 * los datos que necesita mediante getters.
 */
public interface IObservador {
    void actualizar(Evento evento, IObservable observable);
}
