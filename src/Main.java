import java.io.File;
import java.util.List;

/**
 * Clase Principal del Compilador (Grupo 34 - TP2)
 *
 * Implementa la arquitectura monolítica donde el Analizador Sintáctico (Parser)
 * coordina la ejecución bajo demanda solicitándole tokens al Analizador Léxico
 * a medida que los necesita para validar la gramática del programa fuente.
 */
public class Main {

    public static void main(String[] args) {
        // ---------------------------------------------------------------------
        // 1. VALIDACIÓN DE ARGUMENTOS POR LÍNEA DE COMANDOS
        // ---------------------------------------------------------------------
        if (args.length < 1) {
            System.err.println("===================================================================================");
            System.err.println(" ERROR: Debe pasar la ruta del archivo de código fuente como parámetro.");
            System.err.println(" Ejemplo de uso: ./compilador casos_de_prueba/prueba_valida_g34.txt");
            System.err.println("              o: java -cp bin Main casos_de_prueba/prueba_valida_g34.txt");
            System.err.println("===================================================================================");
            System.exit(1);
        }

        String rutaArchivo = args[0];
        File archivo = new File(rutaArchivo);

        if (!archivo.exists() || !archivo.isFile()) {
            System.err.println("===================================================================================");
            System.err.println(" ERROR: El archivo especificado no existe o no es válido: " + rutaArchivo);
            System.err.println("===================================================================================");
            System.exit(1);
        }

        System.out.println("===================================================================================");
        System.out.println("                   COMPILADOR GRUPO 34 - CICLO LECTIVO 2026                        ");
        System.out.println("===================================================================================");
        System.out.println(" Archivo fuente a compilar : " + archivo.getAbsolutePath());
        System.out.println(" Modo de ejecución         : Arquitectura Monolítica (Parser -> Léxico bajo demanda)");
        System.out.println("===================================================================================\n");

        // ---------------------------------------------------------------------
        // 2. INICIALIZACIÓN DE ESTRUCTURAS Y COMPONENTES MONOLÍTICOS
        // ---------------------------------------------------------------------
        // Inicializa la Tabla de Símbolos donde se almacenarán Identificadores, Literales y Palabras Reservadas
        TablaDeSimbolos tablaDeSimbolos = new TablaDeSimbolos();

        // Inicializa el Analizador Léxico con el contador de líneas y estructuras de errores/warnings
        AnalizadorLexico lexico = new AnalizadorLexico(rutaArchivo, tablaDeSimbolos);

        // Inicializa el Analizador Sintáctico (Parser) vinculándolo con el Léxico y la Tabla de Símbolos
        Parser parser = new Parser(lexico, tablaDeSimbolos);

        // ---------------------------------------------------------------------
        // 3. EJECUCIÓN MONOLÍTICA COORDINADA POR EL PARSER
        // ---------------------------------------------------------------------
        System.out.println(">>> INICIANDO PROCESO DE PARSING Y ANÁLISIS SINTÁCTICO / LÉXICO <<<\n");

        int resultadoParser = parser.yyparse();

        // Cierra los descriptores de lectura del archivo fuente
        lexico.cerrar();

        System.out.println("\n>>> FASE DE PARSING FINALIZADA <<<\n");

        // ---------------------------------------------------------------------
        // 4. MUESTRA POR PANTALLA: TOKENS CONSUMIDOS POR EL PARSER
        // ---------------------------------------------------------------------
        System.out.println("===================================================================================");
        System.out.println("                   TIRA DE TOKENS PROCESADOS POR EL PARSER                         ");
        System.out.println("===================================================================================");
        List<Token> tiraTokens = lexico.getTokensDetectados();
        for (Token t : tiraTokens) {
            System.out.println("  " + t);
        }
        System.out.printf("Total de tokens procesados bajo demanda: %d%n", tiraTokens.size());

        // ---------------------------------------------------------------------
        // 5. MUESTRA POR PANTALLA: ESTRUCTURAS SINTÁCTICAS DETECTADAS
        // ---------------------------------------------------------------------
        System.out.println("\n===================================================================================");
        System.out.println("                       ESTRUCTURAS SINTÁCTICAS DETECTADAS                          ");
        System.out.println("===================================================================================");
        List<String> estructuras = parser.getEstructurasDetectadas();
        if (estructuras.isEmpty()) {
            System.out.println("  (No se registraron reducciones de estructuras sintácticas)");
        } else {
            for (String est : estructuras) {
                System.out.println("  [OK] " + est);
            }
        }
        System.out.printf("Total de estructuras sintácticas identificadas: %d%n", estructuras.size());

        // ---------------------------------------------------------------------
        // 6. MUESTRA POR PANTALLA: LISTADO FORMATEADO DE ERRORES Y ADVERTENCIAS
        // ---------------------------------------------------------------------
        System.out.println("\n===================================================================================");
        System.out.println("             LISTADO FORMATEADO DE ERRORES Y ADVERTENCIAS DETECTADOS               ");
        System.out.println("===================================================================================");

        List<String> warningsLexicos = lexico.getWarnings();
        List<String> erroresLexicos = lexico.getErrores();
        List<String> erroresSintacticos = parser.getErroresSintacticos();

        int totalErrores = erroresLexicos.size() + erroresSintacticos.size();
        int totalWarnings = warningsLexicos.size();

        if (totalWarnings > 0) {
            System.out.println("\n--- ADVERTENCIAS LÉXICAS (" + totalWarnings + ") ---");
            for (String w : warningsLexicos) {
                System.out.println("  [ADVERTENCIA] " + w);
            }
        }

        if (erroresLexicos.size() > 0) {
            System.out.println("\n--- ERRORES LÉXICOS (" + erroresLexicos.size() + ") ---");
            for (String e : erroresLexicos) {
                System.out.println("  [ERROR LÉXICO] " + e);
            }
        }

        if (erroresSintacticos.size() > 0) {
            System.out.println("\n--- ERRORES SINTÁCTICOS (" + erroresSintacticos.size() + ") ---");
            for (String s : erroresSintacticos) {
                System.out.println("  [ERROR SINTÁCTICO] " + s);
            }
        }

        if (totalErrores == 0 && totalWarnings == 0) {
            System.out.println("  ¡Excelente! No se detectaron errores léxicos ni sintácticos, ni advertencias.");
        } else if (totalErrores == 0) {
            System.out.println("  Programa sintácticamente válido. Se registraron únicamente advertencias (warnings).");
        }

        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.printf(" RESUMEN: %d Error(es) (%d Léxico(s), %d Sintáctico(s)) | %d Advertencia(s)%n",
                totalErrores, erroresLexicos.size(), erroresSintacticos.size(), totalWarnings);
        System.out.println("===================================================================================");

        // ---------------------------------------------------------------------
        // 7. MUESTRA POR PANTALLA: ESTADO FINAL DE LA TABLA DE SÍMBOLOS
        // ---------------------------------------------------------------------
        tablaDeSimbolos.imprimirTabla();

        // ---------------------------------------------------------------------
        // 8. VEREDICTO FINAL DE COMPILACIÓN
        // ---------------------------------------------------------------------
        boolean compilacionExitosa = (resultadoParser == 0) && !parser.huboError() && erroresLexicos.isEmpty();

        System.out.println("===================================================================================");
        if (compilacionExitosa) {
            System.out.println("                >>> RESULTADO FINAL: COMPILACIÓN EXITOSA (0 ERRORES) <<<           ");
            System.out.println(" El programa fuente fue procesado correctamente y cumple con todas las reglas.");
            System.out.println("===================================================================================\n");
            System.exit(0);
        } else {
            System.err.println("                >>> RESULTADO FINAL: COMPILACIÓN CON ERRORES <<<                   ");
            System.err.println(" Se detectaron inconsistencias léxicas o sintácticas durante la compilación.");
            System.err.println("===================================================================================\n");
            System.exit(1);
        }
    }
}