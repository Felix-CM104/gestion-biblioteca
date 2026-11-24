# 📚 Gestión de Biblioteca

Aplicación de consola en **Java** para gestionar el catálogo, los socios y los préstamos de una biblioteca.
Proyecto de la asignatura **Programación** (1º DAM): programación orientada a objetos, colecciones, excepciones y ficheros.

## ✨ Funcionalidades

- Alta, baja, listado y búsqueda de libros (por título o autor)
- Alta y listado de socios (con validación de email)
- Préstamos y devoluciones:
  - Máximo **3 préstamos activos** por socio
  - Duración de **15 días**; multa de **0,50 €** por día de retraso
  - Listado de préstamos vencidos
- Los datos se guardan en ficheros **CSV** (carpeta `datos/`) y se cargan al iniciar

## 🧱 Estructura

```
src/biblioteca/
├── Main.java                  # Menú de consola
├── modelo/                    # Libro, Socio, Prestamo
├── servicio/                  # Lógica de negocio (Biblioteca) y excepción propia
└── persistencia/AlmacenCSV    # Lectura y escritura de ficheros CSV
test/biblioteca/BibliotecaTest # Pruebas
```

## ▶️ Cómo ejecutarlo

Requisitos: **JDK 17 o superior**.

```bash
bash run.sh     # compila y lanza la aplicación
bash test.sh    # ejecuta las pruebas
```

En Windows, o desde IntelliJ IDEA: marca `src` como carpeta de código fuente y ejecuta `biblioteca.Main`.

## 🛠️ Tecnologías

Java 17 · POO · Colecciones y Streams · `java.time` · Ficheros (NIO)
