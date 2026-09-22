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

    //Alta para ID, Cadenas y Tokens sin valor numérico inicial
    public RegistroTS alta(String lexema, int tipoToken, int nroLinea) {
        return alta(lexema, tipoToken, nroLinea, 0.0);
    }

    //Alta para constantes numericas
    public RegistroTS alta(String lexema, int tipoToken, int nroLinea, double valorNum) {
        if (!tabla.containsKey(lexema)) {
            RegistroTS registro = new RegistroTS(lexema, tipoToken, nroLinea, valorNum);
            tabla.put(lexema, registro);
            return registro;
        }
        return tabla.get(lexema);
    }

    public void imprimirTabla() {
        String lineaSeparadora = "-----------------------------------------------------------------------------------";
        String lineaEncabezado  = "===================================================================================";

        System.out.println("\n" + lineaEncabezado);
        System.out.println("                         CONTENIDO DE LA TABLA DE SÍMBOLOS                         ");
        System.out.println(lineaEncabezado);
        
        System.out.printf("%-25s | %-10s | %-6s | %-6s | %-12s%n", "LEXEMA", "TIPO TOKEN", "LÍNEA", "LONG", "VALOR");
        System.out.println(lineaSeparadora);

        for (RegistroTS reg : tabla.values()) {
            String lexemaLimpio = reg.getLexema()
                                    .replaceAll("\r?\n", "\\\\n")
                                    .replaceAll("\t", "\\\\t");

            // Longitud del lexema (para todos los registros)
            int longitud = reg.getLexema().length();
            
            // Valor numérico (solo para constantes)
            String valorStr = "-";

            if (reg.getTipoToken() == 258 || reg.getTipoToken() == 259) {
                if (reg.getValorNum() != 0.0) {
                    valorStr = String.format("%.2f", reg.getValorNum());
                } else {
                    try {
                        String numPart = lexemaLimpio.split("\\$")[0]; // Extrae la parte numérica antes del '$'
                        double val = Double.parseDouble(numPart);
                        valorStr = String.format("%.2f", val);
                    } catch (Exception e) {
                        valorStr = String.format("%.2f", reg.getValorNum());
                    }
                }
            }

            System.out.printf("%-25.25s | %-10d | %-6d | %-6d | %-12s%n", 
                    lexemaLimpio, 
                    reg.getTipoToken(), 
                    reg.getNroLinea(), 
                    longitud,
                    valorStr);
        }
        System.out.println(lineaEncabezado + "\n");
    }
}