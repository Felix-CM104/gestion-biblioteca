package biblioteca.modelo;

import java.util.Objects;

/**
 * Representa un libro del catálogo de la biblioteca.
 */
public class Libro {

    private final String isbn;
    private String titulo;
    private String autor;
    private int anio;
    private boolean disponible;

    public Libro(String isbn, String titulo, String autor, int anio) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("El ISBN no puede estar vacío");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        this.isbn = isbn.trim();
        this.titulo = titulo.trim();
        this.autor = autor == null ? "" : autor.trim();
        this.anio = anio;
        this.disponible = true;
    }

    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAnio() { return anio; }
    public boolean isDisponible() { return disponible; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setAnio(int anio) { this.anio = anio; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Libro)) return false;
        return isbn.equals(((Libro) o).isbn);
    }

    @Override
    public int hashCode() { return Objects.hash(isbn); }

    @Override
    public String toString() {
        return String.format("%-14s | %-30s | %-22s | %4d | %s",
                isbn, recortar(titulo, 30), recortar(autor, 22), anio,
                disponible ? "Disponible" : "Prestado");
    }

    private static String recortar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max - 1) + "…";
    }
}
