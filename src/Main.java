import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Compilador Grupo 34 - reentrega unificada TP1 a TP4.
 *
 * El parser coordina al analisis lexico bajo demanda y construye el AST con
 * validaciones semanticas. Si no hay errores, se genera codigo WebAssembly
 * textual en salida.wat.
 */
public class Main {

    private static final String ARCHIVO_WAT = "salida.wat";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("ERROR: Debe indicar la ruta del archivo fuente.");
            System.err.println("Uso: ./compilador casos_de_prueba/01_prueba_correcto.txt");
            System.exit(1);
        }

        String rutaArchivo = args[0];
        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || !archivo.isFile()) {
            System.err.println("ERROR: El archivo indicado no existe o no es un archivo valido: " + rutaArchivo);
            System.exit(1);
        }

        Path rutaFuente = archivo.toPath().toAbsolutePath().normalize();
        System.out.println("===================================================================================");
        System.out.println("              COMPILADOR GRUPO 34 - REENTREGA UNIFICADA TP1 A TP4                  ");
        System.out.println("===================================================================================");
        System.out.println(" Archivo fuente : " + rutaFuente);
        System.out.println(" Arquitectura   : Parser -> Lexico bajo demanda -> AST/Semantica -> WebAssembly");
        System.out.println("===================================================================================\n");

        TablaDeSimbolos tablaDeSimbolos = new TablaDeSimbolos();
        AnalizadorLexico lexico;
        try {
            lexico = new AnalizadorLexico(rutaArchivo, tablaDeSimbolos);
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo abrir el archivo fuente: " + e.getMessage());
            System.exit(1);
            return;
        }
        Parser parser = new Parser(lexico, tablaDeSimbolos);
        parser.setModoSilencioso(true);

        int resultadoParser = 0;
        Exception excepcionParser = null;
        try {
            resultadoParser = parser.yyparse();
        } catch (Exception e) {
            excepcionParser = e;
        } finally {
            lexico.cerrar();
        }

        imprimirTokens(lexico);
        imprimirEstructuras(parser);

        List<String> erroresLexicos = new ArrayList<>(lexico.getErrores());
        List<String> erroresSintacticos = new ArrayList<>(parser.getErroresSintacticos());
        List<String> erroresSemanticos = new ArrayList<>(parser.getGenerador().getErroresSemanticos());
        List<String> warnings = new ArrayList<>(lexico.getWarnings());
        imprimirErrores(warnings, erroresLexicos, erroresSintacticos, erroresSemanticos, excepcionParser);

        imprimirAST(parser);
        tablaDeSimbolos.imprimirTabla();
        imprimirFuenteNumerada(rutaFuente);

        boolean hayErrores = resultadoParser != 0
                || parser.huboError()
                || !erroresLexicos.isEmpty()
                || !erroresSintacticos.isEmpty()
                || !erroresSemanticos.isEmpty()
                || excepcionParser != null;

        boolean watGenerado = false;
        if (!hayErrores) {
            try {
                GeneradorWebAssembly generadorWat = new GeneradorWebAssembly(
                        tablaDeSimbolos,
                        parser.getCuerposFunciones(),
                        parser.getGenerador().getRaizAST());
                String codigoWat = generadorWat.generar();
                Path salidaWat = Paths.get(ARCHIVO_WAT).toAbsolutePath().normalize();
                Files.write(salidaWat, codigoWat.getBytes(StandardCharsets.UTF_8));
                System.out.println("===================================================================================");
                System.out.println("                       GENERACION DE CODIGO WEBASSEMBLY                            ");
                System.out.println("===================================================================================");
                System.out.println(" Archivo generado : " + salidaWat);
                System.out.printf(" Tamano           : %d bytes%n", codigoWat.getBytes(StandardCharsets.UTF_8).length);
                System.out.println(" Formato          : WebAssembly textual (.wat)");
                System.out.println("===================================================================================\n");
                watGenerado = true;
            } catch (IOException | RuntimeException e) {
                hayErrores = true;
                System.err.println("ERROR: No se pudo generar el codigo WebAssembly: " + e.getMessage());
            }
        }

        int totalErrores = erroresLexicos.size() + erroresSintacticos.size()
                + erroresSemanticos.size() + (excepcionParser != null ? 1 : 0);

        System.out.println("===================================================================================");
        System.out.printf(" RESUMEN FINAL: %d error(es) | %d advertencia(s) | WAT: %s%n",
                totalErrores, warnings.size(), watGenerado ? "generado" : "no generado");
        System.out.printf(" Detalle: %d lexico(s), %d sintactico(s), %d semantico(s)%n",
                erroresLexicos.size(), erroresSintacticos.size(), erroresSemanticos.size());
        if (hayErrores) {
            System.out.println(" RESULTADO: COMPILACION CON ERRORES");
            System.out.println("===================================================================================");
            System.exit(1);
        }
        System.out.println(" RESULTADO: COMPILACION EXITOSA (0 ERRORES)");
        System.out.println("===================================================================================");
        System.exit(0);
    }

    private static void imprimirTokens(AnalizadorLexico lexico) {
        System.out.println("===================================================================================");
        System.out.println("                   TIRA DE TOKENS PROCESADOS POR EL PARSER                         ");
        System.out.println("===================================================================================");
        List<Token> tokens = lexico.getTokensDetectados();
        for (Token token : tokens) {
            System.out.println("  " + token);
        }
        System.out.printf("Total de tokens: %d%n%n", tokens.size());
    }

    private static void imprimirEstructuras(Parser parser) {
        System.out.println("===================================================================================");
        System.out.println("                       ESTRUCTURAS SINTACTICAS DETECTADAS                          ");
        System.out.println("===================================================================================");
        List<String> estructuras = parser.getEstructurasDetectadas();
        if (estructuras.isEmpty()) {
            System.out.println("  (No se registraron estructuras sintacticas)");
        } else {
            for (String estructura : estructuras) {
                System.out.println("  [OK] " + estructura);
            }
        }
        System.out.printf("Total de estructuras: %d%n%n", estructuras.size());
    }

    private static void imprimirErrores(List<String> warnings, List<String> erroresLexicos,
            List<String> erroresSintacticos, List<String> erroresSemanticos, Exception excepcionParser) {
        System.out.println("===================================================================================");
        System.out.println("             ERRORES Y ADVERTENCIAS DE COMPILACION (TODOS JUNTOS)                  ");
        System.out.println("===================================================================================");

        if (warnings.isEmpty() && erroresLexicos.isEmpty() && erroresSintacticos.isEmpty()
                && erroresSemanticos.isEmpty() && excepcionParser == null) {
            System.out.println("  No se detectaron errores ni advertencias.");
            System.out.println();
            return;
        }

        for (String warning : warnings) {
            System.out.println("  [ADVERTENCIA] " + warning);
        }
        for (String error : erroresLexicos) {
            System.out.println("  [ERROR LEXICO] " + error);
        }
        for (String error : erroresSintacticos) {
            System.out.println("  [ERROR SINTACTICO] " + error);
        }
        for (String error : erroresSemanticos) {
            System.out.println("  [ERROR SEMANTICO] " + error);
        }
        if (excepcionParser != null) {
            System.out.println("  [ERROR INTERNO] " + excepcionParser.getMessage());
        }
        System.out.println();
    }

    private static void imprimirAST(Parser parser) {
        System.out.println("===================================================================================");
        System.out.println("               REPRESENTACION INTERMEDIA: ARBOL SINTACTICO ABSTRACTO               ");
        System.out.println("===================================================================================");
        parser.getGenerador().imprimirAST();
        System.out.println();
    }

    private static void imprimirFuenteNumerada(Path rutaFuente) {
        System.out.println("===================================================================================");
        System.out.println("                         FUENTE CON NUMERACION DE LINEAS                            ");
        System.out.println("===================================================================================");
        System.out.println(" Archivo: " + rutaFuente);
        try {
            List<String> lineas = Files.readAllLines(rutaFuente, StandardCharsets.UTF_8);
            for (int i = 0; i < lineas.size(); i++) {
                System.out.printf("%4d | %s%n", i + 1, lineas.get(i));
            }
        } catch (IOException e) {
            System.out.println("  No se pudo releer el archivo fuente: " + e.getMessage());
        }
        System.out.println();
    }
}
