package biblioteca.servicio;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Lógica de negocio: gestiona libros, socios y préstamos.
 */
public class Biblioteca {

    private final Map<String, Libro> libros = new LinkedHashMap<>();
    private final Map<Integer, Socio> socios = new LinkedHashMap<>();
    private final List<Prestamo> prestamos = new ArrayList<>();

    // ---------- Libros ----------

    public void altaLibro(Libro libro) throws BibliotecaException {
        if (libros.containsKey(libro.getIsbn())) {
            throw new BibliotecaException("Ya existe un libro con ISBN " + libro.getIsbn());
        }
        libros.put(libro.getIsbn(), libro);
    }

    public void bajaLibro(String isbn) throws BibliotecaException {
        Libro libro = buscarLibro(isbn);
        if (!libro.isDisponible()) {
            throw new BibliotecaException("No se puede dar de baja un libro prestado");
        }
        libros.remove(isbn);
    }

    public Libro buscarLibro(String isbn) throws BibliotecaException {
        Libro libro = libros.get(isbn);
        if (libro == null) {
            throw new BibliotecaException("No existe ningún libro con ISBN " + isbn);
        }
        return libro;
    }

    /** Busca por título o autor, sin distinguir mayúsculas. */
    public List<Libro> buscarPorTexto(String texto) {
        String t = texto.toLowerCase();
        return libros.values().stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(t)
                          || l.getAutor().toLowerCase().contains(t))
                .collect(Collectors.toList());
    }

    public List<Libro> listarLibros() {
        return libros.values().stream()
                .sorted(Comparator.comparing(Libro::getTitulo, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    // ---------- Socios ----------

    public Socio altaSocio(String nombre, String email) {
        int id = socios.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
        Socio socio = new Socio(id, nombre, email);
        socios.put(id, socio);
        return socio;
    }

    public void anadirSocio(Socio socio) {
        socios.put(socio.getId(), socio);
    }

    public Socio buscarSocio(int id) throws BibliotecaException {
        Socio socio = socios.get(id);
        if (socio == null) {
            throw new BibliotecaException("No existe ningún socio con id " + id);
        }
        return socio;
    }

    public List<Socio> listarSocios() {
        return new ArrayList<>(socios.values());
    }

    // ---------- Préstamos ----------

    public Prestamo prestar(String isbn, int idSocio, LocalDate fecha) throws BibliotecaException {
        Libro libro = buscarLibro(isbn);
        buscarSocio(idSocio);
        if (!libro.isDisponible()) {
            throw new BibliotecaException("El libro \"" + libro.getTitulo() + "\" ya está prestado");
        }
        long activos = prestamosActivosDe(idSocio).size();
        if (activos >= Socio.MAX_PRESTAMOS) {
            throw new BibliotecaException("El socio ya tiene " + Socio.MAX_PRESTAMOS + " préstamos activos");
        }
        Prestamo prestamo = new Prestamo(isbn, idSocio, fecha);
        prestamos.add(prestamo);
        libro.setDisponible(false);
        return prestamo;
    }

    /** Devuelve el libro y retorna la multa a pagar (0 si se devuelve a tiempo). */
    public double devolver(String isbn, LocalDate fecha) throws BibliotecaException {
        Prestamo prestamo = prestamoActivoDe(isbn)
                .orElseThrow(() -> new BibliotecaException("El libro " + isbn + " no está prestado"));
        prestamo.devolver(fecha);
        buscarLibro(isbn).setDisponible(true);
        return prestamo.calcularMulta(fecha);
    }

    public List<Prestamo> prestamosActivosDe(int idSocio) {
        return prestamos.stream()
                .filter(p -> p.isActivo() && p.getIdSocio() == idSocio)
                .collect(Collectors.toList());
    }

    public List<Prestamo> prestamosVencidos(LocalDate hoy) {
        return prestamos.stream()
                .filter(p -> p.isActivo() && p.diasRetraso(hoy) > 0)
                .collect(Collectors.toList());
    }

    public List<Prestamo> listarPrestamos() {
        return new ArrayList<>(prestamos);
    }

    public void anadirPrestamo(Prestamo p) {
        prestamos.add(p);
    }

    private Optional<Prestamo> prestamoActivoDe(String isbn) {
        return prestamos.stream().filter(p -> p.isActivo() && p.getIsbn().equals(isbn)).findFirst();
    }
}
