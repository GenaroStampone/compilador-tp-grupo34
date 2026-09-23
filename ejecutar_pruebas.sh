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
    "casos_de_prueba/prueba_valida_g34.txt:VALIDO:Programa integral con todos los temas asignados al Grupo 34"
    "casos_de_prueba/test_declaraciones.txt:VALIDO:Declaraciones de clases, herencia múltiple, private, friend, typedef y funciones"
    "casos_de_prueba/1_prueba_correcto.txt:VALIDO:Programa básico correcto (0 errores y 0 warnings)"
    "casos_de_prueba/2_prueba_warnings_id.txt:VALIDO:Programa válido con advertencias de identificadores truncados a 22 chars"
    "casos_de_prueba/prueba_errores_g34.txt:INVALIDO:Errores léxicos y sintácticos con recuperación en punto y coma (;)"
    "casos_de_prueba/3_prueba_errores_constantes.txt:INVALIDO:Errores léxicos por constantes fuera de rango numérico"
    "casos_de_prueba/4_prueba_errores_simbolos.txt:INVALIDO:Errores léxicos por caracteres/símbolos ilegales"
    "casos_de_prueba/5_prueba_cadenas_y_comentarios.txt:INVALIDO:Errores léxicos de cadena multilínea y comentario no cerrado"
    "casos_de_prueba/caso_prueba_tp2.txt:INVALIDO:Caso léxico con sintaxis inválida para parsing"
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

    # Ejecutar el compilador
    ./compilador "$archivo" > /dev/null 2>&1
    RET_CODE=$?

    if [ "$tipo" == "VALIDO" ]; then
        if [ $RET_CODE -eq 0 ]; then
            echo -e ">>> RESULTADO: [PASS] - El programa válido finalizó correctamente (Código 0)."
            ((PASADAS++))
        else
            echo -e ">>> RESULTADO: [FAIL] - El programa válido falló con código $RET_CODE."
            ((FALLADAS++))
        fi
    else
        if [ $RET_CODE -ne 0 ]; then
            echo -e ">>> RESULTADO: [PASS] - El programa inválido reportó adecuadamente los errores (Código $RET_CODE)."
            ((PASADAS++))
        else
            echo -e ">>> RESULTADO: [FAIL] - El programa inválido fue aceptado indebidamente (Código 0)."
            ((FALLADAS++))
        fi
    fi
done

# 3. Verificación de Prueba Léxica Unitaria
echo -e "\n--------------------------------------------------------------------------------"
echo "PRUEBA UNITARIA: PruebaLexicoTP2 (Verificación exhaustiva de 40 tokens)"
echo "--------------------------------------------------------------------------------"
java -cp bin PruebaLexicoTP2 > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo ">>> RESULTADO: [PASS] - Todos los tokens terminales del Grupo 34 fueron validados."
else
    echo ">>> RESULTADO: [FAIL] - Falló la prueba unitaria del Léxico."
    ((FALLADAS++))
fi

echo -e "\n================================================================================"
echo "                             RESUMEN DE LA SUITE                                "
echo "================================================================================"
echo " Total de casos ejecutados : $TOTAL_PRUEBAS"
echo " Casos aprobados (PASS)    : $PASADAS"
echo " Casos reprobados (FAIL)   : $FALLADAS"
echo "================================================================================"

if [ $FALLADAS -eq 0 ]; then
    echo " >>> SUITE DE PRUEBAS COMPLETADA CON 100% DE ÉXITO <<< "
    exit 0
else
    echo " >>> SE DETECTARON FALLOS EN LA SUITE DE PRUEBAS <<< "
    exit 1
fi
