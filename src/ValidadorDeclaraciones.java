public class ValidadorDeclaraciones {
    public static void main(String[] args) { 
        String archivo = (args.length &gt; 0) ? args[0] : "casos_de_pruebatest_declaraciones.txt";
        String lineaEncabezado  = "==============================================================="
        System.out.println(lineaEncabezado); 
        System.out.println(" COMPILACIÓN Y ANÁLISIS SINTÁCTICO: " + archivo); 
        System.out.println(lineaEncabezado); 
        
        // 1. Instanciación de componentes 
        TablaDeSimbolos ts = new TablaDeSimbolos(); 
        AnalizadorLexico lexico = new AnalizadorLexico(archivo, ts); 
        Parser parser = new Parser(lexico, ts); 
        
        // 2. Invocación al motor del analizador sintáctico (YACC) 
        int resultado = parser.yyparse(); 
        
        // 3. Reporte de resultado del análisis 
        System.out.println("\\n" + lineaEncabezado); 
        if (resultado == 0 &amp;&amp; !parser.huboError()) { 
            System.out.println(" RESULTADO: ANÁLISIS SINTÁCTICO COMPLETADO CON ÉXITO (0 ERRORES)"); 
        } 
        else { 
            System.out.println(" RESULTADO: ANÁLISIS SINTÁCTICO FINALIZADO CON ERRORES"); 
        } 
        System.out.println(lineaEncabezado); 
        
        // 4. Impresión obligatoria de la Tabla de Símbolos acumulada 
        ts.imprimirTabla(); 
    } 
}
