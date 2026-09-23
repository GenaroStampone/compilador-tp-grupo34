public class ValidadorDeclaraciones {
    public static void main(String[] args) {
        String archivo = (args.length > 0) ? args[0] : "casos_de_prueba/test_declaraciones.txt";
        System.out.println("===============================================================");
        System.out.println(" VALIDACIÓN SINTÁCTICA: " + archivo);
        System.out.println("===============================================================");

        TablaDeSimbolos ts = new TablaDeSimbolos();
        AnalizadorLexico lexico = new AnalizadorLexico(archivo, ts);
        Parser parser = new Parser(lexico, ts);

        int resultado = parser.yyparse();

        System.out.println("\n===============================================================");
        if (resultado == 0 && !parser.huboError()) {
            System.out.println(" RESULTADO: ANÁLISIS SINTÁCTICO COMPLETADO CON ÉXITO (0 ERRORES)");
        } else {
            System.err.println(" RESULTADO: FALLÓ EL ANÁLISIS SINTÁCTICO (Código: " + resultado + ")");
            System.exit(1);
        }
        System.out.println("===============================================================");
    }
}
