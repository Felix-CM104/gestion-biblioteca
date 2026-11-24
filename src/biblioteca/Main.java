package biblioteca;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.Socio;
import biblioteca.persistencia.AlmacenCSV;
import biblioteca.servicio.Biblioteca;
import biblioteca.servicio.BibliotecaException;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Menú de consola de la aplicación de gestión de biblioteca.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final AlmacenCSV almacen = new AlmacenCSV(Path.of("datos"));
    private static Biblioteca biblioteca;

    public static void main(String[] args) {
        try {
            biblioteca = almacen.cargar();
        } catch (IOException e) {
            System.out.println("No se pudieron cargar los datos: " + e.getMessage());
            biblioteca = new Biblioteca();
        }

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");
            try {
                ejecutar(opcion);
            } catch (BibliotecaException | IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        } while (opcion != 0);

        guardar();
        System.out.println("¡Hasta pronto!");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== GESTIÓN DE BIBLIOTECA =====");
        System.out.println(" 1. Listar libros");
        System.out.println(" 2. Añadir libro");
        System.out.println(" 3. Buscar libro por título/autor");
        System.out.println(" 4. Eliminar libro");
        System.out.println(" 5. Listar socios");
        System.out.println(" 6. Añadir socio");
        System.out.println(" 7. Prestar libro");
        System.out.println(" 8. Devolver libro");
        System.out.println(" 9. Ver préstamos vencidos");
        System.out.println("10. Guardar datos");
        System.out.println(" 0. Salir (guarda automáticamente)");
    }

    private static void ejecutar(int opcion) throws BibliotecaException {
        switch (opcion) {
            case 1 -> imprimir(biblioteca.listarLibros(), "No hay libros registrados.");
            case 2 -> {
                String isbn = leerTexto("ISBN: ");
                String titulo = leerTexto("Título: ");
                String autor = leerTexto("Autor: ");
                int anio = leerEntero("Año de publicación: ");
                biblioteca.altaLibro(new Libro(isbn, titulo, autor, anio));
                System.out.println("✔ Libro añadido.");
            }
            case 3 -> imprimir(biblioteca.buscarPorTexto(leerTexto("Texto a buscar: ")), "Sin resultados.");
            case 4 -> {
                biblioteca.bajaLibro(leerTexto("ISBN del libro a eliminar: "));
                System.out.println("✔ Libro eliminado.");
            }
            case 5 -> imprimir(biblioteca.listarSocios(), "No hay socios registrados.");
            case 6 -> {
                Socio s = biblioteca.altaSocio(leerTexto("Nombre: "), leerTexto("Email: "));
                System.out.println("✔ Socio creado con id " + s.getId());
            }
            case 7 -> {
                String isbn = leerTexto("ISBN: ");
                int id = leerEntero("Id del socio: ");
                Prestamo p = biblioteca.prestar(isbn, id, LocalDate.now());
                System.out.println("✔ Préstamo registrado. Devolver antes del " + p.getFechaLimite());
            }
            case 8 -> {
                double multa = biblioteca.devolver(leerTexto("ISBN: "), LocalDate.now());
                System.out.println(multa > 0
                        ? String.format("✔ Devuelto con retraso. Multa: %.2f €", multa)
                        : "✔ Devuelto a tiempo. ¡Gracias!");
            }
            case 9 -> imprimir(biblioteca.prestamosVencidos(LocalDate.now()), "No hay préstamos vencidos.");
            case 10 -> guardar();
            case 0 -> { }
            default -> System.out.println("Opción no válida.");
        }
    }

    private static void guardar() {
        try {
            almacen.guardar(biblioteca);
            System.out.println("💾 Datos guardados en la carpeta 'datos'.");
        } catch (IOException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    private static void imprimir(List<?> lista, String siVacia) {
        if (lista.isEmpty()) {
            System.out.println(siVacia);
        } else {
            lista.forEach(System.out::println);
        }
    }

    private static boolean finEntrada = false;

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (sc.hasNextLine()) {
            return sc.nextLine().trim();
        }
        finEntrada = true;
        return "";
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String linea = leerTexto(mensaje);
            if (finEntrada) {
                return 0; // fin de la entrada (Ctrl+D): salir guardando
            }
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número válido.");
            }
        }
    }
}
