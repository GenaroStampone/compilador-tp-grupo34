import java.util.List;

public class ValidadorDeclaraciones {
    public static void main(String[] args) { 
        String archivo = (args.length > 0) ? args[0] : "casos_de_prueba/test_declaraciones.txt";
        String lineaEncabezado  = "===============================================================";
        
        System.out.println(lineaEncabezado); 
        System.out.println(" COMPILACIÓN Y ANÁLISIS SINTÁCTICO: " + archivo); 
        System.out.println(lineaEncabezado); 
        
        // Instanciación de componentes 
        TablaDeSimbolos ts = new TablaDeSimbolos(); 
        AnalizadorLexico lexico = new AnalizadorLexico(archivo, ts); 
        Parser parser = new Parser(lexico, ts); 
        
        // Invocación al motor del analizador sintáctico (YACC) 
        int resultado = parser.yyparse(); 

        // Cierre del lector de archivos
        lexico.cerrar();
        
        // Recolección de advertencias y errores (léxicos y sintácticos)
        List<String> warningsLexicos = lexico.getWarnings();
        List<String> erroresLexicos = lexico.getErrores();

        if (!warningsLexicos.isEmpty()) {
            System.out.println("\n--- ADVERTENCIAS LÉXICAS (" + warningsLexicos.size() + ") ---");
            for (String w : warningsLexicos) {
                System.out.println(" [ADVERTENCIA] " + w);
            }
        }

        if (!erroresLexicos.isEmpty()) {
            System.out.println("\n--- ERRORES LÉXICOS (" + erroresLexicos.size() + ") ---");
            for (String e : erroresLexicos) {
                System.out.println(" [ERROR LÉXICO] " + e);
            }
        }

        // Reporte de resultado del análisis
        boolean huboErrorSintactico = (resultado != 0) || parser.huboError();
        boolean sinErrores = !huboErrorSintactico && erroresLexicos.isEmpty();

        System.out.println("\n" + lineaEncabezado);
        if (sinErrores) {
            System.out.println(" RESULTADO: ANÁLISIS COMPLETADO CON ÉXITO (0 ERRORES)");
        } else {
            System.out.println(" RESULTADO: ANÁLISIS FINALIZADO CON ERRORES ("
                    + erroresLexicos.size() + " léxico(s), "
                    + " | Sintácticos: " + (huboErrorSintactico ? "SÍ" : "NO") + ")");
        }
        System.out.println(lineaEncabezado);

        // Impresión obligatoria de la Tabla de Símbolos acumulada
        ts.imprimirTabla(); 

        System.exit(sinErrores ? 0 : 1);
    } 
}
