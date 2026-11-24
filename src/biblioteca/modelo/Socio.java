package biblioteca.modelo;

import java.util.Objects;

/**
 * Socio de la biblioteca que puede tomar libros prestados.
 */
public class Socio {

    public static final int MAX_PRESTAMOS = 3;

    private final int id;
    private String nombre;
    private String email;

    public Socio(int id, String nombre, String email) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new IllegalArgumentException("Email no válido: " + email);
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.email = email.trim();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Socio)) return false;
        return id == ((Socio) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("%4d | %-25s | %s", id, nombre, email);
    }
}
