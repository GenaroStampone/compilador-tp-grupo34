#!/bin/bash
# ==============================================================================
# SUITE DE PRUEBAS - FILTRADO POR CÓDIGO DE SALIDA DEL COMPILADOR (0 / 1)
# Uso: ./tu_script.sh [carpeta_de_pruebas]
# ==============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -d "$SCRIPT_DIR/compilador-tp-grupo34" ]; then
    cd "$SCRIPT_DIR/compilador-tp-grupo34" || exit 1
else
    cd "$SCRIPT_DIR" || exit 1
fi

CARPETA="${1:-casos_de_prueba}"
CARPETA="${CARPETA#./}"
CARPETA="${CARPETA%/}"

if [ ! -d "$CARPETA" ]; then
    echo -e "\033[0;31mERROR: No se encontró la carpeta '$CARPETA'.\033[0m"
    exit 1
fi

# 1. Compilación del proyecto Java
echo -e "\n[PASO 1] Compilando el código fuente del proyecto..."
mkdir -p bin
javac -d bin src/*.java
if [ $? -ne 0 ]; then
    echo "ERROR CRÍTICO: Falló la compilación de los archivos Java."
    exit 1
fi
echo "Compilación exitosa."

# 2. Ejecución sobre todos los .txt
TOTAL=0
OK_WASM=0
RECHAZADOS_COMP=0
FALLADOS_WASM=0
CRASH_JAVA=0

declare -a LISTA_FAIL_WASM=()
declare -a LISTA_CRASH=()

echo -e "\n================================================================================"
echo "  EJECUTANDO PRUEBAS EN: $CARPETA/"
echo "================================================================================"

while IFS= read -r archivo; do
    ((TOTAL++))
    rm -f salida.wat salida.wasm

    SALIDA_COMP=$(./compilador "$archivo" 2>&1)
    RET_COMP=$?

    # Detectar si Java crasheó con una excepción no controlada
    if echo "$SALIDA_COMP" | grep -q "Exception in thread"; then
        echo -e "\033[0;31m[CRASH JAVA]\033[0m $archivo"
        echo -e "   --- SALIDA ---\n$SALIDA_COMP\n   --------------"
        ((CRASH_JAVA++))
        LISTA_CRASH+=("$archivo")
        continue
    fi

    # Si devolvió 0: compila sin errores -> validar WAT + WASM + Node
    if [ $RET_COMP -eq 0 ]; then
        if [ ! -f "salida.wat" ]; then
            echo -e "\033[0;31m[FAIL WASM]\033[0m  $archivo -> Devolvió 0 pero no generó salida.wat"
            ((FALLADOS_WASM++))
            LISTA_FAIL_WASM+=("$archivo (sin salida.wat)")
            continue
        fi

        SALIDA_WAT=$(wat2wasm salida.wat -o salida.wasm 2>&1)
        RET_WAT=$?
        if [ $RET_WAT -ne 0 ] || [ ! -f "salida.wasm" ]; then
            echo -e "\033[0;31m[FAIL WASM]\033[0m  $archivo -> Falló wat2wasm (código $RET_WAT)"
            echo -e "   --- WAT2WASM ---\n$SALIDA_WAT\n   ----------------"
            ((FALLADOS_WASM++))
            LISTA_FAIL_WASM+=("$archivo (error wat2wasm)")
            continue
        fi

        SALIDA_NODE=$(node ejecutar.js salida.wasm 2>&1)
        RET_NODE=$?
        if [ $RET_NODE -ne 0 ]; then
            echo -e "\033[0;31m[FAIL WASM]\033[0m  $archivo -> Falló node ejecutar.js (código $RET_NODE)"
            echo -e "   --- NODE ---\n$SALIDA_NODE\n   ------------"
            ((FALLADOS_WASM++))
            LISTA_FAIL_WASM+=("$archivo (error runtime node)")
            continue
        fi

        echo -e "\033[0;32m[OK + WASM]\033[0m  $archivo"
        ((OK_WASM++))

    # Si devolvió 1 (o != 0): error de compilación -> no corre WASM
    else
        echo -e "\033[0;33m[ERR COMP:$RET_COMP]\033[0m $archivo"
        ((RECHAZADOS_COMP++))
    fi
done < <(find "$CARPETA" -type f -name "*.txt" | sort)

# 3. Resumen final
echo -e "\n================================================================================"
echo "                             RESUMEN DE LA SUITE                                "
echo "================================================================================"
echo " Total ejecutados                   : $TOTAL"
echo -e " \033[0;32mCompilaron (0) y corrieron en WASM\033[0m : $OK_WASM"
echo -e " \033[0;33mRechazados en compilación (1)\033[0m      : $RECHAZADOS_COMP"
echo -e " \033[0;31mFallaron en WAT/WASM/Node\033[0m          : $FALLADOS_WASM"
echo -e " \033[0;31mCrashes de Java\033[0m                    : $CRASH_JAVA"
echo "================================================================================"

if [ $FALLADOS_WASM -gt 0 ]; then
    echo -e "\033[0;31mFALLOS EN WASM:\033[0m"
    for f in "${LISTA_FAIL_WASM[@]}"; do echo "  - $f"; done
fi

if [ $CRASH_JAVA -gt 0 ]; then
    echo -e "\033[0;31mCRASHES DE JAVA:\033[0m"
    for f in "${LISTA_CRASH[@]}"; do echo "  - $f"; done
fi

if [ $FALLADOS_WASM -eq 0 ] && [ $CRASH_JAVA -eq 0 ]; then
    exit 0
else
    exit 1
fi
