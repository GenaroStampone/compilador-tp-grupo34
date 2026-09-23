# Estado de Recuperación del Proyecto (Post-Apagón)

**Grupo:** 34  
**Temas Asignados:** 2, 8, 9, 11, 16, 18, 20, 23, 27, 29, 30, 35  
**Fecha de Diagnóstico:** 22 de Septiembre de 2026  

---

## 1. Integridad de los Archivos en Disco

- **Estado del repositorio Git:** Árbol de trabajo completamente limpio en rama `main` (commit `f173308`). Ningún archivo de código fuente sufrió truncamiento, corrupción o pérdida de datos.
- **Compilación Java:** Todos los archivos fuente (`AnalizadorLexico.java`, `TablaDeSimbolos.java`, `RegistroTS.java`, `Token.java`, `ParserVal.java`, `AccionSemantica.java`, `Main.java`) compilan exitosamente sin errores de sintaxis (`javac src/*.java` finaliza con código 0).
- **Archivos del Parser:** No se generaron ni modificaron archivos del parser antes del apagón (el sistema se encontraba en la fase de inspección y validación de herramientas `byacc`/`javac`).

---

## 2. Qué partes del Paso 1 SÍ quedaron bien escritas en disco

1. **Mecanismo de comunicación con BYacc / Parser:**
   - Método `public int yylex()` en `AnalizadorLexico.java` que retorna el código numérico del token y devuelve `0` al llegar al fin de archivo (`EOF`).
   - Atributo semántico público `public ParserVal yylval` implementado y sincronizado con `ParserVal.java`.
   - Envío de atributos a través de `yylval` para tokens con lexema (`ID`, `CTE_ENTERA`, `DOUBLEF_VAL`, `CADENA`) vinculados con el alta en la `TablaDeSimbolos`.
2. **Tokens y reglas léxicas de TP1 para Grupo 34:**
   - **Tema 2:** Constantes enteras sin signo de 8 bits con sufijo `$us` (rango 0 a 255) y palabra reservada `ushortint` (token 279).
   - **Tema 8:** Constantes de punto flotante de 64 bits con parte exponencial opcional (`d`, `d+`, `d-`) y palabra reservada `doublef` (token 280).
   - **Tema 9:** Cadenas de caracteres de 1 línea delimitadas por llaves `{ ... }` (token 260).
   - **Tema 16:** Comentarios multilínea delimitados por `{{ ... }}` con actualización de número de línea.
   - **Operadores y delimitadores existentes:** `:=`, `==`, `!=`, `<=`, `>=`, `=`, `<`, `>`, `+`, `-`, `*`, `/`, `(`, `)`, `,`, `;`.

---

## 3. Qué partes del Paso 1 quedaron incompletas o faltan para Grupo 34 (TP2)

Para completar al 100% la adaptación del Analizador Léxico previa a la construcción de la gramática del Parser (TP2), resta implementar:

1. **Nuevos símbolos requeridos en el autómata léxico:**
   - **Corchetes `[` y `]`:** Requeridos obligatoriamente por el **Tema 20** (orden de evaluación de parámetros: `f()[1,2]`), **Tema 23** (enumeraciones: `typedef T = [...]`) y **Tema 29** (acceso posicional a atributos: `obj[0$i]`).
   - *Estado actual:* No están mapeados en `mapaCaracteres` de `AnalizadorLexico.java` (caen en columna `COLUMNA_C`, emitiendo error de carácter inesperado en lugar de token ASCII).
2. **Nuevas Palabras Reservadas en `TablaDeSimbolos` y `AnalizadorLexico`:**
   - **Tema 11:** `while` y `repeat`.
   - **Tema 23:** `typedef`.
   - **Tema 27:** `friend` y `private`.
   - **Tema 30 / Herencia:** `extends`.
   - **Temas 33 y 34 (Conversiones asignadas a Grupo 34):**
     - `todf` (conversión explícita a flotante de 64 bits para grupos con Tema 8).
     - `tous` (conversión explícita a entero corto sin signo para grupos con Tema 2).
3. **Constantes de token y mapeo de tokens:**
   - Definir los identificadores numéricos de token en `AnalizadorLexico.java` para las nuevas palabras reservadas y reflejarlos en `obtenerTokenPalabraReservada()`.
4. **Archivo del Parser:**
   - Creación del archivo de especificación sintáctica (`gramatica.y`) para ser procesado por BYacc/J.
