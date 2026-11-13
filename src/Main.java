public class Main {

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("            COMPILADOR - GRUPO 34 (ANÁLISIS SINTÁCTICO)            ");
        System.out.println("===================================================================\n");

        // 1. Verificación del archivo de entrada recibido por consola
        if (args.length == 0) {
            System.err.println("Error: Debe especificar la ruta del archivo fuente a compilar.");
            System.err.println("Uso: java Main <ruta_archivo_fuente.txt>");
            return;
        }

        String rutaArchivo = args[0];
        System.out.println("Cargando archivo fuente: " + rutaArchivo + "...\n");

        // 2. Instanciación de las estructuras principales del compilador
        TablaDeSimbolos ts = new TablaDeSimbolos();
        AnalizadorLexico lexico = new AnalizadorLexico(rutaArchivo, ts);
        Parser parser = new Parser(lexico, ts);
            
        // 3. Ejecución del Analizador Sintáctico (Parser toma el control)
        System.out.println("--- INICIO DEL ANÁLISIS SINTÁCTICO ---");
        int resultado = parser.yyparse();
        System.out.println("--- FIN DEL ANÁLISIS SINTÁCTICO ---\n");

        // 4. Informe de estado del análisis
        if (resultado == 0 && !parser.huboError()) {
            System.out.println("RESULTADO: El archivo fuente es SINTÁCTICAMENTE CORRECTO.");
        } else {
            System.err.println("RESULTADO: Se detectaron errores sintácticos durante la compilación.");
        }

        // 5. Impresión final del contenido de la Tabla de Símbolos
        ts.imprimirTabla();
    }
}