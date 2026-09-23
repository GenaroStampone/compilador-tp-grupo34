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
        // Palabras reservadas generales del lenguaje
        palabrasReservadas.add("if");
        palabrasReservadas.add("then");
        palabrasReservadas.add("else");
        palabrasReservadas.add("end_if");
        palabrasReservadas.add("begin");
        palabrasReservadas.add("end");
        palabrasReservadas.add("pout");
        palabrasReservadas.add("ret");
        palabrasReservadas.add("class");
        palabrasReservadas.add("function");

        // Tipos de datos correspondientes al Grupo 34
        palabrasReservadas.add("ushortint");
        palabrasReservadas.add("doublef");

        // Control y temas particulares asignados al Grupo 34 (TP2)
        palabrasReservadas.add("while");
        palabrasReservadas.add("repeat");
        palabrasReservadas.add("typedef");
        palabrasReservadas.add("private");
        palabrasReservadas.add("friend");
        palabrasReservadas.add("extends");

        // Conversiones explícitas correspondientes al Grupo 34 (Temas 8 y 2)
        palabrasReservadas.add("todf");
        palabrasReservadas.add("tous");
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

    public boolean verificarRangoDoublef(double val) {
        if (val == 0.0) return true;
        double absVal = Math.abs(val);
        double minVal = 2.2250738585072014e-308;
        double maxVal = 1.7976931348623157e+308;
        return (absVal >= minVal && absVal <= maxVal);
    }

    public RegistroTS registrarConstanteNegativa(String lexemaPositivo, int nroLinea, double valorNum) {
        String lexemaNegativo = "-" + lexemaPositivo;
        tabla.remove(lexemaPositivo);
        RegistroTS regNeg = new RegistroTS(lexemaNegativo, 259 /* DOUBLEF_VAL */, nroLinea, valorNum);
        tabla.put(lexemaNegativo, regNeg);
        return regNeg;
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

            if (reg.getTipoToken() == 258) {
                valorStr = String.valueOf((int) reg.getValorNum());
            } else if (reg.getTipoToken() == 259) {
                double v = reg.getValorNum();
                if (Math.abs(v) >= 1e6 || (Math.abs(v) < 1e-3 && v != 0.0)) {
                    valorStr = String.format("%e", v);
                } else {
                    valorStr = String.valueOf(v);
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