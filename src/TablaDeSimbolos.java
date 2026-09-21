import java.util.HashMap;
import java.util.HashSet;

public class TablaDeSimbolos {
    private HashMap<String, RegistroTS> tabla;
    private HashSet<String> palabrasReservadas;

    public TablaDeSimbolos() {
        this.tabla = new HashMap<>();
        this.palabrasReservadas = new HashSet<>();
        cargarPalabrasReservadas();
    }

    private void cargarPalabrasReservadas() {
        // Agregar palabras reservadas al conjunto
        palabrasReservadas.add("if");
        palabrasReservadas.add("else");
        palabrasReservadas.add("end_if");
        palabrasReservadas.add("begin");
        palabrasReservadas.add("end");
        palabrasReservadas.add("pout");
        palabrasReservadas.add("ret");
        palabrasReservadas.add("class");
        palabrasReservadas.add("function");
        // Agregar más palabras reservadas segun el tema asignado
        palabrasReservadas.add("ushortint");
        palabrasReservadas.add("doublef");
    }

    public boolean esPalabraReservada(String lexema) {
        return palabrasReservadas.contains(lexema.toLowerCase());
    }

    public RegistroTS buscar(String lexema) {
        return tabla.get(lexema);
    }

    public RegistroTS alta(String lexema, int tipoToken, int nroLinea) {
        if (!tabla.containsKey(lexema)) {
            RegistroTS registro = new RegistroTS(lexema, tipoToken, nroLinea);
            tabla.put(lexema, registro);
            return registro;
        }
        return tabla.get(lexema);
    }

    public void imprimirTabla() {
        System.out.println("\n=======================================================");
        System.out.println(" CONTENIDO DE LA TABLA DE SÍMBOLOS "); 
        System.out.println("======================================================="); 
        System.out.printf("%-25s | %-12s | %-8s | %-10s\\n", "LEXEMA", "TIPO TOKEN", "LÍNEA", "VALOR/LONG"); 
        System.out.println("-------------------------------------------------------");
        for (RegistroTS reg : tabla.values()) {
            System.out.printf("%-25s | %-12d | %-8d | %-10.2f\\n", reg.getLexema(), reg.getTipoToken(), reg.getNroLinea(), reg.getValorNum());
        }
        System.out.println("=======================================================\n");
    }
}