package ar.edu.unlu.poo.observer;

/**
 * Lo implementa el modelo. Mantiene una lista de observadores y les avisa cada vez que algo cambia.
 * El modelo NO conoce a la vista ni al controlador: solo conoce esta interfaz.
 */
public interface IObservable {
    void agregarObservador(IObservador observador);

    void quitarObservador(IObservador observador);

    void notificarObservadores(Evento evento);
}
