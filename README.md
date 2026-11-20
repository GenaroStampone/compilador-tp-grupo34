# Compilador Grupo 34 - Reentrega unificada TP1 a TP4

Compilador del lenguaje definido para el cursado, con análisis léxico, sintáctico, semántico,
representación intermedia en forma de AST y generación de WebAssembly textual (`.wat`).

## Requisitos

- JDK 8 o superior.
- Bash.
- Node.js 18 o superior para ejecutar los módulos WebAssembly generados.
- `wat2wasm` de WABT para validar/ensamblar `.wat`.

## Compilación del compilador

```bash
javac -d bin src/*.java
```

## Uso

```bash
./compilador casos_de_prueba/01_prueba_correcto.txt
```

El compilador imprime, en este orden:

1. Tira de tokens.
2. Estructuras sintácticas detectadas.
3. Errores léxicos, sintácticos y semánticos agrupados, con número de línea.
4. Representación intermedia (AST).
5. Tabla de símbolos.
6. Fuente numerado con su ruta absoluta.
7. Datos de generación de `salida.wat`.

Si no hay errores, se genera `salida.wat` en el directorio actual. Si hay errores, no se genera.

## Ejecutar el WebAssembly generado

```bash
wat2wasm salida.wat -o salida.wasm
node ejecutar.js salida.wasm
```

`ejecutar.js` provee las funciones de importación `log`, `logf`, `logs` y `abort`, y muestra
los errores de ejecución con su código.

## Pruebas

Suite histórica de casos de compilación:

```bash
./ejecutar_pruebas.sh
```

Suite completa de la reentrega, que incluye compilación, validación con `wat2wasm`, ejecución
y chequeos en tiempo de ejecución:

```bash
./probar_tp4.sh
```

## Estructura

```text
compilador-tp-grupo34/
├── casos_de_prueba/           # Casos TP1-TP4
├── pruebas/                   # Casos de ejecución y errores runtime
├── src/                       # Fuentes Java del compilador
│   ├── AnalizadorLexico.java
│   ├── ControlAmbitos.java
│   ├── GeneradorCodigoIntermedio.java
│   ├── GeneradorWebAssembly.java
│   ├── Main.java
│   ├── NodoArbol.java
│   ├── Parser.java
│   ├── ParserVal.java
│   ├── RegistroTS.java
│   ├── TablaDeSimbolos.java
│   ├── InfoFuncion.java
│   ├── ParametroFormal.java
│   └── Token.java
├── compilador                 # Wrapper de ejecución
├── ejecutar.js                # Host para ejecutar .wasm
├── ejecutar_pruebas.sh        # Suite de compilación
├── probar_tp4.sh              # Suite completa TP4
├── gramatica.y                # Gramática Yacc
├── y.output                   # Informe de conflictos
├── salida.wat                 # Código WebAssembly generado
└── INFORME_REENTREGA_TP4.md   # Informe de la reentrega
```

## Regenerar el parser

```bash
byaccj -J -v -b Parser gramatica.y
mv -f Parser.java src/Parser.java
cp Parser.output y.output
javac -d bin src/*.java
```
