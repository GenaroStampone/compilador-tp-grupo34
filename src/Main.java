
import java.io.File; // Importa la clase File para verificar la existencia del archivo de código fuente

public class Main {

    // Punto de entrada principal de la aplicación de prueba
    public static void main(String[] args) {
        
        // ---------------------------------------------------------------------
        // 1. VALIDACIÓN DE PARÁMETROS Y ARCHIVO DE ENTRADA
        // ---------------------------------------------------------------------
        
        // Controla que el usuario haya pasado la ruta del archivo como argumento por consola
        if (args.length < 1) {
            System.err.println("ERROR: Debe pasar la ruta del archivo de código fuente como parámetro.");
            System.out.println("Uso: java Main <ruta_del_archivo>");
            return; // Interrumpe la ejecución si no se proporcionó el argumento
        }

        // Toma la primera posición del arreglo de argumentos como la ruta del archivo fuente
        String rutaArchivo = args[0];
        
        // Crea un objeto File para manipular y verificar la información del archivo en disco
        File archivo = new File(rutaArchivo);

        // Verifica si el archivo especificado realmente existe en la ruta indicada
        if (!archivo.exists()) {
            System.err.println("ERROR: El archivo especificado no existe en la ruta: " + rutaArchivo);
            return; // Cancela la ejecución si el archivo no existe
        }

        // Imprime un encabezado informativo con el nombre del archivo cargado
        System.out.println("=======================================================");
        System.out.println(" Iniciando análisis léxico de: " + archivo.getName());
        System.out.println("=======================================================\n");

        // ---------------------------------------------------------------------
        // 2. INICIALIZACIÓN DE COMPONENTES DEL COMPILADOR
        // ---------------------------------------------------------------------
        
        // Crea la instancia de la Tabla de Símbolos donde se registrarán los IDs, constantes y cadenas
        TablaDeSimbolos tablaDeSimbolos = new TablaDeSimbolos();
        
        // Instancia el Analizador Léxico pasándole el archivo a leer y la Tabla de Símbolos asociada
        AnalizadorLexico lexico = new AnalizadorLexico(rutaArchivo, tablaDeSimbolos);

        System.out.println("--- TIRA DE TOKENS DETECTADOS ---");

        // ---------------------------------------------------------------------
        // 3. BUCLE PRINCIPAL DE CONSUMO DE TOKENS (SIMULACIÓN DE YACC / PARSER)
        // ---------------------------------------------------------------------
        
        int token;             // Variable donde se almacenará el código entero del token devuelto por yylex()
        int cantidadTokens = 0; // Contador global de tokens válidos reconocidos

        // El bucle invoca a yylex() en cada iteración y se repite MIENTRAS NO se llegue al Fin de Archivo (EOF = 0)
        while ((token = lexico.yylex()) != 0) { 
            cantidadTokens++; // Incrementa el contador de tokens procesados
            
            // Traduce el entero numérico del token a una etiqueta legible (ej. "ID", "CTE_ENTERA", "PR_IF")
            String nombreToken = obtenerNombreToken(token);
            
            // Variable auxiliar para mostrar el atributo o lexema si el token posee uno
            String atributo = "";
            
            // Evalúa si yylex() asignó un atributo en la variable pública yylval (usada para la comunicación con Yacc)
            if (lexico.yylval != null && lexico.yylval.sval != null) {
                atributo = " | Atributo/Lexema: '" + lexico.yylval.sval + "'"; // Formatea el lexema recuperado
            }

            // Imprime en consola la línea actual del código fuente, el código del token, su nombre y su atributo
            System.out.printf("Línea %-3d | Token ID: %-4d (%-15s)%s%n",
                    lexico.getNroLinea(), token, nombreToken, atributo);
        }

        // ---------------------------------------------------------------------
        // 4. IMPRESIÓN DE RESULTADOS Y TABLA DE SÍMBOLOS
        // ---------------------------------------------------------------------
        
        System.out.println("\n--- RESUMEN DEL ANÁLISIS ---");
        // Muestra la cantidad total de tokens entregados al Parser antes del EOF
        System.out.println("Total de tokens válidos procesados: " + cantidadTokens);

        // Imprime en pantalla el reporte con todos los registros acumulados en la Tabla de Símbolos
        tablaDeSimbolos.imprimirTabla();

        System.out.println("\nProceso de análisis léxico finalizado con éxito.");
    }

    // -------------------------------------------------------------------------
    // MÉTODOS AUXILIARES
    // -------------------------------------------------------------------------

    /**
     * Función que mapea los códigos numéricos de los tokens devueltos por yylex()
     * a cadenas de texto descriptivas para facilitar la lectura en la consola.
     */
    private static String obtenerNombreToken(int token) {
        switch (token) {
            // --- Tokens con atributos/lexemas asociados ---
            case AnalizadorLexico.ID:            return "ID";
            case AnalizadorLexico.CTE_ENTERA:    return "CTE_ENTERA";
            case AnalizadorLexico.DOUBLEF_VAL:   return "DOUBLEF_VAL";
            case AnalizadorLexico.CADENA:        return "CADENA";

            // --- Operadores compuestos y comparadores ---
            case AnalizadorLexico.MAYOR_IGUAL:   return "MAYOR_IGUAL (>=)";
            case AnalizadorLexico.MENOR_IGUAL:   return "MENOR_IGUAL (<=)";
            case AnalizadorLexico.IGUAL_IGUAL:   return "IGUAL_IGUAL (==)";
            case AnalizadorLexico.DISTINTO:      return "DISTINTO (!=)";
            case AnalizadorLexico.ASIGNACION:    return "ASIGNACION (:=)";

            // --- Palabras Reservadas (PR) ---
            case AnalizadorLexico.IF:            return "PR_IF";
            case AnalizadorLexico.ELSE:          return "PR_ELSE";
            case AnalizadorLexico.END_IF:        return "PR_END_IF";
            case AnalizadorLexico.BEGIN:         return "PR_BEGIN";
            case AnalizadorLexico.END:           return "PR_END";
            case AnalizadorLexico.POUT:          return "PR_POUT";
            case AnalizadorLexico.RET:           return "PR_RET";
            case AnalizadorLexico.CLASS:         return "PR_CLASS";
            case AnalizadorLexico.FUNCTION:      return "PR_FUNCTION";
            case AnalizadorLexico.USHORTINT:     return "PR_USHORTINT";
            case AnalizadorLexico.DOUBLEF:       return "PR_DOUBLEF";

            // --- Caracteres literales simples ---
            // Si el código es menor a 256, representa un carácter ASCII individual (como '*', '/', '(', ')', ';', etc.)
            default:
                if (token > 0 && token < 256) {
                    return "LITERAL '" + (char) token + "'"; // Convierte el número ASCII al carácter original
                }
                return "TOKEN_DESCONOCIDO"; // En caso de que no coincida con ningún código esperado
        }
    }
}