package biblioteca;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.persistencia.AlmacenCSV;
import biblioteca.servicio.Biblioteca;
import biblioteca.servicio.BibliotecaException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Pruebas sencillas sin dependencias externas. Ejecutar con: ./test.sh
 */
public class BibliotecaTest {

    private static int ok = 0;
    private static int fallos = 0;

    public static void main(String[] args) throws Exception {
        prestamoYDevolucionATiempo();
        devolucionConRetrasoGeneraMulta();
        noSePuedePrestarLibroYaPrestado();
        limiteDePrestamosPorSocio();
        busquedaPorTexto();
        emailInvalidoLanzaExcepcion();
        guardarYCargarCSV();

        System.out.printf("%nResultado: %d OK, %d fallos%n", ok, fallos);
        if (fallos > 0) System.exit(1);
    }

    private static Biblioteca bibliotecaDePrueba() throws BibliotecaException {
        Biblioteca b = new Biblioteca();
        b.altaLibro(new Libro("111", "El Quijote", "Miguel de Cervantes", 1605));
        b.altaLibro(new Libro("222", "Clean Code", "Robert C. Martin", 2008));
        b.altaLibro(new Libro("333", "Java 8 in Action", "Raoul-Gabriel Urma", 2014));
        b.altaLibro(new Libro("444", "Head First Java", "Kathy Sierra", 2005));
        b.altaSocio("Ana López", "ana@correo.com");
        return b;
    }

    static void prestamoYDevolucionATiempo() throws Exception {
        Biblioteca b = bibliotecaDePrueba();
        LocalDate hoy = LocalDate.of(2026, 1, 1);
        b.prestar("111", 1, hoy);
        comprobar(!b.buscarLibro("111").isDisponible(), "el libro queda prestado");
        double multa = b.devolver("111", hoy.plusDays(10));
        comprobar(multa == 0, "sin multa si se devuelve a tiempo");
        comprobar(b.buscarLibro("111").isDisponible(), "el libro vuelve a estar disponible");
    }

    static void devolucionConRetrasoGeneraMulta() throws Exception {
        Biblioteca b = bibliotecaDePrueba();
        LocalDate hoy = LocalDate.of(2026, 1, 1);
        b.prestar("222", 1, hoy);
        double multa = b.devolver("222", hoy.plusDays(Prestamo.DIAS_PRESTAMO + 4));
        comprobar(multa == 4 * Prestamo.MULTA_POR_DIA, "multa de 4 días de retraso");
    }

    static void noSePuedePrestarLibroYaPrestado() throws Exception {
        Biblioteca b = bibliotecaDePrueba();
        b.altaSocio("Luis Pérez", "luis@correo.com");
        b.prestar("111", 1, LocalDate.now());
        comprobarExcepcion(() -> b.prestar("111", 2, LocalDate.now()), "no se presta un libro ya prestado");
    }

    static void limiteDePrestamosPorSocio() throws Exception {
        Biblioteca b = bibliotecaDePrueba();
        b.prestar("111", 1, LocalDate.now());
        b.prestar("222", 1, LocalDate.now());
        b.prestar("333", 1, LocalDate.now());
        comprobarExcepcion(() -> b.prestar("444", 1, LocalDate.now()), "máximo 3 préstamos por socio");
    }

    static void busquedaPorTexto() throws Exception {
        Biblioteca b = bibliotecaDePrueba();
        comprobar(b.buscarPorTexto("java").size() == 2, "busca 'java' sin distinguir mayúsculas");
        comprobar(b.buscarPorTexto("cervantes").size() == 1, "busca por autor");
    }

    static void emailInvalidoLanzaExcepcion() throws Exception {
        Biblioteca b = bibliotecaDePrueba();
        comprobarExcepcion(() -> b.altaSocio("Pepe", "esto-no-es-un-email"), "rechaza emails no válidos");
    }

    static void guardarYCargarCSV() throws Exception {
        Biblioteca b = bibliotecaDePrueba();
        b.prestar("333", 1, LocalDate.of(2026, 3, 1));
        Path tmp = Files.createTempDirectory("biblio");
        AlmacenCSV almacen = new AlmacenCSV(tmp);
        almacen.guardar(b);
        Biblioteca cargada = almacen.cargar();
        comprobar(cargada.listarLibros().size() == 4, "se cargan los 4 libros");
        comprobar(cargada.listarSocios().size() == 1, "se carga el socio");
        comprobar(cargada.prestamosActivosDe(1).size() == 1, "se carga el préstamo activo");
        comprobar(!cargada.buscarLibro("333").isDisponible(), "se conserva el estado del libro");
    }

    // ---------- utilidades ----------

    interface Accion { void ejecutar() throws Exception; }

    private static void comprobar(boolean condicion, String descripcion) {
        if (condicion) {
            ok++;
            System.out.println("  ✔ " + descripcion);
        } else {
            fallos++;
            System.out.println("  ✘ " + descripcion);
        }
    }

    private static void comprobarExcepcion(Accion accion, String descripcion) {
        try {
            accion.ejecutar();
            comprobar(false, descripcion);
        } catch (BibliotecaException | IllegalArgumentException e) {
            comprobar(true, descripcion);
        } catch (Exception e) {
            comprobar(false, descripcion + " (excepción inesperada: " + e + ")");
        }
    }
}
