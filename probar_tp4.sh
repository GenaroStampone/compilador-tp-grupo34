#!/usr/bin/env bash
set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

PASADAS=0
FALLADAS=0

ok() {
    printf '  [PASS] %s\n' "$1"
    PASADAS=$((PASADAS + 1))
}

fail() {
    printf '  [FAIL] %s\n' "$1"
    if [ -n "${2:-}" ] && [ -f "$2" ]; then
        printf '%s\n' '--- salida ---'
        tail -40 "$2"
        printf '%s\n' '--------------'
    fi
    FALLADAS=$((FALLADAS + 1))
}

for cmd in javac node wat2wasm timeout; do
    if ! command -v "$cmd" >/dev/null 2>&1; then
        printf 'ERROR: falta la herramienta %s\n' "$cmd" >&2
        exit 1
    fi
done

printf '%s\n' '================================================================================'
printf '%s\n' '        SUITE TP4 - GRUPO 34: COMPILACION, WAT Y CHEQUEOS RUNTIME              '
printf '%s\n' '================================================================================'

printf '\n%s\n' '[1] Compilando el compilador'
if javac -d bin src/*.java >"$TMP_DIR/javac.log" 2>&1; then
    ok 'javac'
else
    fail 'javac' "$TMP_DIR/javac.log"
    printf '\nResumen: %d pasadas, %d falladas\n' "$PASADAS" "$FALLADAS"
    exit 1
fi

printf '\n%s\n' '[2] Casos de compilacion (TP1-TP4)'
CASOS=(
    'casos_de_prueba/01_prueba_correcto.txt|VALIDO|constantes y variables basicas'
    'casos_de_prueba/02_prueba_warnings_id.txt|VALIDO|identificadores truncados'
    'casos_de_prueba/03_prueba_errores_constantes.txt|INVALIDO|errores de rango'
    'casos_de_prueba/04_prueba_errores_simbolos.txt|INVALIDO|caracteres invalidos'
    'casos_de_prueba/05_prueba_cadenas_y_comentarios.txt|VALIDO|cadenas y comentarios'
    'casos_de_prueba/06_prueba_errores_sintacticos_base.txt|INVALIDO|errores sintacticos'
    'casos_de_prueba/07_programa_integral_valido.txt|VALIDO|programa integral'
    'casos_de_prueba/08_programa_complejo_errores.txt|INVALIDO|errores complejos'
    'casos_de_prueba/09_prueba_declaraciones_y_poo.txt|VALIDO|declaraciones y POO'
)

for caso in "${CASOS[@]}"; do
    IFS='|' read -r archivo esperado descripcion <<< "$caso"
    comp_log="$TMP_DIR/comp.log"
    run_log="$TMP_DIR/run.log"
    wasm="$TMP_DIR/caso.wasm"
    printf '%-58s ' "$(basename "$archivo")"
    ./compilador "$archivo" >"$comp_log" 2>&1
    codigo=$?
    if [ "$esperado" = 'VALIDO' ]; then
        if [ "$codigo" -ne 0 ]; then
            fail "$descripcion: esperado VALIDO, salida $codigo" "$comp_log"
        elif ! wat2wasm salida.wat -o "$wasm" >"$TMP_DIR/wat.log" 2>&1; then
            fail "$descripcion: wat2wasm rechazo salida.wat" "$TMP_DIR/wat.log"
        elif ! timeout 10 node ejecutar.js "$wasm" >"$run_log" 2>&1; then
            fail "$descripcion: error al ejecutar WASM" "$run_log"
        else
            ok "$descripcion"
        fi
    else
        if [ "$codigo" -eq 0 ]; then
            fail "$descripcion: esperado INVALIDO, pero compilo" "$comp_log"
        else
            ok "$descripcion"
        fi
    fi
done

printf '\n%s\n' '[3] Chequeos en tiempo de ejecucion'
RUNTIME=(
    'pruebas/runtime_ok.txt|OK|0|ejecucion correcta'
    'pruebas/runtime_div0_entero.txt|ERROR|1|division por cero entera'
    'pruebas/runtime_div0_flotante.txt|ERROR|1|division por cero flotante'
    'pruebas/runtime_overflow_suma_entero.txt|ERROR|2|overflow suma entera'
    'pruebas/runtime_overflow_suma_flotante.txt|ERROR|3|overflow suma flotante'
    'pruebas/runtime_resta_negativa.txt|ERROR|4|resta negativa ushortint'
    'pruebas/runtime_enum_invalida.txt|ERROR|5|enum invalida'
)

for caso in "${RUNTIME[@]}"; do
    IFS='|' read -r archivo esperado codigo descripcion <<< "$caso"
    comp_log="$TMP_DIR/comp.log"
    run_log="$TMP_DIR/run.log"
    wasm="$TMP_DIR/runtime.wasm"
    printf '%-52s ' "$(basename "$archivo")"
    if ! ./compilador "$archivo" >"$comp_log" 2>&1; then
        fail "$descripcion: no compilo" "$comp_log"
    elif ! wat2wasm salida.wat -o "$wasm" >"$TMP_DIR/wat.log" 2>&1; then
        fail "$descripcion: wat2wasm rechazo salida.wat" "$TMP_DIR/wat.log"
    else
        timeout 10 node ejecutar.js "$wasm" >"$run_log" 2>&1
        resultado=$?
        if [ "$esperado" = 'OK' ]; then
            if [ "$resultado" -eq 0 ] && grep -q 'RESULTADO_WASM=0' "$run_log"; then
                ok "$descripcion"
            else
                fail "$descripcion: esperaba ejecucion correcta" "$run_log"
            fi
        else
            if [ "$resultado" -ne 0 ] && grep -q "ERROR EN TIEMPO DE EJECUCION $codigo" "$run_log"; then
                ok "$descripcion (codigo $codigo)"
            else
                fail "$descripcion: esperaba error runtime $codigo" "$run_log"
            fi
        fi
    fi
done

printf '\n%s\n' '================================================================================'
printf ' Resumen: %d pasadas, %d falladas\n' "$PASADAS" "$FALLADAS"
printf '%s\n' '================================================================================'

if [ "$FALLADAS" -eq 0 ]; then
    printf '%s\n' 'TODAS LAS PRUEBAS TP4 PASARON'
    exit 0
fi
printf '%s\n' 'HAY PRUEBAS TP4 FALLIDAS'
exit 1
