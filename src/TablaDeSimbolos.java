import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TablaDeSimbolos {
    private final HashMap<String, RegistroTS> tabla;
    private final HashSet<String> palabrasReservadas;

    public TablaDeSimbolos() {
        this.tabla = new HashMap<>();
        this.palabrasReservadas = new HashSet<>();
        cargarPalabrasReservadas();
    }

    private void cargarPalabrasReservadas() {
        // TP1: palabras reservadas generales (se reconocen sin distinguir mayúsculas/minúsculas)
        palabrasReservadas.add("if");
        palabrasReservadas.add("else");
        palabrasReservadas.add("end_if");
        palabrasReservadas.add("begin");
        palabrasReservadas.add("end");
        palabrasReservadas.add("pout");
        palabrasReservadas.add("ret");
        palabrasReservadas.add("class");
        palabrasReservadas.add("function");
        // Palabras reservadas generales del lenguaje Tp2
        palabrasReservadas.add("pout");
        palabrasReservadas.add("ret");

        // Tipos de datos del Grupo 34 (TP1)
        palabrasReservadas.add("ushortint"); // Tema 2
        palabrasReservadas.add("doublef");   // Tema 8

        // Temas particulares del Grupo 34 (TP2)
        palabrasReservadas.add("while");     // Tema 11
        palabrasReservadas.add("repeat");    // Tema 11
        palabrasReservadas.add("typedef");   // Tema 23
        palabrasReservadas.add("private");   // Tema 27
        palabrasReservadas.add("friend");    // Tema 27
        palabrasReservadas.add("extends");   // Tema 30
    }

    public boolean esPalabraReservada(String lexema) {
        return lexema != null && palabrasReservadas.contains(lexema.toLowerCase(java.util.Locale.ROOT));
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

    // Rango final de DOUBLEF segun TP1: min < |x| < max, admitiendo 0.0.
    // El lexico acepta ademas el limite positivo para que el parser pueda
    // aplicar el signo y volver a validar el valor definitivo (TP2, punto c).
    public boolean verificarRangoDoublef(double val) {
        if (val == 0.0) 
            return true;
        double absVal = Math.abs(val);
        double minVal = 2.2250738585072014e-308;
        double maxVal = 1.7976931348623157e+308;
        return (absVal > minVal && absVal < maxVal);
    }

    public boolean verificarRangoUshortint(double val) {
        return val >= 0.0 && val <= 255.0;
    }

    public boolean verificarRangoUshortint(String lexema) {
        try { // Removemos el sufijo "$us" para obtener solo la parte numérica
            String numeroLimpio = lexema.toLowerCase(java.util.Locale.ROOT).replace("$us", "").trim();
            long val = Long.parseLong(numeroLimpio);
            return val >= 0 && val <= 255;
        }
        catch (NumberFormatException _) {
            return false; // Si no es un número válido o excede los límites de parseo
        }
    }

    public RegistroTS registrarConstanteNegativa(String lexemaPositivo, int nroLinea, double valorNum) {
        String lexemaNegativo = "-" + lexemaPositivo;
        RegistroTS regNeg = new RegistroTS(lexemaNegativo, Parser.CTE_DOUBLEF,
                "CTE_DOUBLEF", nroLinea, valorNum);
        tabla.put(lexemaNegativo, regNeg);
        return regNeg;
    }

    // ==========================================
    // METODOS SEMANTICOS TP3 (TEMAS 29 Y 30)
    // ==========================================

    // TEMA 30: Herencia Múltiple con Desambiguado Opción 1.
    // Busca un miembro navegando en el orden de 'clasesHeredadas'.
    public RegistroTS buscarEnHerencia(String nombreClase, String nombreMiembro) {
        RegistroTS regClase = buscar(nombreClase);
        if (regClase == null) {
            return null;
        }
        for (RegistroTS atrib : regClase.getListaAtributos()) {
            if (atrib.getLexema().equals(nombreMiembro) || atrib.getLexema().endsWith("@" + nombreMiembro)) {
                return atrib;
            }
        }
        for (String clasePadre : regClase.getClasesHeredadas()) {
            RegistroTS hallado = buscarEnHerencia(clasePadre, nombreMiembro);
            if (hallado != null) {
                return hallado;
            }
        }
        return null;
    }

    // TEMA 29: Acceso Posicional a Atributos obj[i].
    public RegistroTS obtenerAtributoPosicional(String nombreClase, int indice) {
        RegistroTS regClase = buscar(nombreClase);
        if (regClase == null) {
            return null;
        }
        List<RegistroTS> todosLosAtributos = obtenerTodosLosAtributosHeredados(nombreClase);
        if (indice >= 0 && indice < todosLosAtributos.size()) {
            return todosLosAtributos.get(indice);
        }
        return null;
    }

    private List<RegistroTS> obtenerTodosLosAtributosHeredados(String nombreClase) {
        List<RegistroTS> lista = new ArrayList<>();
        RegistroTS regClase = buscar(nombreClase);
        if (regClase != null) {
            for (String padre : regClase.getClasesHeredadas()) {
                lista.addAll(obtenerTodosLosAtributosHeredados(padre));
            }
            lista.addAll(regClase.getListaAtributos());
        }
        return lista;
    }

    public boolean esHerenciaCircular(String clavePadre, String claveActual) { 
        if (clavePadre == null || claveActual == null) { 
            return false; 
        } 
        // Caso 1: Herencia directa (ej. CLASS A EXTENDS A) 
        if (clavePadre.equals(claveActual)) { 
            return true; 
        } 
        // Caso 2: Herencia indirecta (ej. CLASS B EXTENDS A, y luego CLASS A EXTENDS B) 
        RegistroTS regPadre = buscar(clavePadre); 
        if (regPadre != null) { 
            for (String claveAncestral : regPadre.getClasesHeredadas()) { 
                if (esHerenciaCircular(claveAncestral, claveActual)) { 
                    return true; // Se encontró un ciclo en la jerarquía 
                } 
            } 
        } 
        return false; 
    }

    public Map<String, RegistroTS> getTabla() {
        return new HashMap<>(tabla);
    }

    public boolean existeSimbolo(String claveMangled) { 
        return buscar(claveMangled) != null; 
    }
   
    public RegistroTS registrarVariable(String claveMangled, String tipoDato, String uso) { 
        RegistroTS reg = buscar(claveMangled); 
        if (reg == null) { 
            reg = new RegistroTS(claveMangled); 
            tabla.put(claveMangled, reg); 
        } 
        reg.setTipoToken(tipoDato); // ej. "ushortint", "doublef", etc. 
        reg.setUso(uso); // "variable" o "parametro" 
        return reg;
    }

    public RegistroTS registrarClase(String claveMangled, String uso) { 
        RegistroTS reg = buscar(claveMangled); 
        if (reg == null) { 
            reg = new RegistroTS(claveMangled); 
            tabla.put(claveMangled, reg); 
        } 
        reg.setTipoToken("CLASS"); 
        reg.setUso(uso); // "nombre_clase" 
        return reg; 
    }

    public RegistroTS registrarFuncion(String claveMangled, String tipoRetorno, String uso, List<String> tiposParametros) {
        RegistroTS reg = buscar(claveMangled);
        if (reg == null) {
            reg = new RegistroTS(claveMangled);
            tabla.put(claveMangled, reg);
        }
        reg.setTipoToken(tipoRetorno);
        reg.setUso(uso);         // "nombre_funcion" o "metodo"
        if (tiposParametros != null){
            for (String tipo : tiposParametros){
                reg.agregarParametro(tipo);
            }
        }
        return reg;
    }

    public RegistroTS registrarTipoPersonalizado(String claveMangled, String uso, List<RegistroTS> valoresPermitidos) {
        RegistroTS reg = buscar(claveMangled);
        if (reg == null) {
            reg = new RegistroTS(claveMangled);
            tabla.put(claveMangled, reg);
        }
        reg.setTipoToken("typedef");
        reg.setUso(uso);         // "nombre_tipo"
        if (valoresPermitidos != null){
            for (RegistroTS val : valoresPermitidos){
                reg.agregarAtributo(val);
            }
        }
        return reg;
    }

    public boolean tienePermisoAcceso(RegistroTS regClaseDuenia, RegistroTS regMiembro, String ambitoEmisor) { 
        // Si el miembro es público, cualquier ámbito tiene acceso 
        if (!regMiembro.esPrivado()) { 
            return true; 
        }
        if (regClaseDuenia == null){
            return false;
        }
        String nombreClaseDuenia = regClaseDuenia.getLexema(); 
        // Regla a: Si la llamada ocurre DENTRO de un método de la misma clase dueña -> PERMITIDO 
        if (ambitoEmisor.contains(":" + nombreClaseDuenia)) { 
            return true; 
        } 
        // Regla b: Verificar si la clase emisor o función emisora es AMIGA de la clase dueña 
        List<String> amigos = regClaseDuenia.getListaAmigos();
        if (amigos != null) {
            for (String amigo : amigos){
                // Si el ámbito emisor pertenece a una clase amiga o a una función amiga declarada
                if (ambitoEmisor.contains(":" + amigo) || ambitoEmisor.endsWith(":" + amigo)) {
                    return true;
                }
            }
        }
        // Si es privado y no es de la misma clase ni de una clase/función amiga -> DENEGADO
        return false;
    }

    public boolean esAsignable(RegistroTS reg) { 
        if (reg == null) 
            return false; 
        String uso = reg.getUso(); 
        return "variable".equalsIgnoreCase(uso) || "parametro".equalsIgnoreCase(uso) || "objeto".equalsIgnoreCase(uso) || "atributo".equalsIgnoreCase(uso); 
    }

    // ==========================================
    // INFORMES Y VISUALIZACION
    // ==========================================

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
            if (reg.getTipoToken().equals("CTE_ENTERA") || reg.getTipoToken().equals("USHORTINT")) {
                valorStr = String.valueOf((long) v); // Imprime "250" sin ".0"
            } else if (Math.abs(v) >= 1e6 || (Math.abs(v) < 1e-3 && v != 0.0)) {
                valorStr = String.format("%e", v);
            } else {
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