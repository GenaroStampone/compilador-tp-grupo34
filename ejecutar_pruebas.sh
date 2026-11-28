#!/bin/bash
# ==============================================================================
# SUITE DE PRUEBAS AUTOMATIZADA - COMPILADOR GRUPO 34
# Uso: ./tu_script.sh [carpeta_de_pruebas]
# ==============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -d "$SCRIPT_DIR/compilador-tp-grupo34" ]; then
    cd "$SCRIPT_DIR/compilador-tp-grupo34" || exit 1
else
    cd "$SCRIPT_DIR" || exit 1
fi

# Carpeta relativa (por defecto: casos_de_prueba)
CARPETA="${1:-casos_de_prueba}"
CARPETA="${CARPETA#./}"
CARPETA="${CARPETA%/}"

if [ ! -d "$CARPETA" ]; then
    echo -e "\033[0;31mERROR: No se encontró la carpeta '$CARPETA' dentro de $(pwd).\033[0m"
    echo "Uso: $0 [carpeta_de_pruebas]"
    exit 1
fi

echo "================================================================================"
echo "         INICIANDO COMPILACIÓN Y EJECUCIÓN DE LA SUITE DE PRUEBAS               "
echo "================================================================================"

# 1. Compilación del proyecto completo
echo -e "\n[PASO 1] Compilando el código fuente del proyecto..."
mkdir -p bin
javac -d bin src/*.java
if [ $? -ne 0 ]; then
    echo "ERROR CRÍTICO: Falló la compilación de los archivos Java."
    exit 1
fi
echo "Compilación exitosa (0 errores de compilación Java)."

# 2. Ejecución automática sobre todos los .txt de la carpeta
TOTAL_PRUEBAS=0
PASADAS=0
FALLADAS=0
declare -a LISTA_FALLADAS=()

echo -e "\n================================================================================"
echo "  EJECUTANDO PRUEBAS EN: $CARPETA/"
echo "================================================================================"

while IFS= read -r archivo; do
    ((TOTAL_PRUEBAS++))

    ruta_lower=$(echo "$archivo" | tr '[:upper:]' '[:lower:]')
    nombre_lower=$(basename "$ruta_lower")

    # Deducir comportamiento esperado (prioridad: subcarpeta -> nombre de archivo)
    if [[ "$ruta_lower" == *"/invalidos/"* || "$ruta_lower" == *"/invalido/"* || "$ruta_lower" == *"/errores/"* || "$ruta_lower" == *"/invalida/"* ]]; then
        tipo="INVALIDO"
    elif [[ "$ruta_lower" == *"/validos/"* || "$ruta_lower" == *"/valido/"* ]]; then
        tipo="VALIDO"
    elif [[ "$nombre_lower" == *"invalido"* || "$nombre_lower" == *"error"* || "$nombre_lower" == *"fail"* || "$nombre_lower" == *"errores"* || "$nombre_lower" == *"invalidos"* || "$nombre_lower" == *"invalidas"* || "$ruta_lower" == *"/invalida/"* ]]; then
        tipo="INVALIDO"
    else
        tipo="VALIDO"
    fi

    # Ejecuta exactamente: ./compilador casos_de_prueba/archivo.txt
    SALIDA=$(./compilador "$archivo" 2>&1)
    RET_CODE=$?

    if [ "$tipo" == "VALIDO" ]; then
        if [ $RET_CODE -eq 0 ]; then
            echo -e "\033[0;32m[PASS]\033[0m ($tipo)   $archivo"
            ((PASADAS++))
        else
            echo -e "\033[0;31m[FAIL]\033[0m ($tipo)   $archivo -> Falló con código $RET_CODE"
            echo -e "   --- DETALLE DE SALIDA ---\n$SALIDA\n   -------------------------"
            ((FALLADAS++))
            LISTA_FALLADAS+=("$archivo (esperado: $tipo, código: $RET_CODE)")
        fi
    else
        if [ $RET_CODE -ne 0 ]; then
            echo -e "\033[0;32m[PASS]\033[0m ($tipo) $archivo"
            ((PASADAS++))
        else
            echo -e "\033[0;31m[FAIL]\033[0m ($tipo) $archivo -> Aceptado indebidamente (Código 0)"
            echo -e "   --- DETALLE DE SALIDA ---\n$SALIDA\n   -------------------------"
            ((FALLADAS++))
            LISTA_FALLADAS+=("$archivo (esperado: $tipo, código: $RET_CODE)")
        fi
    fi
done < <(find "$CARPETA" -type f -name "*.txt" | sort)

if [ $TOTAL_PRUEBAS -eq 0 ]; then
    echo -e "\033[0;33mAVISO: No se encontraron archivos .txt en '$CARPETA/'.\033[0m"
    exit 1
fi

# 3. Resumen final
echo -e "\n================================================================================"
echo "                             RESUMEN DE LA SUITE                                "
echo "================================================================================"
echo " Total de casos ejecutados : $TOTAL_PRUEBAS"
echo -e " Pruebas \033[0;32mPASADAS [PASS]\033[0m    : $PASADAS"
echo -e " Pruebas \033[0;31mFALLADAS [FAIL]\033[0m   : $FALLADAS"
echo "================================================================================"

if [ $FALLADAS -eq 0 ]; then
    echo -e "\033[0;32m¡TODAS LAS PRUEBAS SE EJECUTARON CON ÉXITO!\033[0m"
    exit 0
else
    echo -e "\033[0;31mPRUEBAS QUE FALLARON:\033[0m"
    for fallada in "${LISTA_FALLADAS[@]}"; do
        echo "  - $fallada"
    done
    exit 1
fi
