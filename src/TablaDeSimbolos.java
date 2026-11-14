import java.util.HashMap;
import java.util.HashSet;

public class TablaDeSimbolos {
    private final HashMap<String, RegistroTS> tabla;
    private final HashSet<String> palabrasReservadas;

    public TablaDeSimbolos() {
        this.tabla = new HashMap<>();
        this.palabrasReservadas = new HashSet<>();
        cargarPalabrasReservadas();
    }

    private void cargarPalabrasReservadas() {
        // Palabras reservadas generales del lenguaje
        palabrasReservadas.add("if");
        palabrasReservadas.add("else");
        palabrasReservadas.add("end_if");
        palabrasReservadas.add("begin");
        palabrasReservadas.add("end");
        palabrasReservadas.add("pout");
        palabrasReservadas.add("class");
        palabrasReservadas.add("function");
        // Palabras reservadas generales del lenguaje Tp2
        palabrasReservadas.add("pout");
        palabrasReservadas.add("ret");

        // Tipos de datos correspondientes al Grupo 34 (TP1)
        // Tema 2
        palabrasReservadas.add("ushortint");
        // Tema 8
        palabrasReservadas.add("doublef");

        // Control y temas particulares asignados al Grupo 34 (TP2)
        // Tema 11
        palabrasReservadas.add("while");
        palabrasReservadas.add("repeat");
        // Tema 23
        palabrasReservadas.add("typedef");
        // Tema 27
        palabrasReservadas.add("private");
        palabrasReservadas.add("friend");
        // Tema 30
        palabrasReservadas.add("extends");
    }

    public boolean esPalabraReservada(String lexema) {
        if (lexema == null) 
            return false;
        return palabrasReservadas.contains(lexema.toLowerCase());
    }

    public RegistroTS buscar(String lexema) {
        return tabla.get(lexema);
    }

    //Alta para ID, Cadenas y Tokens sin valor numérico inicial
    public RegistroTS alta(String lexema,int idToken, String tipoToken, int nroLinea) {
        return alta(lexema, idToken, tipoToken, nroLinea, 0.0);
    }

    //Alta para constantes numericas
    public RegistroTS alta(String lexema, int idToken, String tipoToken, int nroLinea, double valorNum) {
        if (!tabla.containsKey(lexema)) {
            RegistroTS registro = new RegistroTS(lexema, idToken, tipoToken, nroLinea, valorNum);
            tabla.put(lexema, registro);
            return registro;
        }
        return tabla.get(lexema);
    }

    public boolean verificarRangoDoublef(double val) {
        if (val == 0.0) 
            return true;
        double absVal = Math.abs(val);
        double minVal = 2.2250738585072014e-308;
        double maxVal = 1.7976931348623157e+308;
        return (absVal >= minVal && absVal <= maxVal);
    }

    public boolean verificarRangoUshortint(String lexema) { 
        try { // Removemos el sufijo "$us" para obtener solo la parte numérica 
            String numeroLimpio = lexema.toLowerCase().replace("$us", "").trim(); 
            long val = Long.parseLong(numeroLimpio); 
            return val >=0 && val <= 255; } 
        catch (NumberFormatException _) { 
            return false; // Si no es un número válido o excede los límites de parseo 
            } 
    }

    public RegistroTS registrarConstanteNegativa(String lexemaPositivo, int nroLinea, double valorNum) {
        String lexemaNegativo = "-" + lexemaPositivo;
        tabla.remove(lexemaPositivo);
        RegistroTS regNeg = new RegistroTS(lexemaNegativo, 259, "DOUBLEF_VAL", nroLinea, valorNum);
        tabla.put(lexemaNegativo, regNeg);
        return regNeg;
    }

    public void imprimirTabla() {
        String lineaSeparadora = "-----------------------------------------------------------------------------------";
        String lineaEncabezado  = "===================================================================================";

        System.out.println("\n" + lineaEncabezado);
        System.out.println("                         CONTENIDO DE LA TABLA DE SÍMBOLOS                         ");
        System.out.println(lineaEncabezado);
        
        System.out.printf("%-30s | %-10s | %-6s | %-6s | %-12s | %-10s | %-10s%n", 
                        "LEXEMA", "TOKEN", "LÍNEA", "LONG", "VALOR", "TIPO", "USO"); 
        System.out.println(lineaSeparadora);

        for (RegistroTS reg : tabla.values()) { 
            imprimirRegistro(reg); 
        } 
        System.out.println(lineaEncabezado + "\n"); 
    }

    private void imprimirRegistro(RegistroTS reg) {
        // Limpieza de saltos de línea y tabulaciones en el lexema 
        String lexemaLimpio = (reg.getLexema() != null) 
            ? reg.getLexema().replace("\r\n", "\\n").replace("\n", "\\n").replace("\t", "\\t")
            : "";

        // Formateo del valor numérico (solo para constantes o cuando corresponda) 
        String valorStr = "-"; 
        if (reg.getTipoToken() != null && (reg.getTipoToken().contains("CTE") || reg.getValorNum() != 0.0)) { 
            double v = reg.getValorNum(); 
            if (Math.abs(v) >= 1e6 || (Math.abs(v) < 1e-3 && v != 0.0)) { 
                valorStr = String.format("%e", v); 
            } 
            else { 
                valorStr = String.valueOf(v); 
            } 
        }
        
        // Atributos semánticos asignados en TP2 
        String tipoStr = (reg.getTipoToken() != null) ? reg.getTipoToken() : "-"; 
        String usoStr = (reg.getUso() != null) ? reg.getUso() : "-";

        // Impresión formateada  
        System.out.printf("%-30s | %-10d | %-6d | %-6d | %-12s | %-10s | %-10s%n", 
                    lexemaLimpio, reg.getIdToken(), reg.getNroLinea(), reg.getLongitud(), valorStr, tipoStr, usoStr);
    }
}