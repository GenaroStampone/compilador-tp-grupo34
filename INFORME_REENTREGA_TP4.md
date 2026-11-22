# Informe de reentrega unificada TP1 a TP4 - Grupo 34

## Introducción

Este informe describe la reentrega unificada de los trabajos prácticos 1 a 4 del compilador del
Grupo 34. La implementación cubre análisis léxico y sintáctico, control de ámbitos, tabla de
símbolos, análisis semántico, generación de representación intermedia y generación de código
WebAssembly textual.

La salida del compilador reúne todos los resultados exigidos en las entregas anteriores: tira de
tokens, estructuras reconocidas, errores unificados, AST, tabla de símbolos, fuente numerado con
ruta y archivo `salida.wat`.

## Representación intermedia

La representación intermedia es un árbol sintáctico abstracto basado en `NodoArbol`. Cada nodo
contiene:

- `etiqueta`: operador, sentencia o construcción representada.
- `izq`, `der`, `ter`: hijos del nodo. El tercer hijo se usa para programas, funciones y llamadas.
- `regTS`: referencia a la entrada de la tabla de símbolos cuando el nodo es una hoja.
- `tipoToken`: tipo inferido o declarado.
- `info`: metadatos semánticos, por ejemplo registros de función, constantes de enumeración o
  atributos posicionales.

Las acciones semánticas de `gramatica.y` usan notación posicional de Yacc (`$$`, `$1`, `$2`, ...)
para construir el AST. Los casos principales son:

- Declaraciones: se crean nodos `VAR_DECL`, `TYPEDEF`, `CLASS` y `FUNC`.
- Expresiones: se construyen nodos binarios (`+`, `-`, `*`, `/`, comparaciones) y se insertan
  conversiones `ItoD` cuando un operando `ushortint` participa en una operación `doublef`.
- Sentencias de control: `IF` y `WHILE` guardan la condición y el cuerpo en subárboles separados.
- Asignaciones: la asignación normal usa `:=`; la asignación posicional guarda en `info` la clase,
  el atributo y el objeto.
- Invocaciones: el nodo `CALL` guarda el registro de la función en `info`, los argumentos en `izq`
  y la lista de orden en `der`.

### Bifurcaciones

El algoritmo usado para generar las bifurcaciones se resume así:

```text
si sentencia IF:
    compilar condición
    emitir if
    compilar rama then
    emitir else
    compilar rama else
    emitir end

mientras sentencia WHILE:
    crear etiqueta salida y etiqueta ciclo
    emitir block salida
    emitir loop ciclo
    compilar condición
    si la condición es falsa, saltar a salida
    compilar cuerpo
    saltar a ciclo
    cerrar loop
    cerrar block
```

## Generación de WebAssembly

El módulo generado exporta `memory` y la función `main`. Importa cuatro funciones del host:

- `log(i32)`: imprime enteros.
- `logf(f64)`: imprime flotantes.
- `logs(i32, i32)`: imprime cadenas desde memoria lineal.
- `abort(i32)`: finaliza la ejecución e informa el error runtime.

### Mecanismo de generación

La generación recorre el AST y emite instrucciones WebAssembly con variables auxiliares:

- Los resultados intermedios se guardan en temporales `$tmpN`.
- Las variables globales del programa se emiten como `global` mutable.
- Las variables locales y parámetros de funciones se emiten como `local`.
- Los objetos se ubican en memoria lineal; cada atributo ocupa un slot de 8 bytes.
- Los métodos reciben un parámetro oculto `$self` que apunta a la base del objeto.
- Las etiquetas de ciclo se generan con contadores (`$salidaN`, `$cicloN`).

### Operaciones y conversiones

- Suma, resta, producto y división entera usan instrucciones `i32`.
- Operaciones flotantes usan `f64`.
- La conversión implícita `ushortint` a `doublef` se emite como `f64.convert_i32_u`.
- La conversión `doublef` a `ushortint` se emite como `i32.trunc_f64_u`.
- Las comparaciones de enteros sin signo usan variantes `i32.*_u`.

## Chequeos en tiempo de ejecución

Los códigos de error emitidos por `abort` son:

| Código | Chequeo |
|---:|---|
| 1 | División por cero, entera y flotante |
| 2 | Overflow en suma de enteros `ushortint` |
| 3 | Overflow en suma de `doublef` |
| 4 | Resultado negativo en resta de enteros sin signo |
| 5 | Asignación inválida a variable de enumeración |

El grupo tiene asignados los chequeos `abch`; además se incorporó el chequeo `f` por pedido
expreso de la consigna de la reentrega.

Las constantes de enumeración se validan durante el análisis semántico. Las expresiones dinámicas
se validan en ejecución contra la lista de valores permitidos del tipo.

## Modificaciones a etapas anteriores

- Se unificaron las salidas de TP1, TP2, TP3 y TP4 en `Main`.
- Se eliminaron palabras obsoletas del lenguaje (`ENTONCES`, `TODF`, `TOUS`).
- Se admiten explícitamente las dos alternativas de cabecera del programa, con y sin `;`.
- Las funciones requieren al menos un parámetro formal.
- Se completaron `TYPEDEF`, clases, herencia múltiple, `private`, `friend`, métodos y `RET`.
- El acceso con punto queda reservado para invocación de métodos.
- El acceso posicional con corchetes se usa para atributos de objetos.
- Se integraron chequeos semánticos de redeclaración, tipos, visibilidad y parámetros.
- Se agregó `salida.wat` y un host Node.js para ejecutar y verificar los chequeos runtime.

## Casos de prueba y resultados

Se mantuvieron los nueve casos históricos de `casos_de_prueba/` y se agregaron casos de ejecución
y regresión en `pruebas/`:

- Ejecución correcta con impresión de cadenas y números.
- División por cero entera y flotante.
- Overflow de suma entera y flotante.
- Resta negativa de `ushortint`.
- Asignación inválida a enumeración.
- Acceso válido desde función amiga y denegado sin `friend`.
- Acceso posicional con índice variable sobre atributos de tipos distintos (rechazado).

Resultados de la ejecución final:

```text
./ejecutar_pruebas.sh  -> 9 pasadas, 0 falladas
./probar_tp4.sh        -> 20 pasadas, 0 falladas
```

Todos los `.wat` generados por los casos válidos fueron validados con `wat2wasm`.

## Uso de IA

Se utilizó la herramienta OpenCode con el modelo `opencode/big-pickle` para revisar el código
existente, detectar inconsistencias entre la gramática y el parser generado, corregir el
generador WebAssembly y preparar los scripts de prueba y la documentación.

Prompts utilizados, en forma resumida:

- Revisar el estado del repositorio y continuar la reentrega desde la compilación fallida.
- Corregir la generación de WAT y la integración de salidas en `Main`.
- Agregar pruebas runtime para divisiones por cero, overflows, resta negativa y enumeraciones.
- Actualizar README e informe de la reentrega.

Las sugerencias fueron revisadas y adaptadas al código existente antes de aplicarlas.
