import java.util.ArrayList;
import java.util.List;
import java.io.File; // Importamos la clase File para manejar archivos

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("ERROR: Debe pasar la ruta del archivo de codigo fuente como parametro.");
            return;
        }
        String rutaArchivo = args[0];
        File archivo = new File(rutaArchivo);

        if (!archivo.exists()) {
            System.out.println("ERROR: El archivo especificado no existe.");
            return;
        }

        System.out.println("Iniciando compilacion del archivo: " + archivo.getName());

        TablaDeSimbolos tablaDeSimbolos = new TablaDeSimbolos();
        AnalizadorLexico lexico = new AnalizadorLexico(rutaArchivo, tablaDeSimbolos);
        List<Token> tiraDeTokens = new ArrayList<>();

        Token tokenActual;
        do {
            tokenActual = lexico.obtenerSiguienteToken();
            if (tokenActual != null) {
                tiraDeTokens.add(tokenActual);
            }
        } while (tokenActual != null && tokenActual.getIdToken() != AnalizadorLexico.Token_EOF);

        System.out.println("\n======================================================="); 
        System.out.println("               TIRA DE TOKENS DETECTADOS               "); 
        System.out.println("=======================================================");
        for (Token t : tiraDeTokens) {
            System.out.println(t);
        }

        lexico.imprimirReporteErroresYWarnings();

        tablaDeSimbolos.imprimirTabla();

        System.out.println("Proceso de analisis lexico finalizado.");
    }
}