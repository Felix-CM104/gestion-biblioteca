package biblioteca.servicio;

/**
 * Error de negocio de la biblioteca (libro no encontrado, límite de préstamos, etc.).
 */
public class BibliotecaException extends Exception {
    public BibliotecaException(String mensaje) {
        super(mensaje);
    }
}
