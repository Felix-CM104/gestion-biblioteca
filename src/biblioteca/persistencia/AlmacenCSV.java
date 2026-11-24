package biblioteca.persistencia;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import biblioteca.servicio.Biblioteca;
import biblioteca.servicio.BibliotecaException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda y carga los datos de la biblioteca en ficheros CSV (separados por ';').
 */
public class AlmacenCSV {

    private static final String SEP = ";";
    private final Path carpeta;

    public AlmacenCSV(Path carpeta) {
        this.carpeta = carpeta;
    }

    public void guardar(Biblioteca b) throws IOException {
        Files.createDirectories(carpeta);

        List<String> lineas = new ArrayList<>();
        for (Libro l : b.listarLibros()) {
            lineas.add(String.join(SEP, l.getIsbn(), limpiar(l.getTitulo()), limpiar(l.getAutor()),
                    String.valueOf(l.getAnio()), String.valueOf(l.isDisponible())));
        }
        escribir("libros.csv", lineas);

        lineas = new ArrayList<>();
        for (Socio s : b.listarSocios()) {
            lineas.add(String.join(SEP, String.valueOf(s.getId()), limpiar(s.getNombre()), s.getEmail()));
        }
        escribir("socios.csv", lineas);

        lineas = new ArrayList<>();
        for (Prestamo p : b.listarPrestamos()) {
            lineas.add(String.join(SEP, p.getIsbn(), String.valueOf(p.getIdSocio()),
                    p.getFechaInicio().toString(),
                    p.getFechaDevolucion() == null ? "" : p.getFechaDevolucion().toString()));
        }
        escribir("prestamos.csv", lineas);
    }

    public Biblioteca cargar() throws IOException {
        Biblioteca b = new Biblioteca();

        for (String[] c : leer("libros.csv")) {
            Libro l = new Libro(c[0], c[1], c[2], Integer.parseInt(c[3]));
            l.setDisponible(Boolean.parseBoolean(c[4]));
            try {
                b.altaLibro(l);
            } catch (BibliotecaException e) {
                System.err.println("Aviso: " + e.getMessage());
            }
        }
        for (String[] c : leer("socios.csv")) {
            b.anadirSocio(new Socio(Integer.parseInt(c[0]), c[1], c[2]));
        }
        for (String[] c : leer("prestamos.csv")) {
            LocalDate dev = c.length > 3 && !c[3].isBlank() ? LocalDate.parse(c[3]) : null;
            b.anadirPrestamo(new Prestamo(c[0], Integer.parseInt(c[1]), LocalDate.parse(c[2]), dev));
        }
        return b;
    }

    private void escribir(String fichero, List<String> lineas) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(carpeta.resolve(fichero), StandardCharsets.UTF_8)) {
            for (String linea : lineas) {
                w.write(linea);
                w.newLine();
            }
        }
    }

    private List<String[]> leer(String fichero) throws IOException {
        List<String[]> filas = new ArrayList<>();
        Path ruta = carpeta.resolve(fichero);
        if (!Files.exists(ruta)) {
            return filas;
        }
        try (BufferedReader r = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = r.readLine()) != null) {
                if (!linea.isBlank()) {
                    filas.add(linea.split(SEP, -1));
                }
            }
        }
        return filas;
    }

    /** Evita que un ';' en el texto rompa el formato del CSV. */
    private static String limpiar(String texto) {
        return texto.replace(SEP, ",");
    }
}
