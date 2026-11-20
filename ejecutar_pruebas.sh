#!/bin/bash
# ==============================================================================
# SUITE DE PRUEBAS AUTOMATIZADA - COMPILADOR GRUPO 34 (TP2)
# ==============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -d "$SCRIPT_DIR/compilador-tp-grupo34" ]; then
    cd "$SCRIPT_DIR/compilador-tp-grupo34" || exit 1
else
    cd "$SCRIPT_DIR" || exit 1
fi

echo "================================================================================"
echo "         INICIANDO COMPILACIÓN Y EJECUCIÓN DE LA SUITE DE PRUEBAS               "
echo "================================================================================"

# 1. Compilación del proyecto completo
echo -e "\n[PASO 1] Compilando el código fuente del proyecto..."
javac -d bin src/*.java
if [ $? -ne 0 ]; then
    echo "ERROR CRÍTICO: Falló la compilación de los archivos Java."
    exit 1
fi
echo "Compilación exitosa (0 errores de compilación Java)."

# 2. Definición de casos de prueba y su resultado esperado
# Formato: ARCHIVO | TIPO_ESPERADO (VALIDO=0 / INVALIDO=1) | DESCRIPCION
declare -a PRUEBAS=(
    "casos_de_prueba/01_prueba_correcto.txt:VALIDO:Prueba facil de sintaxis general y constantes"
    "casos_de_prueba/02_prueba_warnings_id.txt:VALIDO:Prueba facil de truncado de identificadores"
    "casos_de_prueba/03_prueba_errores_constantes.txt:INVALIDO:Prueba facil de errores de rango numerico"
    "casos_de_prueba/04_prueba_errores_simbolos.txt:INVALIDO:Prueba de caracteres invalidos"
    "casos_de_prueba/05_prueba_cadenas_y_comentarios.txt:VALIDO:Prueba de cadenas POUT y comentarios multilinea cerrados correctamente"
    "casos_de_prueba/06_prueba_errores_sintacticos_base.txt:INVALIDO:Prueba de errores sintacticos simples y recuperacion con ';'"
    "casos_de_prueba/07_programa_integral_valido.txt:VALIDO:Programa complejo integral con los 12 temas"
    "casos_de_prueba/08_programa_complejo_errores.txt:INVALIDO:Programa complejo con errores sintacticos resincronizados"
    "casos_de_prueba/09_prueba_declaraciones_y_poo.txt:VALIDO:Programa complejo de declaraciones, TYPEDEF, clases y herencia"
)

TOTAL_PRUEBAS=${#PRUEBAS[@]}
PASADAS=0
FALLADAS=0

echo -e "\n================================================================================"
echo "                   EJECUTANDO CASOS DE PRUEBA DEL COMPILADOR                    "
echo "================================================================================"

for item in "${PRUEBAS[@]}"; do
    IFS=":" read -r archivo tipo desc <<< "$item"
    echo -e "\n--------------------------------------------------------------------------------"
    echo "PRUEBA: $archivo"
    echo "DESCRIPCIÓN: $desc"
    echo "COMPORTAMIENTO ESPERADO: $tipo"
    echo "--------------------------------------------------------------------------------"

    # Ejecutar el compilador guardando la salida en una variable
    SALIDA=$(./compilador "$archivo" 2>&1)
    RET_CODE=$?

    if [ "$tipo" == "VALIDO" ]; then
        if [ $RET_CODE -eq 0 ]; then
            echo -e ">>> RESULTADO: \033[0;32m[PASS]\033[0m - El programa válido finalizó correctamente (Código 0)."
            ((PASADAS++))
        else
            echo -e ">>> RESULTADO: \\033[0;31m[FAIL]\033[0m - El programa válido falló con código $RET_CODE."
            echo -e "--- DETALLE DE SALIDA DEL COMPILADOR ---\n$SALIDA\n---------------------------------------"
            ((FALLADAS++))
        fi
    else
        if [ $RET_CODE -ne 0 ]; then
            echo -e ">>> RESULTADO: \033[0;32m[PASS]\033[0m - El programa inválido reportó adecuadamente los errores (Código $RET_CODE)."
            ((PASADAS++))
        else
            echo -e ">>> RESULTADO: \033[0;31m[FAIL]\033[0m - El programa inválido fue aceptado indebidamente (Código 0)."
            echo -e "--- DETALLE DE SALIDA DEL COMPILADOR ---\\n$SALIDA\\n---------------------------------------"
            ((FALLADAS++))
        fi
    fi
done

# Resumen final de ejecucion
echo -e "\n================================================================================"
echo "                             RESUMEN DE LA SUITE                                "
echo "================================================================================"
echo " Total de casos ejecutados : $TOTAL_PRUEBAS"
echo " Pruebas \033[0;32mPASADAS [PASS]\033[0m: $PASADAS"
echo " Pruebas \033[0;31mFALLADAS [FAIL]\033[0m: $FALLADAS"
echo "================================================================================"

if [ $FALLADAS -eq 0 ]; then
    echo -e "\033[0;32m¡TODAS LAS PRUEBAS SE EJECUTARON CON ÉXITO!\033[0m"
    exit 0
else
    echo -e "\033[0;31mSE DETECTARON FALLAS EN ALGUNAS PRUEBAS DE LA SUITE.\033[0m"
    exit 1
fi
