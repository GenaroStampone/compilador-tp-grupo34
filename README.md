# Compilador Grupo 34 - TP2

## Requisitos del Sistema
- **Java JDK** 8 o superior instalado.
- Entorno de comandos **Bash** (Linux/macOS o Git Bash en Windows).

## Permisos de Ejecución
Antes de ejecutar por primera vez, otorgar permisos de ejecución a los scripts:
```bash
chmod +x compilador ejecutar_pruebas.sh
```

## Modo de Uso

### 1. Ejecutar un Caso de Prueba Individual
Para probar un archivo fuente específico y ver el resultado completo en consola (reglas reconocidas, warnings y Tabla de Símbolos):
```bash
./compilador casos_de_prueba/01_prueba_correcto.txt
./compilador casos_de_prueba/02_prueba_warnings_id.txt
./compilador casos_de_prueba/03_prueba_errores_constantes.txt
./compilador casos_de_prueba/04_prueba_errores_simbolos.txt
./compilador casos_de_prueba/05_prueba_cadenas_y_comentarios.txt
./compilador casos_de_prueba/06_prueba_errores_sintacticos_base.txt
./compilador casos_de_prueba/07_programa_integral_valido.txt
./compilador casos_de_prueba/08_programa_complejo_errores.txt
./compilador casos_de_prueba/09_prueba_declaraciones_y_poo.txt
```

### 3. Ejecutar la Suite de Pruebas Automatizada
Para correr la batería completa de pruebas (Casos 01 al 09) y verificar los resultados de éxito/fallo:
```bash
./ejecutar_pruebas.sh
```

Aquí tenés el bloque en Markdown perfectamente formateado e identado dentro de un bloque de código listo para copiar y pegar directamente en tu **`README.md`**:

## Estructura de Directorios

```text
compilador-tp-grupo34/
├── casos_de_prueba/           # Archivos .txt con los casos de prueba unitarios e integrales
│   ├── 01_prueba_correcto.txt
│   ├── 02_prueba_warnings_id.txt
│   ├── 03_prueba_errores_constantes.txt
│   ├── 04_prueba_errores_simbolos.txt
│   ├── 05_prueba_cadenas_y_comentarios.txt
│   ├── 06_prueba_errores_sintacticos_base.txt
│   ├── 07_programa_integral_valido.txt
│   ├── 08_programa_complejo_errores.txt
│   └── 09_prueba_declaraciones_y_poo.txt
├── src/                       # Código fuente Java del Analizador Léxico, Parser (BYACC), Tabla de Símbolos, etc.
│   ├── AccionSemantica.java
│   ├── AnalizadorLexico.java
│   ├── Main.java
│   ├── Parser.java
│   ├── ParserVal.java
│   ├── RegistroTS.java
│   ├── TablaDeSimbolos.java
│   ├── Token.java
│   └── ValidadorDeclaraciones.java
├── compilador                 # Script ejecutable principal
├── ejecutar_pruebas.sh        # Script ejecutable de pruebas automatizadas
├── gramatica.y                # Especificación gramatical del parser en YACC
├── README.md                  # Requisitos del sistema, permisos de ejecución y modo de uso
└── y.output                   # Informe de estados y verificación de 0 conflictos Shift/Reduce
```

