#!/usr/bin/env bash
# Compila y ejecuta las pruebas
set -e
rm -rf out-test && mkdir -p out-test
javac -encoding UTF-8 -d out-test $(find src test -name "*.java")
java -cp out-test biblioteca.BibliotecaTest
