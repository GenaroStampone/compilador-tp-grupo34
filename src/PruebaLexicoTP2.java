import java.util.HashSet;
import java.util.Set;

public class PruebaLexicoTP2 {

    public static void main(String[] args) {
        String archivoFuente = (args.length > 0) ? args[0] : "casos_de_prueba/caso_prueba_tp2.txt";
        System.out.println("==================================================================");
        System.out.println(" EJECUTANDO PRUEBA UNITARIA DEL ANALIZADOR LÉXICO (GRUPO 34 - TP2)");
        System.out.println(" Archivo a analizar: " + archivoFuente);
        System.out.println("==================================================================\n");

        TablaDeSimbolos ts = new TablaDeSimbolos();
        AnalizadorLexico lexico = new AnalizadorLexico(archivoFuente, ts);

        Set<Integer> tokensObservados = new HashSet<>();
        int token;
        int totalTokens = 0;
        int lineasAnteriores = 1;
        boolean errorDetectado = false;

        while ((token = lexico.yylex()) != 0) {
            totalTokens++;
            tokensObservados.add(token);

            // 1. Verificación de precisión del contador de línea (yylineno y getNroLinea())
            int lineaActual = lexico.yylineno;
            if (lineaActual != lexico.getNroLinea()) {
                System.err.printf("[FALLO] Desincronización de línea: yylineno=%d, getNroLinea()=%d%n",
                        lineaActual, lexico.getNroLinea());
                errorDetectado = true;
            }
            if (lineaActual < lineasAnteriores) {
                System.err.printf("[FALLO] Retroceso en contador de línea: anterior=%d, actual=%d%n",
                        lineasAnteriores, lineaActual);
                errorDetectado = true;
            }
            lineasAnteriores = lineaActual;

            // 2. Verificación de pasaje de atributos a yylval y referencia a TablaDeSimbolos
            if (token == AnalizadorLexico.ID || 
                token == AnalizadorLexico.CTE_ENTERA || 
                token == AnalizadorLexico.DOUBLEF_VAL || 
                token == AnalizadorLexico.CADENA) {

                if (lexico.yylval == null) {
                    System.err.printf("[FALLO Línea %d] Token %d sin yylval asignado!%n", lineaActual, token);
                    errorDetectado = true;
                } else if (!(lexico.yylval.obj instanceof RegistroTS)) {
                    System.err.printf("[FALLO Línea %d] yylval.obj no es instancia de RegistroTS para token %d!%n",
                            lineaActual, token);
                    errorDetectado = true;
                } else {
                    RegistroTS reg = (RegistroTS) lexico.yylval.obj;
                    if (reg.getLexema() == null || !reg.getLexema().equals(lexico.yylval.sval)) {
                        System.err.printf("[FALLO Línea %d] Inconsistencia entre yylval.sval y reg.getLexema()%n", lineaActual);
                        errorDetectado = true;
                    }
                    if (ts.buscar(reg.getLexema()) != reg) {
                        System.err.printf("[FALLO Línea %d] El registro en yylval no coincide con la entrada en Tabla de Símbolos: '%s'%n",
                                lineaActual, reg.getLexema());
                        errorDetectado = true;
                    }
                }
            }
        }

        System.out.println("Flujo continuo completado: Se consumieron " + totalTokens + " tokens hasta el EOF (código 0).");
        System.out.println("Última línea procesada: " + lexico.yylineno);

        // 3. Verificación de presencia de todos los tokens requeridos para el Grupo 34
        int[] tokensRequeridos = {
            // Tipos de datos
            AnalizadorLexico.USHORTINT,
            AnalizadorLexico.DOUBLEF,
            // Control
            AnalizadorLexico.WHILE,
            AnalizadorLexico.REPEAT,
            AnalizadorLexico.IF,
            AnalizadorLexico.THEN,
            AnalizadorLexico.ELSE,
            AnalizadorLexico.END_IF,
            // Palabras reservadas generales y del grupo
            AnalizadorLexico.BEGIN,
            AnalizadorLexico.END,
            AnalizadorLexico.POUT,
            AnalizadorLexico.RET,
            AnalizadorLexico.CLASS,
            AnalizadorLexico.FUNCTION,
            AnalizadorLexico.TYPEDEF,
            AnalizadorLexico.PRIVATE,
            AnalizadorLexico.FRIEND,
            AnalizadorLexico.EXTENDS,
            // Conversiones del grupo
            AnalizadorLexico.TODF,
            AnalizadorLexico.TOUS,
            // Comparadores
            AnalizadorLexico.MAYOR_IGUAL,
            AnalizadorLexico.MENOR_IGUAL,
            AnalizadorLexico.IGUAL_IGUAL,
            AnalizadorLexico.DISTINTO,
            '>',
            '<',
            // Símbolos
            AnalizadorLexico.ASIGNACION,
            '=',
            '(',
            ')',
            ',',
            ';',
            ':',
            '.',
            '[',
            ']',
            // Literales con atributo
            AnalizadorLexico.ID,
            AnalizadorLexico.CTE_ENTERA,
            AnalizadorLexico.DOUBLEF_VAL,
            AnalizadorLexico.CADENA
        };

        String[] nombresRequeridos = {
            "USHORTINT", "DOUBLEF",
            "WHILE", "REPEAT", "IF", "THEN", "ELSE", "END_IF",
            "BEGIN", "END", "POUT", "RET", "CLASS", "FUNCTION", "TYPEDEF", "PRIVATE", "FRIEND", "EXTENDS",
            "TODF", "TOUS",
            "MAYOR_IGUAL (>=)", "MENOR_IGUAL (<=)", "IGUAL_IGUAL (==)", "DISTINTO (!=)",
            "MAYOR (>)", "MENOR (<)",
            "ASIGNACION (:=)", "IGUAL (=)",
            "PAREN_ABRE '('", "PAREN_CIERRA ')'", "COMA ','", "PUNTO_Y_COMA ';'",
            "DOS_PUNTOS ':'", "PUNTO '.'", "CORCHETE_ABRE '['", "CORCHETE_CIERRA ']'",
            "ID", "CTE_ENTERA", "DOUBLEF_VAL", "CADENA"
        };

        boolean faltanTokens = false;
        System.out.println("\n--- VERIFICACIÓN DE TOKENS DEL GRUPO 34 ---");
        for (int i = 0; i < tokensRequeridos.length; i++) {
            int t = tokensRequeridos[i];
            String nombre = nombresRequeridos[i];
            if (tokensObservados.contains(t)) {
                System.out.printf("  [OK] %-25s detectado (código %d)%n", nombre, t);
            } else {
                System.err.printf("  [ERROR] %-25s NO fue detectado (código %d)%n", nombre, t);
                faltanTokens = true;
            }
        }

        System.out.println("\n--------------------------------------------------");
        if (!errorDetectado && !faltanTokens) {
            System.out.println(" RESULTADO FINAL: TODAS LAS PRUEBAS PASARON EXITOSAMENTE.");
            System.out.println(" El Léxico está 100% adaptado para el Parser del TP2.");
        } else {
            System.err.println(" RESULTADO FINAL: SE DETECTARON FALLOS EN LA PRUEBA.");
            System.exit(1);
        }
        System.out.println("--------------------------------------------------\n");
    }
}
