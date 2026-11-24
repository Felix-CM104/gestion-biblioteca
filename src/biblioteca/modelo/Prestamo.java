package biblioteca.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Préstamo de un libro a un socio. Dura {@link #DIAS_PRESTAMO} días.
 */
public class Prestamo {

    public static final int DIAS_PRESTAMO = 15;
    public static final double MULTA_POR_DIA = 0.50;

    private final String isbn;
    private final int idSocio;
    private final LocalDate fechaInicio;
    private LocalDate fechaDevolucion;

    public Prestamo(String isbn, int idSocio, LocalDate fechaInicio) {
        this(isbn, idSocio, fechaInicio, null);
    }

    public Prestamo(String isbn, int idSocio, LocalDate fechaInicio, LocalDate fechaDevolucion) {
        this.isbn = isbn;
        this.idSocio = idSocio;
        this.fechaInicio = fechaInicio;
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getIsbn() { return isbn; }
    public int getIdSocio() { return idSocio; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }

    public LocalDate getFechaLimite() {
        return fechaInicio.plusDays(DIAS_PRESTAMO);
    }

    public boolean isActivo() {
        return fechaDevolucion == null;
    }

    public void devolver(LocalDate fecha) {
        if (!isActivo()) {
            throw new IllegalStateException("El préstamo ya estaba devuelto");
        }
        this.fechaDevolucion = fecha;
    }

    /** Días de retraso respecto a la fecha límite (0 si no hay retraso). */
    public long diasRetraso(LocalDate hoy) {
        LocalDate fin = isActivo() ? hoy : fechaDevolucion;
        long dias = ChronoUnit.DAYS.between(getFechaLimite(), fin);
        return Math.max(0, dias);
    }

    public double calcularMulta(LocalDate hoy) {
        return diasRetraso(hoy) * MULTA_POR_DIA;
    }

    @Override
    public String toString() {
        return String.format("%-14s | socio %4d | desde %s | límite %s | %s",
                isbn, idSocio, fechaInicio, getFechaLimite(),
                isActivo() ? "ACTIVO" : "devuelto " + fechaDevolucion);
    }
}
