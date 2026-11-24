#!/usr/bin/env bash
# Compila y ejecuta la aplicación
set -e
rm -rf out && mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out biblioteca.Main
